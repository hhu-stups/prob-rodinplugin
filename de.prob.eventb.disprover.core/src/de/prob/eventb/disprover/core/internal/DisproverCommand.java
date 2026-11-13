package de.prob.eventb.disprover.core.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eventb.core.IEventBProject;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IProofMonitor;
import org.osgi.service.prefs.Preferences;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.core.Animator;
import de.prob.core.ProBCommandJob;
import de.prob.core.command.ClearMachineCommand;
import de.prob.core.command.CommandException;
import de.prob.core.command.ComposedCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.command.SetPreferenceCommand;
import de.prob.core.command.SetPreferencesCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.eventb.disprover.core.DisproverReasoner;
import de.prob.eventb.disprover.core.DisproverStatistics;
import de.prob.eventb.disprover.core.command.DisproverLoadCommand;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * The DisproverCommand takes two sets of ASTs (one for the machine and a list
 * for the contexts) and tries to set them up with ProB. If setup is possible,
 * the arguments from that operation are joined with the provided variables and
 * returned as an {@link ICounterExample}.
 * <p>
 * 
 * This command is probably not useful without {@link DisproverReasoner}, which
 * calls it.
 * 
 * @author jastram
 */
public class DisproverCommand implements IComposableCommand {

	private static final String RESULT = "Result";

	/**
	 * ProB may solve several constraints with the given timeout (e.g. for the
	 * selected and for all hypotheses), so give it some more time before
	 * giving up on it
	 */
	private static final int TIMEOUT_MULTIPLIER = 5;
	private static final long DEADLINE_MARGIN_MS = 30000;

	private CounterExample counterExample;
	private final Set<Predicate> allHypotheses;
	private final Set<Predicate> selectedHypotheses;
	private final Predicate goal;
	private final int timeout;
	private final Boolean exportPO;
	// set by the job thread, read by the prover thread
	private volatile boolean interrupted = false;

	protected DisproverCommand(Set<Predicate> allHypotheses,
			Set<Predicate> selectedHypotheses, Predicate goal, int timeout,
			Boolean exportPO) {
		this.allHypotheses = allHypotheses;
		this.selectedHypotheses = selectedHypotheses;
		this.goal = goal;
		this.timeout = timeout;
		this.exportPO = exportPO;
	}

	public static ICounterExample disprove(Animator animator,
			IEventBProject project, Set<Predicate> allHypotheses,
			Set<Predicate> selectedHypotheses, Predicate goal, int timeoutFactor,
			AEventBContextParseUnit context, IProofMonitor pm)
			throws InterruptedException {
		return disprove(animator, project, allHypotheses, selectedHypotheses,
				goal, timeoutFactor, context, pm, null);
	}

	/**
	 * @param options
	 *            additional options for cbc_disprove/6, e.g.
	 *            <code>disprover_option(wd_prover_timeout(50))</code>;
	 *            <code>null</code> for the
	 *            normal disprover
	 */
	public static ICounterExample disprove(Animator animator,
			IEventBProject project, Set<Predicate> allHypotheses,
			Set<Predicate> selectedHypotheses, Predicate goal, int timeoutFactor,
			AEventBContextParseUnit context, IProofMonitor pm,
			List<PrologTerm> options) throws InterruptedException {
		return disprove(animator, project, allHypotheses, selectedHypotheses,
				goal, timeoutFactor, context, pm, options, null);
	}

	/**
	 * @param proofObligation
	 *            the name of the proof obligation, e.g.
	 *            <code>inv1/INV (M1)</code>, for the Progress view and the
	 *            {@link DisproverStatistics}; may be <code>null</code>
	 */
	public static ICounterExample disprove(Animator animator,
			IEventBProject project, Set<Predicate> allHypotheses,
			Set<Predicate> selectedHypotheses, Predicate goal, int timeoutFactor,
			AEventBContextParseUnit context, IProofMonitor pm,
			List<PrologTerm> options, String proofObligation)
			throws InterruptedException {
		final Preferences prefNode = preferences();
		final List<IComposableCommand> commands = setupCommands(animator,
				project, context);

		final int timeout = timeoutFactor * prefNode.getInt("timeout", 1000);
		final boolean exportPO = prefNode.getBoolean("exportpo", false);
		final DisproverCommand disprove = options == null ? new DisproverCommand(
				allHypotheses, selectedHypotheses, goal, timeout, exportPO)
				: new DisproverCommandWithOptions(allHypotheses,
						selectedHypotheses, goal, timeout, exportPO, options);

		// local: the auto prover and an interactive call may run concurrently
		commands.add(disprove);
		final boolean wdProver = isWdProver(options);
		// the statistics of the provers after the call, if probcli has them;
		// they name the prover if it is not the normal disprover
		String statisticsName = proofObligation == null ? "(unknown proof obligation)"
				: proofObligation;
		if (wdProver)
			statisticsName += " [WD prover]";
		else if (timeoutFactor != 1)
			statisticsName += " [timeout x" + timeoutFactor + "]";
		final StatisticsSlot statistics = new StatisticsSlot(statisticsName,
				disprove);
		commands.add(statistics);
		final ComposedCommand composed = new ComposedCommand(commands);

		// the name shown e.g. in the Progress view
		final String jobName = (wdProver ? "Running ProB WD Prover"
				: "Running ProB Disprover")
				+ (proofObligation == null ? "" : " on " + proofObligation);
		final Job job = new ProBCommandJob(jobName, animator, composed) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				// asks probcli once whether it has the statistics; an older
				// one would make the whole command fail
				statistics.supported = DisproverStatistics
						.checkSupported(animator);
				return super.run(monitor);
			}
		};
		job.setUser(true);
		job.schedule();

		// the job may first have to wait for another command on the animator
		final long deadline = System.currentTimeMillis() + (long) timeout
				* TIMEOUT_MULTIPLIER + DEADLINE_MARGIN_MS;
		while (job.getResult() == null && 
		       (pm == null || !pm.isCanceled())) { // treat case IProofMonitor is null; done by Rodin 3.5RC at startup
			if (System.currentTimeMillis() > deadline) {
				// ProB does not answer, e.g. it hangs while loading;
				// cancelling sends it a user interrupt
				job.cancel();
				return new CounterExample(false, true, false);
			}
			Thread.sleep(200);
		}

		if (pm != null && pm.isCanceled()) {
			job.cancel(); // also interrupts ProB
			throw new InterruptedException("ProB: cancelled");
		}

		if (disprove.interrupted) {
			// e.g. after the deadline of a previous call was exceeded
			throw new InterruptedException("ProB: interrupted");
		}
		return disprove.getResult();

	}

	/**
	 * @return whether the options are those of the WD prover, i.e. without
	 *         constraint solving
	 */
	private static boolean isWdProver(final List<PrologTerm> options) {
		if (options == null)
			return false;
		for (PrologTerm option : options)
			if (option.hasFunctor("disprover_option", 1)
					&& option.getArgument(1).hasFunctor(
							"no_constraint_solving", 0))
				return true;
		return false;
	}

	private static Preferences preferences() {
		return Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node("prob_disprover_preferences");
	}

	/**
	 * @return the commands that set ProB up for the disprover: its
	 *         preferences and the context of the proof obligation
	 */
	private static List<IComposableCommand> setupCommands(
			final Animator animator, final IEventBProject project,
			final AEventBContextParseUnit context) {
		final Preferences prefNode = preferences();

		final ClearMachineCommand clear = new ClearMachineCommand();

		final SetPreferencesCommand setPrefs = SetPreferencesCommand
				.createSetPreferencesCommand(animator);

		// set clpfd and chr preference
		final SetPreferenceCommand setCLPFD = new SetPreferenceCommand("CLPFD",
				Boolean.toString(prefNode.getBoolean("clpfd", true)));
		final SetPreferenceCommand setCHR = new SetPreferenceCommand("CHR",
				Boolean.toString(prefNode.getBoolean("chr", true)));
		final SetPreferenceCommand setCSE = new SetPreferenceCommand("CSE",
				Boolean.toString(prefNode.getBoolean("cse", false)));
		final SetPreferenceCommand setSMT = new SetPreferenceCommand(
				"SMT_SUPPORTED_INTERPRETER", Boolean.toString(prefNode
						.getBoolean("smt", false)));
		final SetPreferenceCommand setCSEPred = new SetPreferenceCommand(
				"CSE_PRED", Boolean.toString(prefNode.getBoolean("cse", false)));
		// default must match the one shown in DisproverPreferences
		final SetPreferenceCommand setDoubleEval = new SetPreferenceCommand(
				"DOUBLE_EVALUATION", Boolean.toString(prefNode.getBoolean(
						"doubleeval", true)));

		DisproverLoadCommand load = new DisproverLoadCommand(project, context);

		StartAnimationCommand start = new StartAnimationCommand();
		return new ArrayList<IComposableCommand>(Arrays.<IComposableCommand> asList(
				clear, setPrefs, setCLPFD, setCHR, setSMT, setCSE, setCSEPred,
				setDoubleEval, load, start));
	}

	/**
	 * Writes ProB's formula tree of the hypotheses implying the goal,
	 * evaluated in a counter-example found before, as a dot file, using
	 * <code>cbc_disprove_counter_example_dot/5</code>. Sets ProB up for the
	 * proof obligation again, as the animator may have been used for others
	 * meanwhile.
	 *
	 * @param bindings
	 *            the counter-example, see {@link ICounterExample#getBindings()}
	 */
	public static void writeCounterExampleDot(final Animator animator,
			final IEventBProject project,
			final AEventBContextParseUnit context, final Predicate goal,
			final Set<Predicate> hypotheses, final PrologTerm bindings,
			final File dotFile) throws ProBException {
		final int timeout = preferences().getInt("timeout", 1000);
		final List<IComposableCommand> commands = setupCommands(animator,
				project, context);
		commands.add(new IComposableCommand() {
			@Override
			public void writeCommand(final IPrologTermOutput pto) {
				pto.openTerm("cbc_disprove_counter_example_dot");
				translatePredicate(pto, goal);
				pto.openList();
				for (Predicate p : hypotheses)
					translatePredicate(pto, p);
				pto.closeList();
				pto.printTerm(bindings);
				pto.printNumber(timeout);
				pto.printAtom(dotFile.getAbsolutePath());
				pto.closeTerm();
			}

			@Override
			public void processResult(
					final ISimplifiedROMap<String, PrologTerm> result) {
			}
		});
		animator.execute(new ComposedCommand(commands));
	}

	private ICounterExample getResult() {
		return counterExample;
	}

	/**
	 * Gets the {@link DisproverStatistics} after the disprover call, in the
	 * same composed command; whether probcli supports them is only known
	 * when the job runs.
	 */
	private static final class StatisticsSlot implements IComposableCommand {
		private final String proofObligation;
		private final DisproverCommand disprove;
		volatile boolean supported;
		private IComposableCommand command;

		StatisticsSlot(final String proofObligation,
				final DisproverCommand disprove) {
			this.proofObligation = proofObligation;
			this.disprove = disprove;
		}

		@Override
		public void writeCommand(final IPrologTermOutput pto)
				throws CommandException {
			command = DisproverStatistics.command(supported, proofObligation,
					() -> outcome(disprove));
			command.writeCommand(pto);
		}

		@Override
		public void processResult(
				final ISimplifiedROMap<String, PrologTerm> bindings)
				throws CommandException {
			if (command != null)
				command.processResult(bindings);
		}
	}

	/** @return a short description of the result, for the statistics */
	private static String outcome(final DisproverCommand disprove) {
		final ICounterExample ce = disprove.counterExample;
		if (disprove.interrupted)
			return "interrupted";
		if (ce == null)
			return "error";
		if (ce.counterExampleFound())
			return ce.onlySelectedHypotheses() ? "counter-example for the selected hypotheses"
					: "counter-example";
		if (ce.isProof())
			return ce.doubleCheckFailed() ? "proven (contradiction in hypotheses)"
					: "proven";
		if (ce.timeoutOccured())
			return "time-out";
		final String reason = ce.getReason();
		return reason == null || reason.isEmpty() ? "not proven"
				: "not proven (" + reason + ")";
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("cbc_disprove");

		translatePredicate(pto, goal);
		pto.openList();
		for (Predicate p : this.allHypotheses) {
			translatePredicate(pto, p);
		}
		pto.closeList();
		pto.openList();
		for (Predicate p : this.selectedHypotheses) {
			translatePredicate(pto, p);
		}
		pto.closeList();
		pto.printNumber(timeout);
		pto.openList();
		writeOptions(pto);
		pto.closeList();
		pto.printVariable(RESULT);
		pto.closeTerm();
	}

	/**
	 * Writes the elements of the options list of cbc_disprove/6. The
	 * preferences are set with separate commands, so by default only the
	 * export option is written.
	 */
	protected void writeOptions(final IPrologTermOutput pto) {
		if (this.exportPO) {
			pto.openTerm("disprover_option");
			pto.openTerm("export_po_as_machine");// Note: other valid options : wd_prover_timeout(T), unsat_core,...
			pto.printAtom("/tmp/ProB_Rodin_PO_SelectedHyps.mch");
			// TO DO: provide user preference path; TO DO: we could runProBClassic(prob_location, tmp);
			pto.closeTerm();
			pto.closeTerm();
		}
	}

	private static void translatePredicate(final IPrologTermOutput pto, Predicate pred) {
		TranslationVisitor v = new TranslationVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {

		PrologTerm term = bindings.get(RESULT);

		counterExample = null;

		if ("time_out".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, true, false);
		}
		if ("interrupted".equals(term.getFunctor())) {
			// cancelled by the user or after the deadline: not an error to
			// report in a dialog
			interrupted = true;
			return;
		}
		if ("no_solution_found".equals(term.getFunctor())) {
			PrologTerm reason = term.getArgument(1);
			if (reason.hasFunctor("clpfd_overflow", 0)) {
				counterExample = new CounterExample(false, false,
						"CLPFD Integer Overflow");
			} else if (reason.hasFunctor("unfixed_deferred_sets", 0)) {
				counterExample = new CounterExample(false, false,
						"unfixed deferred sets in predicate");
			} else {
				counterExample = new CounterExample(false, false,
						reason.toString());
			}
		}

		if ("contradiction_found".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, false, false);
			counterExample.setProof(true);
		}

		if ("contradiction_in_hypotheses".equals(term.getFunctor())) {
			counterExample = new CounterExample(false, false, false);
			counterExample.setProof(true);
			counterExample.setDoubleCheckFailed(true);
		}

		if ("solution".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false, false);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);
			counterExample.setBindings(vars);

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

		if ("solution_on_selected_hypotheses".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false, true);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);
			counterExample.setBindings(vars);

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

		if (counterExample == null) {
			throw new CommandException(
					"Internal Error in ProB. Unexpected result:"
							+ term.toString());
		}
	}

}
