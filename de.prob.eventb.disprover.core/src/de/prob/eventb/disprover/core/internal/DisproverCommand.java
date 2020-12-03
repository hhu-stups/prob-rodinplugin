package de.prob.eventb.disprover.core.internal;

import java.util.Set;

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
import de.prob.eventb.disprover.core.command.DisproverLoadCommand;
import de.prob.eventb.translator.internal.TranslationVisitor;
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

	private CounterExample counterExample;
	private final Set<Predicate> allHypotheses;
	private final Set<Predicate> selectedHypotheses;
	private final Predicate goal;
	private final int timeout;
	private final Boolean exportPO

	private static ComposedCommand composed;

	public DisproverCommand(Set<Predicate> allHypotheses,
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
		Preferences prefNode = Platform.getPreferencesService().getRootNode()
				.node(InstanceScope.SCOPE).node("prob_disprover_preferences");

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
		final SetPreferenceCommand setDoubleEval = new SetPreferenceCommand(
				"DOUBLE_EVALUATION", Boolean.toString(prefNode.getBoolean(
						"doubleeval", true)));

		DisproverLoadCommand load = new DisproverLoadCommand(project, context);

		StartAnimationCommand start = new StartAnimationCommand();

		DisproverCommand disprove = new DisproverCommand(allHypotheses,
				selectedHypotheses, goal, 
				timeoutFactor * prefNode.getInt("timeout", 1000),
				prefNode.getBoolean("exportpo", false));

		composed = new ComposedCommand(clear, setPrefs, setCLPFD, setCHR,
				setSMT, setCSE, setCSEPred, setDoubleEval, load, start,
				disprove);

		final Job job = new ProBCommandJob("Disproving", animator, composed);
		job.setUser(true);
		job.schedule();

		while (job.getResult() == null && 
		       (pm == null || !pm.isCanceled())) { // treat case IProofMonitor is null; done by Rodin 3.5RC at startup
			Thread.sleep(200);
		}

		if (pm != null && pm.isCanceled()) {
			job.cancel();
			throw new InterruptedException();
		}

		return disprove.getResult();

	}

	private ICounterExample getResult() {
		return counterExample;
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
		if (this.exportPO) {
			pto.openList();
		    pto.openTerm("disprover_option");
		    pto.openTerm("export_po_as_machine");// Note: other valid options : wd_prover_timeout(T), unsat_core,...
			pto.printAtom("/tmp/ProB_Rodin_PO_SelectedHyps.mch");  
			             // TO DO: provide user preference path; TO DO: we could runProBClassic(prob_location, tmp);
		    pto.closeTerm();
		    pto.closeTerm();
			pto.closeList();
		} else {
			pto.emptyList(); // do not submit extra options because we set the
							 // preference above; we could transmit additional valid_disprover_option
		}
		pto.printVariable(RESULT);
		pto.closeTerm();
	}

	private void translatePredicate(final IPrologTermOutput pto, Predicate pred) {
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
			throw new CommandException("Interrupted");
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

			for (PrologTerm e : vars) {
				counterExample.addVar(e.getArgument(1).getFunctor(), e
						.getArgument(3).getFunctor());
			}
		}

		if ("solution_on_selected_hypotheses".equals(term.getFunctor())) {
			counterExample = new CounterExample(true, false, true);
			ListPrologTerm vars = (ListPrologTerm) term.getArgument(1);

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
