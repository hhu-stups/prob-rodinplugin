/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator.internal; // NOPMD by bendisposto
// High coupling to the ast is ok

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eventb.core.IConvergenceElement.Convergence;
import org.eventb.core.ILabeledElement;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IPOSequent;
import org.eventb.core.IPOSource;
import org.eventb.core.IPSRoot;
import org.eventb.core.IPSStatus;
import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCInternalContext;
import org.eventb.core.ISCInvariant;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.eventb.core.ISCPredicateElement;
import org.eventb.core.ISCRefinesEvent;
import org.eventb.core.ISCRefinesMachine;
import org.eventb.core.ISCVariable;
import org.eventb.core.ISCVariant;
import org.eventb.core.ISCWitness;
import org.eventb.core.ITraceableElement;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.basis.Event;
import org.eventb.core.basis.Guard;
import org.eventb.core.seqprover.IConfidence;
import org.rodinp.core.IElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.node.AAnticipatedEventstatus;
import de.be4.classicalb.core.parser.node.AConvergentEventstatus;
import de.be4.classicalb.core.parser.node.AEvent;
import de.be4.classicalb.core.parser.node.AEventBModelParseUnit;
import de.be4.classicalb.core.parser.node.AEventsModelClause;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AInvariantModelClause;
import de.be4.classicalb.core.parser.node.AOrdinaryEventstatus;
import de.be4.classicalb.core.parser.node.ARefinesModelClause;
import de.be4.classicalb.core.parser.node.ASeesModelClause;
import de.be4.classicalb.core.parser.node.ATheoremsModelClause;
import de.be4.classicalb.core.parser.node.AVariablesModelClause;
import de.be4.classicalb.core.parser.node.AVariantModelClause;
import de.be4.classicalb.core.parser.node.AWitness;
import de.be4.classicalb.core.parser.node.Node;
import de.be4.classicalb.core.parser.node.PEvent;
import de.be4.classicalb.core.parser.node.PEventstatus;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PModelClause;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.PSubstitution;
import de.be4.classicalb.core.parser.node.PWitness;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.core.translator.TranslationFailedException;
import de.prob.eventb.translator.AbstractComponentTranslator;
import de.prob.logging.Logger;

public class ModelTranslator extends AbstractComponentTranslator {

	private final ISCMachineRoot machine;
	private final AEventBModelParseUnit model = new AEventBModelParseUnit();
	private final FormulaFactory ff;
	private final ITypeEnvironment te;
	private final IMachineRoot origin;

	// private final List<String> depContext = new ArrayList<String>();

	// Confined in the thread calling the factory method
	private String refines;
	private boolean broken = false;

	// ###############################################################################################################################################################
	// Public Interface

	public static ModelTranslator create(final ISCMachineRoot model)
			throws TranslationFailedException {
		ModelTranslator modelTranslator = new ModelTranslator(model);
		try {
			modelTranslator.translate();
		} catch (CoreException re) {
			final String message = "Rodin Database Exception / Core Exception: \n"
					+ re.getLocalizedMessage();
			throw new TranslationFailedException(modelTranslator.getClass()
					.toString(), message);
		} catch (RuntimeException e) {
			// spurious runtime exceptions were thrown, especially if older
			// EventB= Projects were used without cleaning the project
			final String message = "Possible cause: building aborted or still in progress. Please wait until building has finished before starting ProB. If this does not help, perform a clean and start ProB after building has finished.\nException was: "
					+ e.getClass().getSimpleName()
					+ "\n"
					+ e.getLocalizedMessage();
			throw new TranslationFailedException(modelTranslator.getClass()
					.toString(), message);
		}
		return modelTranslator;
	}

	public AEventBModelParseUnit getModelAST() {
		if (broken) {
			final String message = "The machine contains Rodin Problems. ProB will continue, but you may observe unexpected behaviour";
			Logger.notifyUserWithoutBugreport(message);
			return model;
		}
		return model;
	}

	// public List<String> getContextDependencies() {
	// return depContext;
	// }

	public String getRefinementDependency() {
		return refines;
	}

	// ###############################################################################################################################################################
	// Implementation

	private ModelTranslator(final ISCMachineRoot machine)
			throws TranslationFailedException {
		super(machine.getComponentName());
		this.machine = machine;
		origin = machine.getMachineRoot();
		ff = machine.getFormulaFactory();
		try {
			te = machine.getTypeEnvironment();
		} catch (CoreException e) {
			final String message = "A Rodin exception occured during translation process. Original Exception: ";
			throw new TranslationFailedException(machine.getComponentName(),
					message + e.getLocalizedMessage());
		}
	}

	private void translate() throws CoreException, TranslationFailedException {

		final String message = "machine.getRodinFile().isConsistent() [Note: Maybe you can fix this Rodin problem by refreshing and rebuilding the project]";
		Logger.assertProB(message, machine.getRodinFile().isConsistent());

		broken = !machine.isAccurate(); // isAccurate() is not transitive, we
		// need to collect the information also
		// for the events
		translateMachine();

		// Check for fully discharged Invariants and Events
		collectProofInfo();

		// Collect Pragmas, Units, etc.
		collectPragmas();
	}

	private void collectPragmas() throws RodinDBException {
		// unit pragma, attached to variables
		addUnitPragmas(machine.getSCVariables());
	}

	private void collectProofInfo() throws RodinDBException {

		IPSRoot proofStatus = machine.getPSRoot();
		IPSStatus[] statuses = proofStatus.getStatuses();

		List<String> bugs = new LinkedList<String>();

		for (IPSStatus status : statuses) {
			final int confidence = status.getConfidence();
			boolean broken = status.isBroken();

			EProofStatus pstatus = EProofStatus.UNPROVEN;

			if (!broken
					&& (confidence > IConfidence.PENDING && confidence <= IConfidence.REVIEWED_MAX))
				pstatus = EProofStatus.REVIEWED;
			if (!broken && confidence == IConfidence.DISCHARGED_MAX)
				pstatus = EProofStatus.PROVEN;

			IPOSequent sequent = status.getPOSequent();
			IPOSource[] sources = sequent.getSources();

			String name = sequent.getDescription();

			ArrayList<SequentSource> s = new ArrayList<SequentSource>(
					sources.length);
			for (IPOSource source : sources) {

				IRodinElement srcElement = source.getSource();
				if (!srcElement.exists()
						|| !(srcElement instanceof ILabeledElement)) {
					bugs.add(status.getElementName());
					break;
				}

				ILabeledElement le = (ILabeledElement) srcElement;
				IElementType<? extends IRodinElement> type = srcElement
						.getElementType();
				s.add(new SequentSource(type, le.getLabel()));

				if (srcElement instanceof Guard) {
					Event srcEvent = (Event) srcElement.getParent();
					String srvEventName = srcEvent.getLabel();
					s.add(new SequentSource(srcEvent.getElementType(),
							srvEventName));
				}

			}
			addProof(new ProofObligation(origin, s, name, pstatus));
		}

		if (!bugs.isEmpty()) {
			String message = "Translation incomplete due to a Bug in Rodin. This does not affect correctness of the Animation/Model Checking but can decrease its performance. Skipped discharged information about: "
					+ StringUtils.join(bugs, ",");
			Logger.notifyUser(message);
		}

	}

	private void translateMachine() throws CoreException,
			TranslationFailedException {
		model.setName(new TIdentifierLiteral(machine.getRodinFile()
				.getBareName()));

		final List<PModelClause> clauses = new ArrayList<PModelClause>();

		clauses.add(processContexts());
		final ARefinesModelClause ref = processRefines();
		if (ref != null) {
			clauses.add(ref);
		}
		clauses.add(processVariables());
		clauses.add(processInvariants());
		clauses.add(processTheorems());
		final AVariantModelClause var = processVariant();
		if (var != null) {
			clauses.add(var);
		}
		clauses.add(processEvents());

		model.setModelClauses(clauses);
	}

	private AVariantModelClause processVariant() throws CoreException,
			TranslationFailedException {
		final ISCVariant[] variant = machine.getSCVariants();
		final AVariantModelClause var;
		if (variant.length == 1) {
			final PExpression expression = translateExpression(ff, te,
					variant[0]);
			var = new AVariantModelClause(expression);
		} else if (variant.length == 0) {
			var = null;
		} else
			throw new TranslationFailedException(machine.getComponentName(),
					"expected at most one variant, but there were "
							+ variant.length);
		return var;
	}

	private ARefinesModelClause processRefines() throws CoreException,
			TranslationFailedException {
		final ISCRefinesMachine[] refinesClauses = machine
				.getSCRefinesClauses();
		final ARefinesModelClause ref;
		if (refinesClauses.length == 1) {
			final String name = refinesClauses[0].getAbstractSCMachine()
					.getBareName();
			ref = new ARefinesModelClause(new TIdentifierLiteral(name));
			refines = name;
		} else if (refinesClauses.length == 0) {
			ref = null;
			refines = null;
		} else
			throw new TranslationFailedException(machine.getComponentName(),
					"expected at most one refined machine, but there were "
							+ refinesClauses.length);
		return ref;
	}

	private ASeesModelClause processContexts() throws RodinDBException {
		final ISCInternalContext[] seenContexts = machine.getSCSeenContexts();
		final List<TIdentifierLiteral> contexts = new ArrayList<TIdentifierLiteral>(
				seenContexts.length);
		for (final ISCInternalContext context : seenContexts) {
			final String componentName = context.getComponentName();
			// depContext.add(componentName);
			contexts.add(new TIdentifierLiteral(componentName));
		}
		return new ASeesModelClause(contexts);
	}

	private AEventsModelClause processEvents()
			throws TranslationFailedException, RodinDBException {
		final AEventsModelClause clause = new AEventsModelClause();
		final ISCEvent[] events = machine.getSCEvents();
		final List<PEvent> eventsList = new ArrayList<PEvent>(events.length);
		for (final ISCEvent revent : events) {

			broken = broken || !revent.isAccurate();

			ITypeEnvironment localEnv = revent.getTypeEnvironment(te, ff);
			localEnv.addAll(te);

			ISCVariable[] variables = machine.getSCVariables();
			for (ISCVariable variable : variables) {
				if (variable.isAbstract() || variable.isConcrete()) {
					localEnv.add(variable.getIdentifier(ff).withPrime(ff));
				}
			}

			final AEvent event = new AEvent();
			// Name
			event.setEventName(new TIdentifierLiteral(revent.getLabel()));
			labelMapping.put(event, revent);

			List<PPredicate> guards = new ArrayList<PPredicate>();
			List<PPredicate> theorems = new ArrayList<PPredicate>();

			event.setStatus(extractEventStatus(revent));
			event.setRefines(extractRefinedEvents(revent));
			event.setVariables(extractParameters(revent));
			extractGuards(revent, localEnv, guards, theorems);
			event.setGuards(guards);
			event.setTheorems(theorems);
			event.setWitness(extractWitnesses(revent, localEnv));
			event.setAssignments(extractActions(revent, localEnv));
			eventsList.add(event);
		}
		clause.setEvent(eventsList);
		return clause;
	}

	private PEventstatus extractEventStatus(final ISCEvent revent)
			throws TranslationFailedException, CoreException {
		Convergence convergence = revent.getConvergence();
		PEventstatus status;
		switch (convergence) {
		case ORDINARY:
			status = new AOrdinaryEventstatus();
			break;
		case ANTICIPATED:
			status = new AAnticipatedEventstatus();
			break;
		case CONVERGENT:
			status = new AConvergentEventstatus();
			break;
		default:
			throw new TranslationFailedException(machine.getComponentName(),
					"unexpected convergent status " + convergence);
		}
		return status;
	}

	private List<TIdentifierLiteral> extractRefinedEvents(final ISCEvent revent)
			throws CoreException {
		final ISCRefinesEvent[] refinesClauses = revent.getSCRefinesClauses();
		final List<TIdentifierLiteral> refines = new ArrayList<TIdentifierLiteral>(
				refinesClauses.length);
		for (final ISCRefinesEvent refinesEvent : refinesClauses) {
			final String label = refinesEvent.getAbstractSCEvent().getLabel();
			refines.add(new TIdentifierLiteral(label));
		}
		return refines;
	}

	private List<PExpression> extractParameters(final ISCEvent revent)
			throws RodinDBException {
		final ISCParameter[] parameters = revent.getSCParameters();
		final List<PExpression> parametersList = new ArrayList<PExpression>(
				parameters.length);
		for (final ISCParameter parameter : parameters) {
			final AIdentifierExpression id = new AIdentifierExpression(
					Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
							parameter.getIdentifierString()) }));
			parametersList.add(id);
			labelMapping.put(id, parameter);
		}
		return parametersList;
	}

	private void extractGuards(final ISCEvent revent,
			final ITypeEnvironment localEnv, final List<PPredicate> guardsList,
			final List<PPredicate> theoremsList) throws CoreException {
		final ISCGuard[] guards = revent.getSCGuards();
		for (final ISCGuard guard : guards) {
			final PPredicate predicate = translatePredicate(ff, localEnv, guard);
			if (guard.isTheorem()) {
				theoremsList.add(predicate);
			} else {
				guardsList.add(predicate);
			}
			labelMapping.put(predicate, guard);
		}
	}

	private AVariablesModelClause processVariables() throws RodinDBException {
		final ISCVariable[] variables = machine.getSCVariables();
		final AVariablesModelClause variablesModelClause = new AVariablesModelClause();

		final List<PExpression> list = new ArrayList<PExpression>(
				variables.length);
		for (final ISCVariable variable : variables) {
			if (variable.isConcrete()) {
				final TIdentifierLiteral literal = new TIdentifierLiteral(
						variable.getIdentifierString());
				final AIdentifierExpression id = new AIdentifierExpression(
						Arrays.asList(new TIdentifierLiteral[] { literal }));
				list.add(id);
			}
		}
		variablesModelClause.setIdentifiers(list);
		return variablesModelClause;
	}

	private List<PWitness> extractWitnesses(final ISCEvent revent,
			final ITypeEnvironment localEnv) throws CoreException {
		final ISCWitness[] witnesses = revent.getSCWitnesses();
		final List<PWitness> witnessList = new ArrayList<PWitness>(
				witnesses.length);
		for (final ISCWitness witness : witnesses) {
			final PPredicate predicate = translatePredicate(ff, localEnv,
					witness);
			final TIdentifierLiteral label = new TIdentifierLiteral(
					witness.getLabel());
			witnessList.add(new AWitness(label, predicate));
			labelMapping.put(predicate, witness);
		}
		return witnessList;
	}

	private List<PSubstitution> extractActions(final ISCEvent revent,
			final ITypeEnvironment localEnv) throws RodinDBException {
		final ISCAction[] actions = revent.getSCActions();
		final List<PSubstitution> actionList = new ArrayList<PSubstitution>();
		for (final ISCAction action : actions) {
			final Assignment assignment = action.getAssignment(ff, localEnv);
			final PSubstitution substitution = TranslationVisitor
					.translateAssignment(assignment);
			actionList.add(substitution);
			labelMapping.put(substitution, action);
		}
		return actionList;
	}

	private AInvariantModelClause processInvariants() throws CoreException {
		final AInvariantModelClause invariantModelClause = new AInvariantModelClause();
		invariantModelClause.setPredicates(getPredicateList(
				machine.getSCInvariants(), false));
		return invariantModelClause;
	}

	private ATheoremsModelClause processTheorems() throws CoreException {
		final ATheoremsModelClause theoremsModelClause = new ATheoremsModelClause();
		theoremsModelClause.setPredicates(getPredicateList(
				machine.getSCInvariants(), true));
		return theoremsModelClause;
	}

	/**
	 * Generates a list of classical B predicates from a list of invariants or
	 * theorems Note: Aside from {@link ISCPredicateElement}, each element must
	 * additionally implement {@link ITraceableElement}, such as
	 * {@link ISCInvariant} does.
	 * 
	 * @param predicates
	 *            Array of static checked predicated taken from the invariants
	 * @param theorems
	 *            true, if the returned list should only contain theorems,
	 *            false, if all theorems shall be filtered out
	 * @return
	 * @throws RodinDBException
	 */
	private List<PPredicate> getPredicateList(final ISCInvariant[] predicates,
			final boolean theorems) throws CoreException {
		final List<PPredicate> list = new ArrayList<PPredicate>(
				predicates.length);
		for (final ISCInvariant evPredicate : predicates) {
			// skip, if we want invariants and this is a theorem or vice versa
			if (evPredicate.isTheorem() != theorems) {
				continue;
			}
			// only use predicates that are defined in the current refinement
			// level, not in an abstract machine
			if (!isDefinedInAbstraction(evPredicate)) {
				final PPredicate predicate = translatePredicate(ff, te,
						evPredicate);
				list.add(predicate);
				labelMapping.put(predicate, evPredicate);
			}
		}
		return list;
	}

	private boolean isDefinedInAbstraction(final ITraceableElement element)
			throws CoreException {
		final IRodinElement parentsource = element.getSource().getParent();
		final boolean result;

		if (parentsource instanceof IMachineRoot) {
			IMachineRoot src = (IMachineRoot) parentsource;

			// do a finer level check
			String srcName = src.getRodinFile().getBareName();

			// is the source one of the refined machines?
			for (IRodinFile abstr : machine.getAbstractSCMachines())
				if (abstr.getBareName().equals(srcName))
					return true;

			result = false;

			// result = !machine.getRodinFile().getBareName()
			// .equals(src.getRodinFile().getBareName());
		} else {
			result = false;
		}
		return result;
	}

	@Override
	public Node getAST() {
		return getModelAST();
	}
}
