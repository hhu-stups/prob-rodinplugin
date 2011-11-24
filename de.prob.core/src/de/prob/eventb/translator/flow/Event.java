package de.prob.eventb.translator.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eventb.core.ISCAction;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCParameter;
import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.rodinp.core.RodinDBException;

public class Event {

	private final String name;
	private final ITypeEnvironment localTypeEnv;
	private final Map<FreeIdentifier, Integer> variables;
	private final BitSet reads;
	private final BitSet writes;
	private final List<Assignment> assignements;
	private final Predicate guards;
	private final FlowAnalysis analysis;
	private FreeIdentifier[] freeIdentifiers;
	private BoundIdentDecl[] boundIdentifiers;

	private Event() {
		throw new UnsupportedOperationException(
				"Use Factory Method create(ISCEvent) to get an instance");
	}

	@Override
	public String toString() {
		return name;
	}

	private Event(final ISCEvent evt, final FlowAnalysis analysis)
			throws RodinDBException {
		this.analysis = analysis;
		this.name = evt.getLabel();
		this.localTypeEnv = generateLocalTypeEnvironment(evt, analysis);
		this.variables = analysis.getVariables();

		ISCParameter[] parameters = evt.getSCParameters();

		freeIdentifiers = new FreeIdentifier[parameters.length];
		boundIdentifiers = new BoundIdentDecl[parameters.length];
		for (int k = 0; k < parameters.length; k++) {
			freeIdentifiers[k] = parameters[k].getIdentifier(analysis.FF);
			boundIdentifiers[k] = analysis.FF.makeBoundIdentDecl(
					freeIdentifiers[k].getName(), null,
					freeIdentifiers[k].getType());
		}

		this.guards = getGuard(analysis, evt);
		this.reads = createReadSet(guards);

		this.assignements = getAssignments(analysis,
				Arrays.asList(evt.getSCActions()));
		this.writes = createWriteSet(assignements);
	}

	private Predicate getGuard(final FlowAnalysis analysis, ISCEvent event)
			throws RodinDBException {
		ISCParameter[] parameters = event.getSCParameters();
		ISCGuard[] guards = event.getSCGuards();

		FormulaFactory factory = analysis.FF;

		ITypeEnvironment typenv = factory.makeTypeEnvironment();
		typenv.addAll(analysis.getTypeEnvironment());
		typenv.addAll(freeIdentifiers);

		Predicate[] gPreds = new Predicate[guards.length];
		for (int k = 0; k < guards.length; k++) {
			gPreds[k] = guards[k].getPredicate(factory, typenv);
		}

		if (guards.length == 0)
			return factory.makeLiteralPredicate(Predicate.BTRUE, null);
		Predicate conjPred = guards.length == 1 ? gPreds[0] : factory
				.makeAssociativePredicate(Predicate.LAND, gPreds, null);
		Predicate predicate = parameters.length == 0 ? conjPred : factory
				.makeQuantifiedPredicate(Predicate.EXISTS, boundIdentifiers,
						conjPred.bindTheseIdents(
								Arrays.asList(freeIdentifiers), factory), null);
		return predicate;

	}

	public List<Assignment> getAssignements() {
		return assignements;
	}

	private List<Assignment> getAssignments(final FlowAnalysis analysis,
			final List<ISCAction> actions) throws RodinDBException {
		List<Assignment> assignements = new ArrayList<Assignment>();
		for (ISCAction action : actions) {
			assignements.add(action.getAssignment(analysis.FF, localTypeEnv));
		}
		return Collections.unmodifiableList(assignements);
	}

	public boolean effects(final Event e) {
		return writes.intersects(e.getReads());
	}

	public BitSet getReads() {
		return reads;
	}

	public BitSet getWrites() {
		return writes;
	}

	private BitSet createWriteSet(final List<Assignment> actions)
			throws RodinDBException {
		BitSet writeset = new BitSet();
		for (Assignment assignment : actions) {
			FreeIdentifier[] identifiers = assignment.getAssignedIdentifiers();
			for (FreeIdentifier identifier : identifiers) {
				Integer nr = variables.get(identifier);
				writeset.set(nr);
			}
		}
		return writeset;
	}

	private BitSet createReadSet(final Predicate predicate)
			throws RodinDBException {
		BitSet result = new BitSet();
		result.or(calculateReadSet(predicate));
		return result;
	}

	private BitSet calculateReadSet(final Predicate predicate) {
		BitSet readset = new BitSet();
		FreeIdentifier[] identifiers = predicate
				.getSyntacticallyFreeIdentifiers();
		for (FreeIdentifier identifier : identifiers) {
			Integer nr = variables.get(identifier);
			if (nr != null) {
				readset.set(nr);
			}
		}
		return readset;
	}

	private ITypeEnvironment generateLocalTypeEnvironment(final ISCEvent evt,
			final FlowAnalysis analysis) throws RodinDBException {
		final ITypeEnvironment globalTypeEnvironment = analysis
				.getTypeEnvironment();
		final ITypeEnvironment typeEnvironment = evt.getTypeEnvironment(
				globalTypeEnvironment, analysis.FF);
		typeEnvironment.addAll(globalTypeEnvironment);
		return typeEnvironment;
	}

	public static Event create(final ISCEvent evt, final FlowAnalysis analysis)
			throws RodinDBException {
		Event event = new Event(evt, analysis);
		return event;
	}

	public Predicate getSubstGuards(final Event other) {
		Predicate p = this.guards;
		for (Assignment assignment : other.assignements) {
			List<BecomesEqualTo> deterministic = new ArrayList<BecomesEqualTo>();
			if (assignment instanceof BecomesEqualTo) {
				deterministic.add((BecomesEqualTo) assignment);
			} else {
				// FIXME We need to deal with non deterministic assignments
				// here
			}
			p = p.applyAssignments(deterministic, analysis.FF);
		}

		return p;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Event) {
			Event other = (Event) obj;
			return this.name.equals(other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public Predicate getGuardsAfterAssignment() {
		List<Predicate> result = new ArrayList<Predicate>();
		Predicate[] grd = unpackPredicates(guards);
		for (Predicate g : grd) {
			BitSet readSet = calculateReadSet(g);
			if (!readSet.intersects(writes)) {
				result.add(g);
			}
		}

		return packPredicates(result);
	}

	private Predicate[] unpackPredicates(Predicate guards) {
		Predicate g;
		Predicate[] preds;
		if (guards instanceof QuantifiedPredicate) {
			QuantifiedPredicate qp = (QuantifiedPredicate) guards;
			g = qp.getPredicate();
		} else {
			g = guards;
		}

		if (g instanceof AssociativePredicate) {
			AssociativePredicate ass = (AssociativePredicate) g;
			preds = ass.getChildren();
		} else {
			preds = new Predicate[] { g };
		}
		return preds;
	}

	private Predicate packPredicates(List<Predicate> preds) {
		FormulaFactory factory = analysis.FF;
		Predicate conjPred = preds.size() == 1 ? preds.get(0) : factory
				.makeAssociativePredicate(Predicate.LAND, preds, null);
		Predicate predicate = freeIdentifiers.length == 0 ? conjPred : factory
				.makeQuantifiedPredicate(Predicate.EXISTS, boundIdentifiers,
						conjPred, null);
		return predicate;
	}

}
