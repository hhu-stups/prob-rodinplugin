/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator; // NOPMD
// high coupling is ok, we only use the AST nodes

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.UnaryExpression;

import de.be4.classicalb.core.parser.node.AAddExpression;
import de.be4.classicalb.core.parser.node.ABoolSetExpression;
import de.be4.classicalb.core.parser.node.ABooleanFalseExpression;
import de.be4.classicalb.core.parser.node.ABooleanTrueExpression;
import de.be4.classicalb.core.parser.node.ACardExpression;
import de.be4.classicalb.core.parser.node.ACartesianProductExpression;
import de.be4.classicalb.core.parser.node.ACompositionExpression;
import de.be4.classicalb.core.parser.node.AConvertBoolExpression;
import de.be4.classicalb.core.parser.node.ACoupleExpression;
import de.be4.classicalb.core.parser.node.ADirectProductExpression;
import de.be4.classicalb.core.parser.node.ADivExpression;
import de.be4.classicalb.core.parser.node.ADomainExpression;
import de.be4.classicalb.core.parser.node.ADomainRestrictionExpression;
import de.be4.classicalb.core.parser.node.ADomainSubtractionExpression;
import de.be4.classicalb.core.parser.node.AEmptySetExpression;
import de.be4.classicalb.core.parser.node.AEventBComprehensionSetExpression;
import de.be4.classicalb.core.parser.node.AEventBFirstProjectionExpression;
import de.be4.classicalb.core.parser.node.AEventBFirstProjectionV2Expression;
import de.be4.classicalb.core.parser.node.AEventBIdentityExpression;
import de.be4.classicalb.core.parser.node.AEventBSecondProjectionExpression;
import de.be4.classicalb.core.parser.node.AEventBSecondProjectionV2Expression;
import de.be4.classicalb.core.parser.node.AFunctionExpression;
import de.be4.classicalb.core.parser.node.AGeneralIntersectionExpression;
import de.be4.classicalb.core.parser.node.AGeneralUnionExpression;
import de.be4.classicalb.core.parser.node.AIdentifierExpression;
import de.be4.classicalb.core.parser.node.AIdentityExpression;
import de.be4.classicalb.core.parser.node.AImageExpression;
import de.be4.classicalb.core.parser.node.AIntegerExpression;
import de.be4.classicalb.core.parser.node.AIntegerSetExpression;
import de.be4.classicalb.core.parser.node.AIntersectionExpression;
import de.be4.classicalb.core.parser.node.AIntervalExpression;
import de.be4.classicalb.core.parser.node.AMaxExpression;
import de.be4.classicalb.core.parser.node.AMinExpression;
import de.be4.classicalb.core.parser.node.AMinusExpression;
import de.be4.classicalb.core.parser.node.AModuloExpression;
import de.be4.classicalb.core.parser.node.AMultiplicationExpression;
import de.be4.classicalb.core.parser.node.ANatural1SetExpression;
import de.be4.classicalb.core.parser.node.ANaturalSetExpression;
import de.be4.classicalb.core.parser.node.AOverwriteExpression;
import de.be4.classicalb.core.parser.node.AParallelProductExpression;
import de.be4.classicalb.core.parser.node.APartialFunctionExpression;
import de.be4.classicalb.core.parser.node.APartialInjectionExpression;
import de.be4.classicalb.core.parser.node.APartialSurjectionExpression;
import de.be4.classicalb.core.parser.node.APow1SubsetExpression;
import de.be4.classicalb.core.parser.node.APowSubsetExpression;
import de.be4.classicalb.core.parser.node.APowerOfExpression;
import de.be4.classicalb.core.parser.node.APredecessorExpression;
import de.be4.classicalb.core.parser.node.AQuantifiedIntersectionExpression;
import de.be4.classicalb.core.parser.node.AQuantifiedUnionExpression;
import de.be4.classicalb.core.parser.node.ARangeExpression;
import de.be4.classicalb.core.parser.node.ARangeRestrictionExpression;
import de.be4.classicalb.core.parser.node.ARangeSubtractionExpression;
import de.be4.classicalb.core.parser.node.ARelationsExpression;
import de.be4.classicalb.core.parser.node.AReverseExpression;
import de.be4.classicalb.core.parser.node.ARingExpression;
import de.be4.classicalb.core.parser.node.ASetExtensionExpression;
import de.be4.classicalb.core.parser.node.ASetSubtractionExpression;
import de.be4.classicalb.core.parser.node.ASuccessorExpression;
import de.be4.classicalb.core.parser.node.ASurjectionRelationExpression;
import de.be4.classicalb.core.parser.node.ATotalBijectionExpression;
import de.be4.classicalb.core.parser.node.ATotalFunctionExpression;
import de.be4.classicalb.core.parser.node.ATotalInjectionExpression;
import de.be4.classicalb.core.parser.node.ATotalRelationExpression;
import de.be4.classicalb.core.parser.node.ATotalSurjectionExpression;
import de.be4.classicalb.core.parser.node.ATotalSurjectionRelationExpression;
import de.be4.classicalb.core.parser.node.AUnaryMinusExpression;
import de.be4.classicalb.core.parser.node.AUnionExpression;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.be4.classicalb.core.parser.node.TIdentifierLiteral;
import de.be4.classicalb.core.parser.node.TIntegerLiteral;
import de.prob.eventb.translator.internal.SimpleVisitorAdapter;

// Complexity is actually quite low for a visitor ;-)
public class ExpressionVisitor extends SimpleVisitorAdapter implements // NOPMD
		// by bendisposto
		ISimpleVisitor {

	private PExpression e;
	private final LinkedList<String> bounds; // NOPMD bendisposto
	// we need some abilities of the linked list, using List is not an option
	private boolean expressionSet = false;

	@SuppressWarnings("unused")
	private ExpressionVisitor() { // we want to prevent clients from calling
		// the default constructor
		super();
		throw new AssertionError("Do not call this constructor");
	}

	public ExpressionVisitor(final LinkedList<String> bounds) { // NOPMD
		super();
		this.bounds = bounds;
	}

	public PExpression getExpression() {
		return e;
	}

	public void setExpression(final PExpression e) {
		if (expressionSet) {
			throw new AssertionError("The Visitor must not be used twice!");
		}
		expressionSet = true;
		this.e = e;
	}

	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		// QUNION, QINTER, CSET
		final int tag = expression.getTag();

		final List<ExpressionVisitor> ev = new LinkedList<ExpressionVisitor>();

		final BoundIdentDecl[] decls = expression.getBoundIdentDecls();
		for (final BoundIdentDecl boundIdentDecl : decls) {
			final ExpressionVisitor visitor = new ExpressionVisitor(bounds);
			boundIdentDecl.accept(visitor);
			ev.add(visitor);
			bounds.addFirst(boundIdentDecl.getName());
		}

		// Collect Subtrees in a list.
		final LinkedList<PExpression> list = new LinkedList<PExpression>();
		for (final ExpressionVisitor visitor : ev) {
			list.add(visitor.getExpression());
		}

		// Process internal Expression and Predcate
		final Predicate predicate = expression.getPredicate();
		final PredicateVisitor predicateVisitor = new PredicateVisitor(bounds);
		predicate.accept(predicateVisitor);

		final PPredicate pr = predicateVisitor.getPredicate();

		final ExpressionVisitor expressionVisitor = new ExpressionVisitor(
				bounds);
		expression.getExpression().accept(expressionVisitor);

		final PExpression ex = expressionVisitor.getExpression();

		switch (tag) {
		case Formula.QUNION:
			final AQuantifiedUnionExpression quantifiedUnionExpression = new AQuantifiedUnionExpression();
			quantifiedUnionExpression.setExpression(ex);
			quantifiedUnionExpression.setPredicates(pr);
			quantifiedUnionExpression.setIdentifiers(list);
			setExpression(quantifiedUnionExpression);
			break;
		case Formula.QINTER:
			final AQuantifiedIntersectionExpression quantifiedIntersectionExpression = new AQuantifiedIntersectionExpression();
			quantifiedIntersectionExpression.setExpression(ex);
			quantifiedIntersectionExpression.setPredicates(pr);
			quantifiedIntersectionExpression.setIdentifiers(list);
			setExpression(quantifiedIntersectionExpression);
			break;
		case Formula.CSET:
			final AEventBComprehensionSetExpression comprehensionSetExpression = new AEventBComprehensionSetExpression();
			comprehensionSetExpression.setExpression(ex);
			comprehensionSetExpression.setPredicates(pr);
			comprehensionSetExpression.setIdentifiers(list);
			setExpression(comprehensionSetExpression);
			break;
		default:
			break;
		}

		for (int i = 0; i < decls.length; i++) {
			bounds.remove(0);
		}
	}

	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		// BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL
		final Expression[] children = expression.getChildren();

		final LinkedList<ExpressionVisitor> ev = new LinkedList<ExpressionVisitor>();

		for (final Expression ex : children) {
			final ExpressionVisitor e = new ExpressionVisitor(bounds);
			ev.add(e);
			ex.accept(e);
		}

		final int tag = expression.getTag();
		switch (tag) {
		case Formula.BUNION:
			setExpression(recurseBUNION(ev));
			break;
		case Formula.BINTER:
			setExpression(recurseBINTER(ev));
			break;
		case Formula.BCOMP:
			setExpression(recurseBCOMP(ev));
			break;
		case Formula.FCOMP:
			setExpression(recurseFCOMP(ev));
			break;
		case Formula.OVR:
			setExpression(recurseOVR(ev));
			break;
		case Formula.PLUS:
			setExpression(recursePLUS(ev));
			break;
		case Formula.MUL:
			setExpression(recurseMUL(ev));
			break;

		default:
			break;
		}
		super.visitAssociativeExpression(expression);
	}

	private PExpression recurseFCOMP(final List<ExpressionVisitor> list) {
		final ACompositionExpression r = new ACompositionExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseFCOMP(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recurseOVR(final List<ExpressionVisitor> list) {
		final AOverwriteExpression r = new AOverwriteExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseOVR(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recursePLUS(final List<ExpressionVisitor> list) {
		final AAddExpression r = new AAddExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recursePLUS(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recurseMUL(final List<ExpressionVisitor> list) {
		final AMultiplicationExpression r = new AMultiplicationExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseMUL(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recurseBUNION(final List<ExpressionVisitor> list) {
		final AUnionExpression r = new AUnionExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseBUNION(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recurseBINTER(final List<ExpressionVisitor> list) {
		final AIntersectionExpression r = new AIntersectionExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseBINTER(list.subList(1, list.size())));
		}
		return r;
	}

	private PExpression recurseBCOMP(final List<ExpressionVisitor> list) {
		final ARingExpression r = new ARingExpression();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getExpression());
			r.setRight(list.get(1).getExpression());
		} else {
			r.setLeft(list.get(0).getExpression());
			r.setRight(recurseBCOMP(list.subList(1, list.size())));
		}
		return r;
	}

	@Override
	// this long method is far easier to read than smaller ones
	public void visitBinaryExpression(final BinaryExpression expression) { // NOPMD
		final int tag = expression.getTag();
		final ExpressionVisitor visitorLeft = new ExpressionVisitor(bounds);
		final ExpressionVisitor visitorRight = new ExpressionVisitor(bounds);
		expression.getLeft().accept(visitorLeft);
		expression.getRight().accept(visitorRight);
		final PExpression exL = visitorLeft.getExpression();
		final PExpression exR = visitorRight.getExpression();

		switch (tag) {
		case Formula.MAPSTO:
			final ACoupleExpression coupleExpression = new ACoupleExpression();
			coupleExpression.setList(Arrays
					.asList(new PExpression[] { exL, exR }));
			setExpression(coupleExpression);
			break;
		case Formula.REL:
			final ARelationsExpression relationsExpression = new ARelationsExpression();
			relationsExpression.setLeft(exL);
			relationsExpression.setRight(exR);
			setExpression(relationsExpression);
			break;
		case Formula.TREL:
			final ATotalRelationExpression totalRelationExpression = new ATotalRelationExpression();
			totalRelationExpression.setLeft(exL);
			totalRelationExpression.setRight(exR);
			setExpression(totalRelationExpression);
			break;
		case Formula.SREL:
			final ASurjectionRelationExpression surjectionRelationExpression = new ASurjectionRelationExpression();
			surjectionRelationExpression.setLeft(exL);
			surjectionRelationExpression.setRight(exR);
			setExpression(surjectionRelationExpression);
			break;
		case Formula.STREL:
			final ATotalSurjectionRelationExpression totalSurjectionRelationExpression = new ATotalSurjectionRelationExpression();
			totalSurjectionRelationExpression.setLeft(exL);
			totalSurjectionRelationExpression.setRight(exR);
			setExpression(totalSurjectionRelationExpression);
			break;
		case Formula.PFUN:
			final APartialFunctionExpression partialFunctionExpression = new APartialFunctionExpression();
			partialFunctionExpression.setLeft(exL);
			partialFunctionExpression.setRight(exR);
			setExpression(partialFunctionExpression);
			break;
		case Formula.TFUN:
			final ATotalFunctionExpression totalFunctionExpression = new ATotalFunctionExpression();
			totalFunctionExpression.setLeft(exL);
			totalFunctionExpression.setRight(exR);
			setExpression(totalFunctionExpression);
			break;
		case Formula.PINJ:
			final APartialInjectionExpression partialInjectionExpression = new APartialInjectionExpression();
			partialInjectionExpression.setLeft(exL);
			partialInjectionExpression.setRight(exR);
			setExpression(partialInjectionExpression);
			break;
		case Formula.TINJ:
			final ATotalInjectionExpression totalInjectionExpression = new ATotalInjectionExpression();
			totalInjectionExpression.setLeft(exL);
			totalInjectionExpression.setRight(exR);
			setExpression(totalInjectionExpression);
			break;
		case Formula.PSUR:
			final APartialSurjectionExpression partialSurjectionExpression = new APartialSurjectionExpression();
			partialSurjectionExpression.setLeft(exL);
			partialSurjectionExpression.setRight(exR);
			setExpression(partialSurjectionExpression);
			break;
		case Formula.TSUR:
			final ATotalSurjectionExpression totalSurjectionExpression = new ATotalSurjectionExpression();
			totalSurjectionExpression.setLeft(exL);
			totalSurjectionExpression.setRight(exR);
			setExpression(totalSurjectionExpression);
			break;
		case Formula.TBIJ:
			final ATotalBijectionExpression totalBijectionExpression = new ATotalBijectionExpression();
			totalBijectionExpression.setLeft(exL);
			totalBijectionExpression.setRight(exR);
			setExpression(totalBijectionExpression);
			break;
		case Formula.SETMINUS:
			final ASetSubtractionExpression setSubtractionExpression = new ASetSubtractionExpression();
			setSubtractionExpression.setLeft(exL);
			setSubtractionExpression.setRight(exR);
			setExpression(setSubtractionExpression);
			break;
		case Formula.CPROD:
			final ACartesianProductExpression mulExpression = new ACartesianProductExpression();
			mulExpression.setLeft(exL);
			mulExpression.setRight(exR);
			setExpression(mulExpression);
			break;
		case Formula.DPROD:
			final ADirectProductExpression directProductExpression = new ADirectProductExpression();
			directProductExpression.setLeft(exL);
			directProductExpression.setRight(exR);
			setExpression(directProductExpression);
			break;
		case Formula.PPROD:
			final AParallelProductExpression parallelProductExpression = new AParallelProductExpression();
			parallelProductExpression.setLeft(exL);
			parallelProductExpression.setRight(exR);
			setExpression(parallelProductExpression);
			break;
		case Formula.DOMRES:
			final ADomainRestrictionExpression domainRestrictionExpression = new ADomainRestrictionExpression();
			domainRestrictionExpression.setLeft(exL);
			domainRestrictionExpression.setRight(exR);
			setExpression(domainRestrictionExpression);
			break;
		case Formula.DOMSUB:
			final ADomainSubtractionExpression domainSubtractionExpression = new ADomainSubtractionExpression();
			domainSubtractionExpression.setLeft(exL);
			domainSubtractionExpression.setRight(exR);
			setExpression(domainSubtractionExpression);
			break;
		case Formula.RANRES:
			final ARangeRestrictionExpression rangeRestrictionExpression = new ARangeRestrictionExpression();
			rangeRestrictionExpression.setLeft(exL);
			rangeRestrictionExpression.setRight(exR);
			setExpression(rangeRestrictionExpression);
			break;
		case Formula.RANSUB:
			final ARangeSubtractionExpression rangeSubtractionExpression = new ARangeSubtractionExpression();
			rangeSubtractionExpression.setLeft(exL);
			rangeSubtractionExpression.setRight(exR);
			setExpression(rangeSubtractionExpression);
			break;
		case Formula.UPTO:
			final AIntervalExpression intervalExpression = new AIntervalExpression();
			intervalExpression.setLeftBorder(exL);
			intervalExpression.setRightBorder(exR);
			setExpression(intervalExpression);
			break;
		case Formula.MINUS:
			final AMinusExpression minusExpression = new AMinusExpression();
			minusExpression.setLeft(exL);
			minusExpression.setRight(exR);
			setExpression(minusExpression);
			break;
		case Formula.DIV:
			final ADivExpression divExpression = new ADivExpression();
			divExpression.setLeft(exL);
			divExpression.setRight(exR);
			setExpression(divExpression);
			break;
		case Formula.MOD:
			final AModuloExpression moduloExpression = new AModuloExpression();
			moduloExpression.setLeft(exL);
			moduloExpression.setRight(exR);
			setExpression(moduloExpression);
			break;
		case Formula.EXPN:
			final APowerOfExpression powerOfExpression = new APowerOfExpression();
			powerOfExpression.setLeft(exL);
			powerOfExpression.setRight(exR);
			setExpression(powerOfExpression);
			break;
		case Formula.FUNIMAGE:
			final AFunctionExpression functionExpression = new AFunctionExpression();
			functionExpression.setIdentifier(exL);
			functionExpression.setParameters(Arrays
					.asList(new PExpression[] { exR }));
			setExpression(functionExpression);
			break;
		case Formula.RELIMAGE:
			final AImageExpression imageExpression = new AImageExpression();
			imageExpression.setLeft(exL);
			imageExpression.setRight(exR);
			setExpression(imageExpression);
			break;
		default:
			throw new AssertionError("Uncovered Expression");
		}

	}

	@Override
	public void visitAtomicExpression(final AtomicExpression expression) { // NOPMD
		// by
		// bendisposto
		final int tag = expression.getTag();

		switch (tag) {
		case Formula.INTEGER:
			setExpression(new AIntegerSetExpression());
			break;
		case Formula.NATURAL:
			setExpression(new ANaturalSetExpression());
			break;
		case Formula.NATURAL1:
			setExpression(new ANatural1SetExpression());
			break;
		case Formula.BOOL:
			setExpression(new ABoolSetExpression());
			break;
		case Formula.TRUE:
			setExpression(new ABooleanTrueExpression());
			break;
		case Formula.FALSE:
			setExpression(new ABooleanFalseExpression());
			break;
		case Formula.EMPTYSET:
			setExpression(new AEmptySetExpression());
			break;
		case Formula.KPRED:
			setExpression(new APredecessorExpression());
			break;
		case Formula.KSUCC:
			setExpression(new ASuccessorExpression());
			break;
		case Formula.KPRJ1_GEN: // see task#215
			setExpression(new AEventBFirstProjectionV2Expression());
			break;
		case Formula.KPRJ2_GEN:
			setExpression(new AEventBSecondProjectionV2Expression());
			break;
		case Formula.KID_GEN:
			setExpression(new AEventBIdentityExpression());
			break;
		default:
			throw new AssertionError("Uncovered Expression " + expression);
		}
	}

	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		final AConvertBoolExpression convertBoolExpression = new AConvertBoolExpression();
		final PredicateVisitor visitor = new PredicateVisitor(bounds);
		expression.getPredicate().accept(visitor);
		convertBoolExpression.setPredicate(visitor.getPredicate());
		setExpression(convertBoolExpression);
	}

	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final List<TIdentifierLiteral> list = Arrays
				.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
						boundIdentDecl.getName()) });
		final AIdentifierExpression expression = new AIdentifierExpression();
		expression.setIdentifier(list);
		setExpression(expression);
	}

	@Override
	public void visitBoundIdentifier(final BoundIdentifier identifierExpression) {
		final List<TIdentifierLiteral> list = Arrays
				.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
						bounds.get(identifierExpression.getBoundIndex())) });
		final AIdentifierExpression expression = new AIdentifierExpression();
		expression.setIdentifier(list);
		setExpression(expression);
	}

	@Override
	public void visitFreeIdentifier(final FreeIdentifier identifierExpression) {
		final List<TIdentifierLiteral> list = Arrays
				.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
						identifierExpression.getName()) });
		final AIdentifierExpression expression = new AIdentifierExpression();
		expression.setIdentifier(list);
		setExpression(expression);
	}

	@Override
	public void visitIntegerLiteral(final IntegerLiteral expression) {
		final BigInteger value = expression.getValue();
		final AIntegerExpression integerExpression = new AIntegerExpression();
		integerExpression.setLiteral(new TIntegerLiteral(value.toString()));
		setExpression(integerExpression);
	}

	@Override
	public void visitSetExtension(final SetExtension expression) {
		final Expression[] members = expression.getMembers();
		final ASetExtensionExpression setExtensionExpression = new ASetExtensionExpression();
		final List<PExpression> list = new ArrayList<PExpression>();
		for (final Expression e : members) {
			final ExpressionVisitor visitor = new ExpressionVisitor(bounds);
			e.accept(visitor);
			list.add(visitor.getExpression());
		}
		setExtensionExpression.setExpressions(list);
		setExpression(setExtensionExpression);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void visitUnaryExpression(final UnaryExpression expression) { // NOPMD
		// by
		// bendisposto
		final int tag = expression.getTag();
		final ExpressionVisitor visitor = new ExpressionVisitor(bounds);
		expression.getChild().accept(visitor);
		final PExpression exp = visitor.getExpression();

		switch (tag) {
		case Formula.KCARD:
			final ACardExpression cardExpression = new ACardExpression();
			cardExpression.setExpression(exp);
			setExpression(cardExpression);
			break;
		case Formula.POW:
			final APowSubsetExpression powExpression = new APowSubsetExpression();
			powExpression.setExpression(exp);
			setExpression(powExpression);
			break;
		case Formula.POW1:
			final APow1SubsetExpression pow1Expression = new APow1SubsetExpression();
			pow1Expression.setExpression(exp);
			setExpression(pow1Expression);
			break;
		case Formula.KUNION:
			final AGeneralUnionExpression unionExpression = new AGeneralUnionExpression();
			unionExpression.setExpression(exp);
			setExpression(unionExpression);
			break;
		case Formula.KINTER:
			final AGeneralIntersectionExpression interExpression = new AGeneralIntersectionExpression();
			interExpression.setExpression(exp);
			setExpression(interExpression);
			break;
		case Formula.KDOM:
			final ADomainExpression domainExpression = new ADomainExpression();
			domainExpression.setExpression(exp);
			setExpression(domainExpression);
			break;
		case Formula.KRAN:
			final ARangeExpression rangeExpression = new ARangeExpression();
			rangeExpression.setExpression(exp);
			setExpression(rangeExpression);
			break;
		case Formula.KPRJ1:
			final AEventBFirstProjectionExpression firstProjectionExpression = new AEventBFirstProjectionExpression();
			firstProjectionExpression.setExpression(exp);
			setExpression(firstProjectionExpression);
			break;
		case Formula.KPRJ2:
			final AEventBSecondProjectionExpression secondProjectionExpression = new AEventBSecondProjectionExpression();
			secondProjectionExpression.setExpression(exp);
			setExpression(secondProjectionExpression);
			break;
		case Formula.KID:
			final AIdentityExpression identityExpression = new AIdentityExpression();
			identityExpression.setExpression(exp);
			setExpression(identityExpression);
			break;
		case Formula.KMIN:
			final AMinExpression minExpression = new AMinExpression();
			minExpression.setExpression(exp);
			setExpression(minExpression);
			break;
		case Formula.KMAX:
			final AMaxExpression maxExpression = new AMaxExpression();
			maxExpression.setExpression(exp);
			setExpression(maxExpression);
			break;
		case Formula.CONVERSE:
			final AReverseExpression reverseExpression = new AReverseExpression();
			reverseExpression.setExpression(exp);
			setExpression(reverseExpression);
			break;
		case Formula.UNMINUS:
			final AUnaryMinusExpression unaryExpression = new AUnaryMinusExpression();
			unaryExpression.setExpression(exp);
			setExpression(unaryExpression);
			break;

		default:
			throw new AssertionError("Uncovered Expression");
		}
	}
}
