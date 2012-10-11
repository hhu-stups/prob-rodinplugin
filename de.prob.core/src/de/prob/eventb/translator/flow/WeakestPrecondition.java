package de.prob.eventb.translator.flow;

import java.util.LinkedList;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.analysis.prolog.ClassicalPositionPrinter;
import de.be4.classicalb.core.parser.analysis.prolog.NodeIdAssignment;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.eventb.translator.PredicateVisitor;
import de.prob.prolog.output.IPrologTermOutput;

public class WeakestPrecondition {

	private final Event first;
	private final Event second;
	private Predicate wps;

	private WeakestPrecondition() {
		throw new UnsupportedOperationException(
				"Use Factory Method create(EventTuple) to get an instance");
	}

	private WeakestPrecondition(final EventTuple tuple) {
		first = tuple.getFirst();
		second = tuple.getSecond();
		wps = second.getSubstGuards(first);
	}

	public Predicate getPredicates() {
		return wps;
	}

	public static WeakestPrecondition create(final EventTuple tuple) {
		WeakestPrecondition weakestPrecondition = new WeakestPrecondition(tuple);
		return weakestPrecondition;
	}

	@Override
	public String toString() {
		return "['" + wps + "']";
	}

	public void getSyntaxTree(final IPrologTermOutput pout) {
		final ASTProlog prolog = new ASTProlog(pout,
				new ClassicalPositionPrinter(new NodeIdAssignment()));
		pout.openList();
		// pout.openTerm("entry");
		// pout.printAtom(ReverseTranslate.reverseTranslate(p.toString()));
		PredicateVisitor pv = new PredicateVisitor(new LinkedList<String>(),
				FormulaFactory.getDefault(), FormulaFactory.getDefault()
						.makeTypeEnvironment());
		wps.accept(pv);
		PPredicate predicate = pv.getPredicate();
		predicate.apply(prolog);
		// pout.closeTerm();
		pout.closeList();
	}
}
