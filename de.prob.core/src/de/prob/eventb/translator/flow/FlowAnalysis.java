package de.prob.eventb.translator.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eventb.core.ISCEvent;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCVariable;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ITypeEnvironment;
import org.eventb.core.ast.Predicate;
import org.rodinp.core.RodinDBException;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.analysis.prolog.ClassicalPositionPrinter;
import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.eventb.translator.internal.TranslationVisitor;
import de.prob.prolog.output.IPrologTermOutput;

public class FlowAnalysis {

	public final FormulaFactory FF = FormulaFactory.getDefault();
	private final ITypeEnvironment typeEnvironment;
	private final Map<FreeIdentifier, Integer> identifiers;
	private final List<Event> events;
	private final Map<EventTuple, WeakestPrecondition> edges;
	private final List<EventTuple> noEffect;

	public void printGraph(final IPrologTermOutput pout) {

		final Set<EventTuple> entries = edges.keySet();
		EventTuple[] tuples = entries.toArray(new EventTuple[entries.size()]);
		Arrays.sort(tuples);
		for (EventTuple eventTuple : tuples) {
			pout.openTerm("wp").printAtom(eventTuple.getFirst().toString())
					.printAtom(eventTuple.getSecond().toString());
			final WeakestPrecondition weakestPrecondition = edges
					.get(eventTuple);

			weakestPrecondition.getSyntaxTree(pout);
			pout.closeTerm();
		}
		for (Event evt : events) {
			final ASTProlog prolog = new ASTProlog(pout,
					new ClassicalPositionPrinter(new NodeIdAssignment()));
			pout.openTerm("nonchanging_guard");
			pout.printAtom(evt.toString());
			pout.openList();
			final Predicate predicate = evt.getGuardsAfterAssignment();
			final PPredicate p = TranslationVisitor
					.translatePredicate(predicate);
			p.apply(prolog);
			pout.closeList();
			pout.closeTerm();
		}

	}

	public FlowAnalysis(final ISCMachineRoot model) throws RodinDBException {
		this.typeEnvironment = model.getTypeEnvironment(FF);
		this.identifiers = enumerateVariables(model.getSCVariables());
		this.events = createEvents(model);
		noEffect = new ArrayList<EventTuple>();
		this.edges = calculateInputGraph(noEffect);
	}

	private Map<EventTuple, WeakestPrecondition> calculateInputGraph(
			final List<EventTuple> noEffect) {
		Map<EventTuple, WeakestPrecondition> wps = new HashMap<EventTuple, WeakestPrecondition>();
		for (Event e : events) {
			for (Event f : events) {
				EventTuple tuple = new EventTuple(e, f);
				if (e.effects(f)) {
					WeakestPrecondition weakestPrecondition = WeakestPrecondition
							.create(tuple);
					wps.put(tuple, weakestPrecondition);
				} else {
					noEffect.add(tuple);
				}
			}
		}
		return Collections.unmodifiableMap(wps);
	}

	private ArrayList<Event> createEvents(final ISCMachineRoot model)
			throws RodinDBException {
		ISCEvent[] revents = model.getSCEvents();
		final ArrayList<Event> arrayList = new ArrayList<Event>(revents.length);

		for (ISCEvent e1 : revents) {
			Event event = Event.create(e1, this);
			arrayList.add(event);
		}
		return arrayList;
	}

	public Map<FreeIdentifier, Integer> getVariables() throws RodinDBException {
		return identifiers;
	}

	public ITypeEnvironment getTypeEnvironment() {
		return typeEnvironment;
	}

	private Map<FreeIdentifier, Integer> enumerateVariables(
			final ISCVariable[] variables) throws RodinDBException {
		Map<FreeIdentifier, Integer> varNumber = new HashMap<FreeIdentifier, Integer>();
		int count = 0;
		for (ISCVariable variable : variables) {
			varNumber.put(variable.getIdentifier(FF), count++);
		}
		final Map<FreeIdentifier, Integer> result = Collections
				.unmodifiableMap(varNumber);
		return result;
	}
}
