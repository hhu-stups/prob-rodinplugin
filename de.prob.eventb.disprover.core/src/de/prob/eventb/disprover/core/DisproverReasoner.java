package de.prob.eventb.disprover.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.core.Animator;
import de.prob.eventb.disprover.core.internal.DisproverCommand;
import de.prob.eventb.disprover.core.internal.ICounterExample;
import de.prob.eventb.disprover.core.translation.DisproverContextCreator;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.logging.Logger;
import de.prob.prolog.output.PrologTermStringOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.IntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

import org.eclipse.core.runtime.IStatus;
import org.eventb.core.IEventBProject;
import org.eventb.core.IPOSequent;
import org.eventb.core.ast.Predicate;
import org.eventb.core.seqprover.IConfidence;
import org.eventb.core.seqprover.IProofMonitor;
import org.eventb.core.seqprover.IProofRule;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.eventb.core.seqprover.IProverSequent;
import org.eventb.core.seqprover.IReasoner;
import org.eventb.core.seqprover.IReasonerInput;
import org.eventb.core.seqprover.IReasonerInputReader;
import org.eventb.core.seqprover.IReasonerInputWriter;
import org.eventb.core.seqprover.IReasonerOutput;
import org.eventb.core.seqprover.ProverFactory;
import org.eventb.core.seqprover.SerializeException;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinDBException;

public class DisproverReasoner implements IReasoner {

	static final String DISPROVER_CONTEXT = "disprover_context";

	private static final String DISPROVER_REASONER_NAME = "de.prob.eventb.disprover.core.disproverReasoner";

	private static final String TIMEOUT_FACTOR_KEY = "timeoutFactor";
	private static final String WD_PROVER_TIMEOUT_KEY = "wdProverTimeout";

	private final int timeoutFactor;

	/**
	 * whether the user started it, e.g. in the Proof Control view: then a
	 * counter-example is also shown in a notice; not for the auto-prover or
	 * when a proof is replayed
	 */
	private final boolean interactive;

	public DisproverReasoner() {
		this(1);
	}

	public DisproverReasoner(int timeoutFactor) {
		this(timeoutFactor, false);
	}

	public DisproverReasoner(int timeoutFactor, boolean interactive) {
		this.timeoutFactor = timeoutFactor;
		this.interactive = interactive;
	}

	@Override
	public String getReasonerID() {
		return DISPROVER_REASONER_NAME;
	}

	@Override
	public IReasonerOutput apply(final IProverSequent sequent,
			final IReasonerInput input, final IProofMonitor pm) {
		try {
			DisproverReasonerInput disproverInput = (DisproverReasonerInput) input;
			// when replaying a proof, this reasoner has been created by Rodin
			// with factor 1 and only the stored input knows the factor used
			final int factor = Math.max(timeoutFactor,
					disproverInput.getTimeoutFactor());
			ICounterExample ce = evaluateSequent(sequent, disproverInput,
					factor, pm);
			return createDisproverResult(ce, sequent, disproverInput);
		} catch (RodinDBException e) {
			Logger.log(IStatus.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (InterruptedException e) {
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		}
	}

	private ICounterExample evaluateSequent(final IProverSequent sequent,
			final DisproverReasonerInput disproverInput, int timeoutFactor,
			IProofMonitor pm) throws RodinDBException, InterruptedException {
		// Logger.info("Calling Disprover on Sequent");

		Set<Predicate> allHypotheses = new HashSet<Predicate>();
		// StringBuilder hypothesesString = new StringBuilder();
		for (Predicate predicate : sequent.hypIterable()) {
			allHypotheses.add(predicate);
			// hypothesesString.append(predicateToProlog(predicate));
			// hypothesesString.append(" & ");
		}

		Set<Predicate> selectedHypotheses = new HashSet<Predicate>();
		// StringBuilder hypothesesString = new StringBuilder();
		for (Predicate predicate : sequent.selectedHypIterable()) {
			selectedHypotheses.add(predicate);
			// hypothesesString.append(predicateToProlog(predicate));
			// hypothesesString.append(" & ");
		}

		/*
		 * if (hypothesesString.length() == 0) {
		 * Logger.info("Disprover: No Hypotheses"); } else {
		 * hypothesesString.delete(hypothesesString.length() - 2,
		 * hypothesesString.length());
		 * Logger.info("Disprover: Sending Hypotheses: " +
		 * UnicodeTranslator.toAscii(hypothesesString.toString())); }
		 */
		Predicate goal = sequent.goal();
		// Logger.info("Disprover: Sending Goal: "+
		// UnicodeTranslator.toAscii(predicateToProlog(goal)));

		AEventBContextParseUnit context = DisproverContextCreator
				.createDisproverContext(sequent);

		// find the IEventBProject belonging to the sequent
		IPOSequent origin = (IPOSequent) sequent.getOrigin();
		
		IEventBProject evbProject;
		
		if (origin==null) { // no origin available; seems to happen in Rodin 3.5RC upon startup
		   System.out.println("No origin available for sequent");
		   // throw new InterruptedException(); // Should we do this instead of trying to work with null project?
		   evbProject = null;
		} else {
			IRodinProject project = origin.getRodinProject();
			evbProject = (IEventBProject) project
					.getAdapter(IEventBProject.class);
		}
		ICounterExample counterExample = DisproverCommand.disprove(
				Animator.getAuxAnimator(), evbProject, allHypotheses,
				selectedHypotheses, goal, timeoutFactor, context, pm,
				getOptions(disproverInput));
		// counterExample is null if the ProB command failed;
		// createDisproverResult turns that into a reasoner failure
		Logger.info("Disprover: Result: " + counterExample);
		if (interactive && counterExample != null
				&& counterExample.counterExampleFound())
			notifyCounterExample(sequent, counterExample, evbProject, context,
					selectedHypotheses);

		return counterExample;
	}

	/**
	 * Tells the user about the counter-example in a notice that does not
	 * block them; its details list the values and the goal.
	 */
	private static void notifyCounterExample(final IProverSequent sequent,
			final ICounterExample ce, final IEventBProject project,
			final AEventBContextParseUnit context,
			final Set<Predicate> selectedHypotheses) {
		final Object origin = sequent.getOrigin();
		String where = "";
		if (origin instanceof IPOSequent) {
			final IPOSequent po = (IPOSequent) origin;
			where = " for " + po.getElementName() + " ("
					+ po.getRodinFile().getBareName() + ")";
		}
		final String message = ce.onlySelectedHypotheses() ? "ProB found a counter-example"
				+ where
				+ " using only the selected hypotheses: the goal may still be provable with all hypotheses."
				: "ProB found a counter-example" + where
						+ ": the goal does not follow from the hypotheses.";
		final StringBuilder details = new StringBuilder();
		if (ce.getValues().isEmpty())
			details.append("No identifiers need a value.\n");
		else {
			details.append("Values:\n");
			for (java.util.Map.Entry<String, String> e : ce.getValues()
					.entrySet())
				details.append("  ").append(e.getKey()).append(" = ")
						.append(e.getValue()).append('\n');
		}
		details.append("\nGoal: ").append(sequent.goal());
		final CounterExampleVisualiser visualiser = counterExampleVisualiser;
		if (visualiser == null) {
			Logger.notifyUserNonModal(message, details.toString());
			return;
		}
		final Predicate goal = sequent.goal();
		final String title = "Counter-Example" + where;
		Logger.notifyUserNonModal(message, details.toString(), "Visualise",
				() -> visualiser.show(title, Animator.getAuxAnimator(),
						dotFile -> DisproverCommand.writeCounterExampleDot(
								Animator.getAuxAnimator(), project, context,
								goal, selectedHypotheses, ce.getBindings(),
								dotFile)));
	}

	/** shows a counter-example for the notice's "Visualise" link */
	private static volatile CounterExampleVisualiser counterExampleVisualiser;

	/**
	 * Called by the disprover's UI plug-in: without a visualiser, the notice
	 * about a counter-example has no "Visualise" link.
	 */
	public static void setCounterExampleVisualiser(
			final CounterExampleVisualiser visualiser) {
		counterExampleVisualiser = visualiser;
	}

	/**
	 * @return the additional options for cbc_disprove/6, or <code>null</code>
	 *         for the normal disprover
	 */
	private static List<PrologTerm> getOptions(
			final DisproverReasonerInput input) {
		if (!input.isWdProver())
			return null;
		final List<PrologTerm> options = new ArrayList<PrologTerm>();
		options.add(disproverOption(new CompoundPrologTerm(
				"wd_prover_timeout", new IntegerPrologTerm(input
						.getWdProverTimeout()))));
		options.add(disproverOption(new CompoundPrologTerm(
				"no_constraint_solving")));
		return options;
	}

	/** ProB expects each option of cbc_disprove/6 as disprover_option(Option) */
	private static PrologTerm disproverOption(final PrologTerm option) {
		return new CompoundPrologTerm("disprover_option", option);
	}

	@SuppressWarnings("unused")
	private String predicateToProlog(Predicate pred) {
		PrologTermStringOutput pto = new PrologTermStringOutput();
		TranslationVisitor v = new TranslationVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
		return pto.toString();
	}

	/**
	 * Create a {@link IProofRule} containing the result from the disprover.
	 */
	private IReasonerOutput createDisproverResult(
			final ICounterExample counterExample, final IProverSequent sequent,
			final DisproverReasonerInput input) {

		// shown in the proof tree
		final String prover = input.isWdProver() ? "ProB WD prover" : "ProB";

		Predicate goal = sequent.goal();

		// currently, we assume that all existing hypotheses have been used
		// TODO: make ProB figure out which ones were actually used
		Set<Predicate> usedHyps = new HashSet<Predicate>();
		for (Predicate predicate : sequent.hypIterable()) {
			usedHyps.add(predicate);
		}

		IAntecedent ante = ProverFactory.makeAntecedent(goal);

		if (counterExample == null) {
			return ProverFactory.reasonerFailure(this, input,
					prover + ": Error occurred.");
		}

		if (counterExample.timeoutOccured()) {
			System.out.println(sequent.toString() + ": Timeout occured.");
			return ProverFactory.reasonerFailure(this, input,
					prover + ": Timeout occurred.");
		}

		if (!counterExample.counterExampleFound() && counterExample.isProof()
				&& !counterExample.doubleCheckFailed()) {
			System.out.println(sequent.toString() + ": Proof.");
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					usedHyps, IConfidence.DISCHARGED_MAX,
					// the suffix only makes sense for the constraint solver
					input.isWdProver() ? prover
							: prover + " (no enumeration / all cases checked)");
		}

		if (!counterExample.counterExampleFound() && counterExample.isProof()
				&& counterExample.doubleCheckFailed()) {
			System.out.println(sequent.toString() + ": Proof.");
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					usedHyps, IConfidence.DISCHARGED_MAX,
					prover + " (contradiction in hypotheses)");
		}

		if (!counterExample.counterExampleFound() && input.isWdProver()) {
			// no constraint solving: no counter-examples are searched
			final String reason = counterExample.getReason();
			return ProverFactory.reasonerFailure(this, input, prover
					+ ": Not proven"
					+ (reason == null ? "." : " (" + reason + ")."));
		}

		if (!counterExample.counterExampleFound()) {
			System.out.println(sequent.toString() + ": Unsure.");

			return ProverFactory.reasonerFailure(
					this,
					input,
					"ProB: No Counter-Example found due to "
							+ counterExample.getReason()
							+ ", but there might exist one.");
		}

		if (counterExample.counterExampleFound()
				&& counterExample.onlySelectedHypotheses()) {
			System.out.println(sequent.toString()
					+ ": Counter-Example for selected hypotheses found.");
            System.out.println(counterExample.toString()); 
			return ProverFactory
					.reasonerFailure(
							this,
							input,
							"ProB: Counter-Example to Goal for selected Hypotheses found (may be provable with all Hypotheses): " + counterExample.toString()); // use abbreviate or substring(0, Math.min(s.length(), 100));
		}

		System.out.println(sequent.toString() + ": Counter-Example found.");
		return ProverFactory.makeProofRule(this, input, sequent.goal(),
				usedHyps, IConfidence.PENDING, counterExample.toString(), ante);
	}

	@Override
	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		// missing in proofs stored before these values were serialized, and
		// for the default values
		final int factor = readInt(reader, TIMEOUT_FACTOR_KEY, 1);
		final int wdProverTimeout = readInt(reader, WD_PROVER_TIMEOUT_KEY,
				DisproverReasonerInput.NO_WD_PROVER);
		return new DisproverReasonerInput(factor, wdProverTimeout);
	}

	private static int readInt(final IReasonerInputReader reader,
			final String key, final int defaultValue)
			throws SerializeException {
		String value;
		try {
			value = reader.getString(key);
		} catch (SerializeException e) {
			value = null; // not stored
		}
		if (value == null)
			return defaultValue;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new SerializeException(new IllegalStateException(
					"Invalid value for disprover input " + key + ": " + value,
					e));
		}
	}

	@Override
	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
		final DisproverReasonerInput disproverInput = (DisproverReasonerInput) input;
		final int factor = disproverInput.getTimeoutFactor();
		// keep proofs with the default timeout as they were
		if (factor != 1)
			writer.putString(TIMEOUT_FACTOR_KEY, Integer.toString(factor));
		// so that a proof of the WD prover is replayed with the WD prover
		if (disproverInput.isWdProver())
			writer.putString(WD_PROVER_TIMEOUT_KEY,
					Integer.toString(disproverInput.getWdProverTimeout()));
	}

}
