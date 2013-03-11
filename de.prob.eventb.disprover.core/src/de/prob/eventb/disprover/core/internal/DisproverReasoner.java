package de.prob.eventb.disprover.core.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Status;
import org.eventb.core.ISCContextRoot;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.ITypeEnvironment.IIterator;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
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

import de.be4.classicalb.core.parser.analysis.ASTPrinter;
import de.be4.classicalb.core.parser.node.AAxiomsContextClause;
import de.be4.classicalb.core.parser.node.AConstantsContextClause;
import de.be4.classicalb.core.parser.node.ADeferredSetSet;
import de.be4.classicalb.core.parser.node.AEvent;
import de.be4.classicalb.core.parser.node.AEventBContextParseUnit;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.be4.classicalb.core.parser.node.AEventsModelClause;
import de.be4.classicalb.core.parser.node.AExtendsContextClause;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.ASeesModelClause;
import de.be4.classicalb.core.parser.node.ASetsContextClause;
import de.be4.classicalb.core.parser.node.ATheoremsContextClause;
import de.be4.classicalb.core.parser.node.EOF;
import de.be4.classicalb.core.parser.node.PContextClause;
import de.be4.classicalb.core.parser.node.PEvent;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PModelClause;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSet;
import de.be4.classicalb.core.parser.node.Start;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.core.Animator;
import de.prob.eventb.disprover.core.AstPrettyPrinter;
import de.prob.eventb.disprover.core.DisproverException;
import de.prob.eventb.disprover.core.DisproverReasonerInput;
import de.prob.eventb.disprover.core.ICounterExample;
import de.prob.eventb.translator.ContextTranslator;
import de.prob.eventb.translator.PredicateVisitor;
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
			ICounterExample counterExample = evaluateSequent(sequent,
					disproverInput);
			return createDisproverResult(counterExample, sequent, input);
		} catch (ProBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		} catch (RodinDBException e) {
			Logger.log(Logger.WARNING, Status.WARNING, e.getMessage(), e);
			return ProverFactory.reasonerFailure(this, input, e.getMessage());
		}
	}

	ICounterExample evaluateSequent(final IProverSequent sequent,
			final DisproverReasonerInput disproverInput) throws ProBException,
			RodinDBException {
		// Build a Machine ast
		List<DisproverIdentifier> ids = findIdentifiers(sequent, disproverInput);

		AEventBModelParseUnit modelParseUnit = createEmptyModelParseUnit();

		List<AEventBContextParseUnit> contextParseUnits = createContextParseUnits(
				sequent, disproverInput, ids);

		// TODO Remove before release
		AstPrettyPrinter printer = new AstPrettyPrinter();
		modelParseUnit.apply(printer);
		System.out.println(printer.toString());
		System.out
				.println("-------------- Machine ------------------------------");
		Start ast = new Start(modelParseUnit, new EOF());
		ast.apply(printer);
		ast.apply(new ASTPrinter());

		for (AEventBContextParseUnit parseUnit : contextParseUnits) {
			System.out.println("-------------- Context: " + parseUnit.getName()
					+ " -----------------");
			ast = new Start(parseUnit, new EOF());
			ast.apply(printer);
			ast.apply(new ASTPrinter());
		}
		System.out
				.println("-----------------------------------------------------");

		ICounterExample counterExample = DisproverCommand.disprove(Animator
				.getAnimator(), modelParseUnit, contextParseUnits, ids,
				disproverInput);
		return counterExample;
	}

	/**
	 * @return a ModelClause that contains an machine with the name
	 *         "disprover_machine" that contains an empty INITIALISATION event
	 *         and that sees a Context with the name {@link #DISPROVER_CONTEXT}.
	 */
	AEventBModelParseUnit createEmptyModelParseUnit() {
		List<PModelClause> modelClauses = new ArrayList<PModelClause>();

		// We see the context
		List<TIdentifierLiteral> ctxList = new ArrayList<TIdentifierLiteral>();
		ctxList.add(new TIdentifierLiteral(DISPROVER_CONTEXT));
		ASeesModelClause ctxClause = new ASeesModelClause(ctxList);
		modelClauses.add(ctxClause);

		List<PEvent> events = new ArrayList<PEvent>();
		AEvent init = new AEvent();
		init.setEventName(new TIdentifierLiteral("INITIALISATION"));
		events.add(init);
		AEventsModelClause eventClause = new AEventsModelClause();
		eventClause.setEvent(events);
		modelClauses.add(eventClause);

		AEventBModelParseUnit modelParseUnit = new AEventBModelParseUnit();
		modelParseUnit.setName(new TIdentifierLiteral("disprover_machine"));
		modelParseUnit.setModelClauses(modelClauses);
		return modelParseUnit;
	}

	private List<AEventBContextParseUnit> createContextParseUnits(
			final IProverSequent sequent,
			final DisproverReasonerInput disproverInput,
			final List<DisproverIdentifier> ids) throws RodinDBException,
			ProBException {

		ArrayList<AEventBContextParseUnit> parseUnits = new ArrayList<AEventBContextParseUnit>();

		for (ISCContextRoot context : disproverInput.getContexts()) {
			parseUnits.add(ContextTranslator.create(context).getContextAST());
		}

		// Build a Context ast
		// Adding the clauses. Careful - Order matters!
		List<PContextClause> contextClauses = new ArrayList<PContextClause>();

		List<TIdentifierLiteral> extendList = new ArrayList<TIdentifierLiteral>();
		for (ISCContextRoot context : disproverInput.getContexts()) {
			extendList.add(new TIdentifierLiteral(context.getComponentName()));
		}
		AExtendsContextClause extend = new AExtendsContextClause(extendList);
		contextClauses.add(extend);

		contextClauses.add(createConstantsClause(disproverInput, ids));

		contextClauses.add(createAxiomsClause(sequent, disproverInput, ids));

		ATheoremsContextClause theorems = new ATheoremsContextClause();
		contextClauses.add(theorems);

		contextClauses.add(createSetClause(disproverInput, ids));

		AEventBContextParseUnit contextParseUnit = new AEventBContextParseUnit();
		contextParseUnit.setName(new TIdentifierLiteral(DISPROVER_CONTEXT));
		contextParseUnit.setContextClauses(contextClauses);
		parseUnits.add(contextParseUnit);
		return parseUnits;
	}

	private PContextClause createAxiomsClause(final IProverSequent sequent,
			final DisproverReasonerInput input,
			final List<DisproverIdentifier> ids) throws DisproverException {
		AAxiomsContextClause axiomsClause = new AAxiomsContextClause();

		List<PPredicate> axioms = new ArrayList<PPredicate>();

		// Add the negated goal to the guards
		PPredicate goal = convertPredicate(sequent.goal());
		axioms.add(new ANegationPredicate(goal));

		// Add the hypothesis to the guards
		for (Predicate predicate : input.getHypotheses(sequent)) {
			PPredicate hyp = convertPredicate(predicate);
			axioms.add(hyp);
		}

		// Add type information to the guards
		for (DisproverIdentifier id : ids) {
			if (id.getType() != null) {
				axioms.add(new AMemberPredicate(id.getId(), id.getType()));
			}
		}

		axiomsClause.setPredicates(axioms);
		return axiomsClause;
	}

	PContextClause createConstantsClause(final DisproverReasonerInput input,
			final List<DisproverIdentifier> ids) throws RodinDBException,
			DisproverException {
		List<PExpression> constants = new ArrayList<PExpression>();
		AConstantsContextClause constantsClause = new AConstantsContextClause();

		for (DisproverIdentifier id : ids) {
			if (id.getType() != null) {
				if (!input.getConstantNames().contains(id.getName())) {
					constants.add(id.getId());
				}
			}
		}

		constantsClause.setIdentifiers(constants);
		return constantsClause;
	}

	/**
	 * @return a {@link ASetsContextClause} that is only filled when necessary.
	 *         It is not necessary if all contexts are included.
	 */
	private PContextClause createSetClause(final DisproverReasonerInput input,
			final List<DisproverIdentifier> ids) {
		List<PSet> sets = new ArrayList<PSet>();
		if (!input.usesContexts()) {
			for (DisproverIdentifier id : ids) {
				if (id.getType() != null) {
					continue;
				}
				List<TIdentifierLiteral> idList = new ArrayList<TIdentifierLiteral>();
				idList.add(new TIdentifierLiteral(id.getName()));
				sets.add(new ADeferredSetSet(idList));
			}
		}
		ASetsContextClause setClause = new ASetsContextClause(sets);
		return setClause;
	}

	/**
	 * Create a {@link IProofRule} containing the result from the disprover.
	 */
	private IReasonerOutput createDisproverResult(
			final ICounterExample counterExample, final IProverSequent sequent,
			final IReasonerInput input) {

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

		// for (String name : counterExample.getState().keySet()) {
		// String value = counterExample.getState().get(name);
		//
		// Expression left = FormulaFactory.getDefault().makeFreeIdentifier(
		// "x", null);
		// // sequent.typeEnvironment().
		// Expression right = FormulaFactory.getDefault().makeIntegerLiteral(
		// new BigInteger(value), null);
		// RelationalPredicate expr = FormulaFactory.getDefault()
		// .makeRelationalPredicate(Formula.EQUAL, left, right, null);
		// Set<Predicate> hyps = new HashSet<Predicate>();
		// hyps.add(expr);
		// return ProverFactory.makeAntecedent(sequent.goal(), hyps, null);
		// }
		//
	}

	/**
	 * @return the ProB-{@link PPredicate}, generated from the Event-B-Predicate
	 */
	private PPredicate convertPredicate(final Predicate predicate) {
		LinkedList<String> list = new LinkedList<String>();
		PredicateVisitor visitor = new PredicateVisitor(list);
		predicate.accept(visitor);
		return visitor.getPredicate();
	}

	public IReasonerInput deserializeInput(final IReasonerInputReader reader)
			throws SerializeException {
		return null;
	}

	public void serializeInput(final IReasonerInput input,
			final IReasonerInputWriter writer) throws SerializeException {
	}

	/**
	 * Helper that extracts typed identifiers from an {@link IProverSequent}. It
	 * looks for all identifiers in Goal and Hypotheses, and then extracts the
	 * type information from {@link ITypeEnvironment}. Unfortunately,
	 * ITypeEnvironment contains too many identifiers. While we just could use
	 * those, that would burden the model checker unnecessarily.
	 */
	List<DisproverIdentifier> findIdentifiers(final IProverSequent sequent,
			final DisproverReasonerInput input) throws DisproverException {
		Map<String, DisproverIdentifier> ids = new TreeMap<String, DisproverIdentifier>();

		// Select all Predicates to be processed (Hypotheses and Goal)
		Set<Predicate> predicates = new HashSet<Predicate>();
		for (Predicate predicate : input.getHypotheses(sequent)) {
			predicates.add(predicate);
		}
		predicates.add(sequent.goal());

		// Find the Identifiers in the Predicates
		for (Predicate predicate : predicates) {
			for (FreeIdentifier id : predicate.getFreeIdentifiers()) {
				Type type = sequent.typeEnvironment().getType(id.getName());

				// We retrieve deferred sets here. Unfortunately, this is not
				// very intuitive. For a deferred set S, the type is POW(S).
				// Thus, if the name of the set equals type.getBaseType() we
				// know we found a deferred set.
				Type baseType = type.getBaseType();
				if (baseType != null
						&& baseType.toString().equals(id.getName())) {
					type = null;
				}

				LinkedList<String> bounds = new LinkedList<String>();
				ids
						.put(id.getName(), new DisproverIdentifier(id, type,
								bounds));
			}
		}

		// We may have missed some sets. Thus, we iterate once again over all
		// Types and try to find more deferred sets.
		ITypeEnvironment env = sequent.typeEnvironment();
		for (IIterator i = env.getIterator(); i.hasNext();) {
			i.advance();
			String name = i.getName();
			Type type = i.getType();
			Type baseType = type.getBaseType();
			if (baseType != null && baseType.toString().equals(name)) {
				FreeIdentifier id = FormulaFactory.getDefault()
						.makeFreeIdentifier(name, null, baseType);
				LinkedList<String> bounds = new LinkedList<String>();
				ids.put(name, new DisproverIdentifier(id, null, bounds));
			}
		}

		return new ArrayList<DisproverIdentifier>(ids.values());
	}

}
