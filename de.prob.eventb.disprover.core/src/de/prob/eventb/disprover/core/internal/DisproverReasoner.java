package de.prob.eventb.disprover.core.internal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.Status;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.SourceLocation;
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
import de.prob.unicode.UnicodeTranslator;

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
					null, IConfidence.PENDING, "Timeout occured (Disprover)");

		}

		if (!counterExample.counterExampleFound() && counterExample.isProof())
			return ProverFactory
					.makeProofRule(this, input, sequent.goal(), null,
							IConfidence.DISCHARGED_MAX,
							"ProB (all cases checked)");

		if (!counterExample.counterExampleFound())
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					null, IConfidence.PENDING,
					"No Counter-Example found (Disprover)");

		Predicate goal = sequent.goal();// makeNewGoal((CounterExample)
										// counterExample,sequent);

		// Predicate ng = sequent.getFormulaFactory().makeAssociativePredicate(
		// Formula.LAND,
		// makeNewHyps((CounterExample) counterExample, sequent),
		// new SourceLocation(0, 0));

		IAntecedent ante = ProverFactory.makeAntecedent(goal);

		return ProverFactory.makeProofRule(this, input, null, null,
				IConfidence.PENDING,
				"Counter-Example: " + counterExample.toString(), ante);

	}

	// private Set<Predicate> makeNewHyps(final CounterExample counterExample,
	// final IProverSequent sequent) {
	// FormulaFactory ff = sequent.getFormulaFactory();
	// SourceLocation noloc = new SourceLocation(0, 0);
	// Set<Predicate> p = new HashSet<Predicate>();
	// p.add(sequent.goal());
	//
	// Set<Entry<String, String>> entries = counterExample.state.entrySet();
	// for (Entry<String, String> entry : entries) {
	// String predstring = "( " + entry.getKey() + " = "
	// + entry.getValue() + " )";
	// String unicode = UnicodeTranslator.toUnicode(predstring);
	// IParseResult parseResult = ff.parsePredicate(unicode,
	// LanguageVersion.V1, null);
	// if (!parseResult.getProblems().isEmpty()) {
	// return Collections.emptySet();
	// }
	// p.add(parseResult.getParsedPredicate());
	//
	// }
	//
	// return p;
	// }

	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		return null;
	}

	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
	}

}
