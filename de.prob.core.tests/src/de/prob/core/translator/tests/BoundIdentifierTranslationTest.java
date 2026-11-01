package de.prob.core.translator.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.Type;
import org.junit.jupiter.api.Test;

import de.be4.classicalb.core.parser.analysis.DepthFirstAdapter;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.eventb.translator.internal.TranslationVisitor;

/**
 * Rodin refers to bound identifiers by index, so a bound identifier may have
 * the same name as a free identifier in its scope. The translation must not
 * let the bound identifier capture the free one.
 */
public class BoundIdentifierTranslationTest {
	private final FormulaFactory ff = FormulaFactory.getDefault();
	private final Type integer = ff.makeIntegerType();

	@Test
	public void testUnusedNameIsKept() {
		// ∀x·x>y
		final Predicate p = forall("x",
				ff.makeRelationalPredicate(Formula.GT, bound(0), free("y"),
						null));
		assertEquals(Arrays.asList("x", "x", "x", "y"), identifiers(p));
	}

	@Test
	public void testBoundIdentifierDoesNotCaptureFreeIdentifier() {
		// ∀x·x>x where the second x is free, e.g. a substituted parameter
		final Predicate p = forall("x",
				ff.makeRelationalPredicate(Formula.GT, bound(0), free("x"),
						null));
		assertEquals(Arrays.asList("x0", "x0", "x0", "x"), identifiers(p));
	}

	@Test
	public void testFreshNameAvoidsOtherFreeIdentifiers() {
		// ∀x·x>x+x0 where x and x0 are free
		final Predicate p = forall("x", ff.makeRelationalPredicate(
				Formula.GT,
				bound(0),
				ff.makeAssociativeExpression(Formula.PLUS, new Expression[] {
						free("x"), free("x0") }, null), null));
		assertEquals(Arrays.asList("x1", "x1", "x1", "x", "x0"),
				identifiers(p));
	}

	@Test
	public void testInnerBoundIdentifierDoesNotHideOuterOne() {
		// ∃x·∀x·x=x where the second x refers to the outer quantifier
		final Predicate inner = forall("x",
				ff.makeRelationalPredicate(Formula.EQUAL, bound(0), bound(1),
						null));
		final Predicate p = ff.makeQuantifiedPredicate(Formula.EXISTS,
				new BoundIdentDecl[] { ff.makeBoundIdentDecl("x", null,
						integer) }, inner, null);
		assertEquals(Arrays.asList("x", "x", "x0", "x0", "x0", "x"),
				identifiers(p));
	}

	private Predicate forall(final String name, final Predicate body) {
		return ff.makeQuantifiedPredicate(Formula.FORALL,
				new BoundIdentDecl[] { ff.makeBoundIdentDecl(name, null,
						integer) }, body, null);
	}

	private Expression bound(final int index) {
		return ff.makeBoundIdentifier(index, null, integer);
	}

	private Expression free(final String name) {
		return ff.makeFreeIdentifier(name, null, integer);
	}

	/**
	 * @return the identifiers of the translated predicate in the order they
	 *         appear, including the declarations and typing predicates
	 */
	private static List<String> identifiers(final Predicate p) {
		final List<String> result = new ArrayList<String>();
		TranslationVisitor.translatePredicate(p).apply(
				new DepthFirstAdapter() {
					@Override
					public void inAIdentifierExpression(
							final AIdentifierExpression node) {
						final StringBuilder sb = new StringBuilder();
						for (TIdentifierLiteral l : node.getIdentifier()) {
							sb.append(l.getText());
						}
						result.add(sb.toString());
					}
				});
		return result;
	}
}
