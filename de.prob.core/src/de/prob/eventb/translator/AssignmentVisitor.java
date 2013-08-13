/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.node.AAssignSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesElementOfSubstitution;
import de.be4.classicalb.core.parser.node.ABecomesSuchSubstitution;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PSubstitution;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.prob.eventb.translator.internal.SimpleVisitorAdapter;
import de.prob.eventb.translator.internal.TranslationVisitor;

/**
 * @deprecated Use {@link TranslationVisitor} instead
 */
@Deprecated
public class AssignmentVisitor extends SimpleVisitorAdapter implements
		ISimpleVisitor {

	private PSubstitution sub;
	private boolean substitutonSet = false;

	public PSubstitution getSubstitution() {
		return sub;
	}

	public void setSub(final PSubstitution sub) {
		if (substitutonSet) {
			throw new AssertionError("The Visitor must not be used twice!");
		}
		substitutonSet = true;
		this.sub = sub;
	}

	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		final FreeIdentifier[] identifiers = assignment
				.getAssignedIdentifiers();
		final Expression[] expressions = assignment.getExpressions();
		final AAssignSubstitution assignSubstitution = new AAssignSubstitution();
		assignSubstitution
				.setLhsExpression(createListOfIdentifiers(identifiers));
		assignSubstitution
				.setRhsExpressions(createListOfExpressions(expressions));
		setSub(assignSubstitution);
	}

	private List<PExpression> createListOfExpressions(
			final Expression[] expressions) {
		final List<PExpression> list = new ArrayList<PExpression>();
		for (Expression e : expressions) {
			final ExpressionVisitor visitor = new ExpressionVisitor(
					new LinkedList<String>());
			e.accept(visitor);
			list.add(visitor.getExpression());
		}
		return list;
	}

	private List<PExpression> createListOfIdentifiers(
			final FreeIdentifier[] identifiers) {
		final List<PExpression> res = new ArrayList<PExpression>(
				identifiers.length);
		for (FreeIdentifier freeIdentifier : identifiers) {
			final List<TIdentifierLiteral> list = Arrays
					.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
							freeIdentifier.getName()) });
			final AIdentifierExpression expression = new AIdentifierExpression();
			expression.setIdentifier(list);
			res.add(expression);
		}
		return res;
	}

	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		final ABecomesElementOfSubstitution becomesElementOfSubstitution = new ABecomesElementOfSubstitution();
		final FreeIdentifier[] identifiers = assignment
				.getAssignedIdentifiers();
		final Expression set = assignment.getSet();

		final ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>());
		set.accept(visitor);

		becomesElementOfSubstitution
				.setIdentifiers(createListOfIdentifiers(identifiers));
		becomesElementOfSubstitution.setSet(visitor.getExpression());
		setSub(becomesElementOfSubstitution);
	}

	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		final FreeIdentifier[] identifiers = assignment
				.getAssignedIdentifiers();
		LinkedList<String> list = new LinkedList<String>();
		for (FreeIdentifier f : identifiers) {
			list.addFirst(f.getName() + "'");
		}
		final Predicate predicate = assignment.getCondition();
		final PredicateVisitor visitor = new PredicateVisitor(list);
		predicate.accept(visitor);
		final ABecomesSuchSubstitution becomesSuchSubstitution = new ABecomesSuchSubstitution();
		becomesSuchSubstitution
				.setIdentifiers(createListOfIdentifiers(identifiers));
		becomesSuchSubstitution.setPredicate(visitor.getPredicate());
		setSub(becomesSuchSubstitution);

	}
}
