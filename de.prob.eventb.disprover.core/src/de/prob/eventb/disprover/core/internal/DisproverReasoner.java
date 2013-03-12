package de.prob.eventb.disprover.core.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Status;
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
import org.rodinp.core.RodinDBException;

import de.prob.core.Animator;
import de.prob.eventb.disprover.core.DisproverReasonerInput;
import de.prob.eventb.disprover.core.ICounterExample;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class DisproverReasoner implements IReasoner {

	static final String DISPROVER_CONTEXT = "disprover_context";

	private static final String DISPROVER_REASONER_NAME = "de.prob.eventb.disprover.core.disproverReasoner";

	public String getReasonerID() {
		return DISPROVER_REASONER_NAME;
	}

	/**
	 * Applies the Disprover by building a machine from Goal and Hypotheses.
	 */
	public IReasonerOutput apply(final IProverSequent sequent,
			final IReasonerInput input, final IProofMonitor pm) {
		try {
			DisproverReasonerInput disproverInput = (DisproverReasonerInput) input;
			ICounterExample ce = evaluateSequent(sequent, disproverInput);
			return createDisproverResult(ce, sequent, input);
		} catch (ProBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (RodinDBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		}
	}

	private ICounterExample evaluateSequent(final IProverSequent sequent,
			final DisproverReasonerInput disproverInput) throws ProBException,
			RodinDBException {

		Set<Predicate> hypotheses = new HashSet<Predicate>();
		for (Predicate predicate : disproverInput.getHypotheses(sequent)) {
			hypotheses.add(predicate);
		}
		Predicate goal = sequent.goal();

		ICounterExample counterExample = DisproverCommand.disprove(
				Animator.getAnimator(), hypotheses, goal);
		return counterExample;
	}

	/**
	 * Create a {@link IProofRule} containing the result from the disprover.
	 */
	private IReasonerOutput createDisproverResult(
			final ICounterExample counterExample, final IProverSequent sequent,
			final IReasonerInput input) {

		if (counterExample.timeoutOccured()) {
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					null, IConfidence.REVIEWED_MAX,
					"Timeout occured (Disprover)");

		}

		if (!counterExample.counterExampleFound())
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					null, IConfidence.REVIEWED_MAX,
					"No Counter-Example found (Disprover)");

		IAntecedent anticident = makeAnticident(
				(CounterExample) counterExample, sequent);

		return ProverFactory.makeProofRule(this, input, null,
				"Counter-Example: " + counterExample.toString(), anticident);

	}

	// TODO build proper Anticident
	private IAntecedent makeAnticident(final CounterExample counterExample,
			final IProverSequent sequent) {
		return ProverFactory.makeAntecedent(sequent.goal());
	}

	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		return null;
	}

	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
	}

}
