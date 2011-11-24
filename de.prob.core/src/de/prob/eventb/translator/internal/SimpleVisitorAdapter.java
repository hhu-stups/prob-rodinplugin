/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator.internal;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;

public class SimpleVisitorAdapter implements ISimpleVisitor {

	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		// Default implementation does nothing
	}

	public void visitAssociativePredicate(final AssociativePredicate predicate) {
		// Default implementation does nothing

	}

	public void visitAtomicExpression(final AtomicExpression expression) {
		// Default implementation does nothing

	}

	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {

		// Default implementation does nothing
	}

	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		// Default implementation does nothing

	}

	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		// Default implementation does nothing

	}

	public void visitBinaryExpression(final BinaryExpression expression) {

		// Default implementation does nothing
	}

	public void visitBinaryPredicate(final BinaryPredicate predicate) {
		// Default implementation does nothing

	}

	public void visitBoolExpression(final BoolExpression expression) {
		// Default implementation does nothing

	}

	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		// Default implementation does nothing

	}

	public void visitBoundIdentifier(final BoundIdentifier identifierExpression) {
		// Default implementation does nothing

	}

	public void visitFreeIdentifier(final FreeIdentifier identifierExpression) {
		// Default implementation does nothing

	}

	public void visitIntegerLiteral(final IntegerLiteral expression) {
		// Default implementation does nothing

	}

	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		// Default implementation does nothing

	}

	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		// Default implementation does nothing

	}

	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		// Default implementation does nothing

	}

	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		// Default implementation does nothing

	}

	public void visitSetExtension(final SetExtension expression) {
		// Default implementation does nothing

	}

	public void visitSimplePredicate(final SimplePredicate predicate) {
		// Default implementation does nothing

	}

	public void visitUnaryExpression(final UnaryExpression expression) {
		// Default implementation does nothing

	}

	public void visitUnaryPredicate(final UnaryPredicate predicate) {
		// Default implementation does nothing

	}

	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		// Default implementation does nothing

	}

	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		// Default implementation does nothing
	}

	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		// Default implementation does nothing
	}

}
