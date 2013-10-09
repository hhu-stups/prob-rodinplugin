package de.prob.eventb.disprover.core.internal;

import java.util.*;

import org.eclipse.core.runtime.Status;
import org.eventb.core.ast.*;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.seqprover.*;
import org.eventb.core.seqprover.IProofRule.IAntecedent;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.*;
import de.prob.core.*;
import de.prob.eventb.disprover.core.DisproverReasonerInput;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.prolog.output.PrologTermStringOutput;

public class DisproverReasoner implements IReasoner {

	static final String DISPROVER_CONTEXT = "disprover_context";

	private static final String DISPROVER_REASONER_NAME = "de.prob.eventb.disprover.core.disproverReasoner";

	@Override
	public String getReasonerID() {
		return DISPROVER_REASONER_NAME;
	}

	@Override
	public IReasonerOutput apply(final IProverSequent sequent,
			final IReasonerInput input, final IProofMonitor pm) {
		try {
			DisproverReasonerInput disproverInput = (DisproverReasonerInput) input;
			ICounterExample ce = evaluateSequent(sequent, disproverInput, pm);
			return createDisproverResult(ce, sequent, input);
		} catch (PrologException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (ProBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (RodinDBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (InterruptedException e) {
			return ProverFactory.reasonerFailure(this, input, e.getMessage());

		}
	}

	private ICounterExample evaluateSequent(final IProverSequent sequent,
			final DisproverReasonerInput disproverInput, IProofMonitor pm)
			throws ProBException, RodinDBException, InterruptedException {
		// Logger.info("Calling Disprover on Sequent");

		Set<Predicate> hypotheses = new HashSet<Predicate>();
		StringBuilder hypothesesString = new StringBuilder();
		for (Predicate predicate : sequent.hypIterable()) {
			hypotheses.add(predicate);
			hypothesesString.append(predicateToProlog(predicate));
			hypothesesString.append(" & ");
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

		AEventBContextParseUnit context = createContext(sequent);

		ICounterExample counterExample = DisproverCommand.disprove(
				Animator.getAnimator(), hypotheses, goal, context, pm);
		// Logger.info("Disprover: Result: " + counterExample.toString());

		return counterExample;
	}

	private String predicateToProlog(Predicate pred) {
		PrologTermStringOutput pto = new PrologTermStringOutput();
		TranslationVisitor v = new TranslationVisitor();
		pred.accept(v);
		ASTProlog p = new ASTProlog(pto, null);
		v.getPredicate().apply(p);
		return pto.toString();
	}

	/**
	 * Creates an artificial root that contains only typing information
	 * 
	 * @param sequent
	 * @return Machine root of artificial machine
	 */
	private AEventBContextParseUnit createContext(IProverSequent sequent) {
		AEventBContextParseUnit context = new AEventBContextParseUnit();

		context.setName(new TIdentifierLiteral("DisproverContext"));

		final List<PContextClause> contextClauses = new ArrayList<PContextClause>();

		// collecting variables, sets and (type-)invariants
		final AConstantsContextClause constantsClause = new AConstantsContextClause();
		final List<PExpression> constantsIdentifiers = new ArrayList<PExpression>();

		final ASetsContextClause setsContextClause = new ASetsContextClause();
		final List<PSet> sets = new ArrayList<PSet>();

		final AAxiomsContextClause axiomsContextClause = new AAxiomsContextClause();
		final List<PPredicate> axioms = new ArrayList<PPredicate>();

		// Iterate over the type environment to construct a typing context
		ITypeEnvironment typeEnvironment = sequent.typeEnvironment();
		IIterator typeIterator = typeEnvironment.getIterator();

		while (typeIterator.hasNext()) {
			typeIterator.advance();

			DisproverIdentifier id = new DisproverIdentifier(
					typeIterator.getName(), typeIterator.getType(),
					typeIterator.isGivenSet(), sequent.getFormulaFactory());

			// sets are added to the context, vars to the model
			if (id.isGivenSet()) {
				sets.add(new ADeferredSetSet(id.getId()));
			} else {
				// might not be necessary for constants
				// if (!id.isPrimedVariable()) {
				constantsIdentifiers.add(new AIdentifierExpression(id.getId()));
				axioms.add(new AMemberPredicate(id.getIdExpression(), id
						.getType()));
				// }
			}
		}

		// store hypothesis as axioms
		TranslationVisitor translator = new TranslationVisitor();
		for (Predicate predicate : sequent.hypIterable()) {
			predicate.accept(translator);
			axioms.add(translator.getPredicate());
		}

		axiomsContextClause.setPredicates(axioms);
		contextClauses.add(axiomsContextClause);

		constantsClause.setIdentifiers(constantsIdentifiers);
		contextClauses.add(constantsClause);

		setsContextClause.setSet(sets);
		contextClauses.add(setsContextClause);

		context.setContextClauses(contextClauses);

		return context;
	}

	/**
	 * Create a {@link IProofRule} containing the result from the disprover.
	 */
	private IReasonerOutput createDisproverResult(
			final ICounterExample counterExample, final IProverSequent sequent,
			final IReasonerInput input) {

		Predicate goal = sequent.goal();

		IAntecedent ante = ProverFactory.makeAntecedent(goal);

		if (counterExample.timeoutOccured())
			return ProverFactory.reasonerFailure(this, input,
					"ProB: Timeout occurred.");

		if (!counterExample.counterExampleFound() && counterExample.isProof())
			return ProverFactory.makeProofRule(this, input, sequent.goal(),
					null, IConfidence.DISCHARGED_MAX,
					"ProB (no enumeration / all cases checked)");

		if (!counterExample.counterExampleFound())
			return ProverFactory
					.reasonerFailure(this, input,
							"ProB: No Counter-Example found, but there might exist one.");

		return ProverFactory.makeProofRule(this, input, null, null,
				IConfidence.PENDING, counterExample.toString(), ante);
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
