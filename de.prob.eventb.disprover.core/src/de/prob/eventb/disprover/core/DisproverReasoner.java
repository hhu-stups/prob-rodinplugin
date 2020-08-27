package de.prob.eventb.disprover.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Status;
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

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.prob.core.Animator;
import de.prob.eventb.disprover.core.internal.DisproverCommand;
import de.prob.eventb.disprover.core.internal.ICounterExample;
import de.prob.eventb.disprover.core.translation.DisproverContextCreator;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.logging.Logger;
import de.prob.prolog.output.PrologTermStringOutput;

public class DisproverReasoner implements IReasoner {

	static final String DISPROVER_CONTEXT = "disprover_context";

	private static final String DISPROVER_REASONER_NAME = "de.prob.eventb.disprover.core.disproverReasoner";

	private final int timeoutFactor;

	public DisproverReasoner() {
		this(1);
	}

	public DisproverReasoner(int timeoutFactor) {
		this.timeoutFactor = timeoutFactor;
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
			ICounterExample ce = evaluateSequent(sequent, disproverInput,
					timeoutFactor, pm);
			return createDisproverResult(ce, sequent, input);
		} catch (RodinDBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
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
		   System.out.println("No origin available for sequent")
		   // throw new InterruptedException(); // Should we do this instead of trying to work with null project?
		   evbProject = null;
		} else {
			IRodinProject project = origin.getRodinProject();
			evbProject = (IEventBProject) project
					.getAdapter(IEventBProject.class);
		}
		ICounterExample counterExample = DisproverCommand.disprove(
				Animator.getAuxAnimator(), evbProject, allHypotheses,
				selectedHypotheses, goal, timeoutFactor, context, pm);
		// Logger.info("Disprover: Result: " + counterExample.toString());

		return counterExample;
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
			final IReasonerInput input) {

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
					"ProB: Error occurred.");
		}

		if (counterExample.timeoutOccured()) {
			System.out.println(sequent.toString() + ": Timeout occured.");
			return ProverFactory.reasonerFailure(this, input,
					"ProB: Timeout occurred.");
		}

		if (!counterExample.counterExampleFound() && counterExample.isProof()
				&& !counterExample.doubleCheckFailed()) {
			System.out.println(sequent.toString() + ": Proof.");
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					usedHyps, IConfidence.DISCHARGED_MAX,
					"ProB (no enumeration / all cases checked)");
		}

		if (!counterExample.counterExampleFound() && counterExample.isProof()
				&& counterExample.doubleCheckFailed()) {
			System.out.println(sequent.toString() + ": Proof.");
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					usedHyps, IConfidence.DISCHARGED_MAX,
					"ProB (contradiction in hypotheses)");
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

			return ProverFactory
					.reasonerFailure(
							this,
							input,
							"ProB: Counter-Example for selected Hypotheses found, Goal not provable from selected Hypotheses (may be provable with all Hypotheses)");
		}

		System.out.println(sequent.toString() + ": Counter-Example found.");
		return ProverFactory.makeProofRule(this, input, sequent.goal(),
				usedHyps, IConfidence.PENDING, counterExample.toString(), ante);
	}

	@Override
	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		return new DisproverReasonerInput();
	}

	@Override
	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
	}

}
