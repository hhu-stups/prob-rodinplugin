/**
 * 
 */
package de.prob.sap.util;

import java.util.LinkedList;

import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.prob.eventb.translator.PredicateVisitor;
import de.prob.prolog.output.IPrologTermOutput;

/**
 * Some helper methods for handling formulas
 * 
 * @author plagge
 */
public final class FormulaUtils {

	/**
	 * Write a Event-B predicate to a Prolog output
	 * 
	 * @param predicate
	 * @param pto
	 */
	static public void printPredicate(final Predicate predicate,
			final IPrologTermOutput pto) {
		final PredicateVisitor visitor = new PredicateVisitor(
				new LinkedList<String>());
		predicate.accept(visitor);
		final PPredicate probPredicate = visitor.getPredicate();
		final ASTProlog prolog = new ASTProlog(pto, null);
		probPredicate.apply(prolog);
	}
}
