/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.eventb.translator.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Assignment;
import org.eventb.core.ast.AssociativeExpression;
import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.AtomicExpression;
import org.eventb.core.ast.BecomesEqualTo;
import org.eventb.core.ast.BecomesMemberOf;
import org.eventb.core.ast.BecomesSuchThat;
import org.eventb.core.ast.BinaryExpression;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoolExpression;
import org.eventb.core.ast.BooleanType;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.BoundIdentifier;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.ExtendedExpression;
import org.eventb.core.ast.ExtendedPredicate;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.FreeIdentifier;
import org.eventb.core.ast.GivenType;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.IntegerLiteral;
import org.eventb.core.ast.IntegerType;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.ParametricType;
import org.eventb.core.ast.PowerSetType;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.ProductType;
import org.eventb.core.ast.QuantifiedExpression;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SetExtension;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.Type;
import org.eventb.core.ast.UnaryExpression;
import org.eventb.core.ast.UnaryPredicate;
import org.eventb.core.ast.extension.IExpressionExtension;

import de.be4.classicalb.core.parser.node.*;

/**
 * The global SuppressWarnings annotation is used because the deprecated code is
 * used to check if this new implementation computes the same results as the old
 * code.
 * 
 * That should be removed after a while.
 * 
 * @author plagge
 */
@SuppressWarnings("deprecation")
public class TranslationVisitor implements ISimpleVisitor {
	private static final String UNCOVERED_PREDICATE = "Uncovered Predicate";

	private static final int[] EXTRA_TYPE_CONSTRUCTS = new int[] {
			Formula.EMPTYSET, Formula.KPRJ1_GEN, Formula.KPRJ2_GEN,
			Formula.KID_GEN };

	private final LookupStack<PPredicate> predicates = new LookupStack<PPredicate>();
	private final LookupStack<PExpression> expressions = new LookupStack<PExpression>();
	private final LookupStack<PSubstitution> substitutions = new LookupStack<PSubstitution>();
	private final LookupStack<String> boundVariables = new LookupStack<String>();

	@Override
	public void visitAssociativeExpression(
			final AssociativeExpression expression) {
		// BUNION, BINTER, BCOMP, FCOMP, OVR, PLUS, MUL
		List<PExpression> exprs = getSubExpressions(expression.getChildren());
		final PExpression result;
		switch (expression.getTag()) {
		case Formula.BUNION:
			result = recurseBUNION(exprs);
			break;
		case Formula.BINTER:
			result = recurseBINTER(exprs);
			break;
		case Formula.BCOMP:
			result = recurseBCOMP(exprs);
			break;
		case Formula.FCOMP:
			result = recurseFCOMP(exprs);
			break;
		case Formula.OVR:
			result = recurseOVR(exprs);
			break;
		case Formula.PLUS:
			result = recursePLUS(exprs);
			break;
		case Formula.MUL:
			result = recurseMUL(exprs);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		pushExpression(expression, result);
	}

	private void pushExpression(Expression original, PExpression translated) {
		final PExpression toPush;
		if (original.getType() != null && shouldHaveExtraTypeInfo(original)) {
			toPush = new ATypeofExpression(translated,
					translateType(original.getType()));
		} else {
			toPush = translated;
		}
		expressions.push(toPush);
	}

	private boolean shouldHaveExtraTypeInfo(Expression original) {
		if (original instanceof ExtendedExpression) {
			return true;
		} else {
			int tag = original.getTag();
			for (int i = 0; i < EXTRA_TYPE_CONSTRUCTS.length; i++) {
				if (EXTRA_TYPE_CONSTRUCTS[i] == tag)
					return true;
			}
			return false;
		}
	}

	private PExpression translateType(Type type) {
		final PExpression result;
		if (type instanceof BooleanType) {
			result = new ABoolSetExpression();
		} else if (type instanceof GivenType) {
			final String name = ((GivenType) type).getName();
			result = createIdentifierExpression(name);
		} else if (type instanceof IntegerType) {
			result = new AIntegerSetExpression();
		} else if (type instanceof PowerSetType) {
			final Type a = ((PowerSetType) type).getBaseType();
			result = new APowSubsetExpression(translateType(a));
		} else if (type instanceof ProductType) {
			final Type a = ((ProductType) type).getLeft();
			final Type b = ((ProductType) type).getRight();
			result = new ACartesianProductExpression(translateType(a),
					translateType(b));
		} else if (type instanceof ParametricType) {
			final ParametricType pt = (ParametricType) type;
			final IExpressionExtension ext = pt.getExprExtension();
			final Type[] params = pt.getTypeParameters();
			final List<PExpression> list = new ArrayList<PExpression>();
			for (final Type param : params) {
				list.add(translateType(param));
			}
			result = new AExtendedExprExpression(new TIdentifierLiteral(
					ext.getSyntaxSymbol()), list,
					Collections.<PPredicate> emptyList());
		} else {
			throw new UnsupportedOperationException(
					"Don't know how to handle the Event-B type of class "
							+ type.getClass().getCanonicalName());
		}
		return result;
	}

	private PExpression recurseFCOMP(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseFCOMP(list.subList(1, list.size()));
		return new ACompositionExpression(list.get(0), right);
	}

	private PExpression recurseOVR(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseOVR(list.subList(1, list.size()));
		return new AOverwriteExpression(list.get(0), right);
	}

	private PExpression recursePLUS(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recursePLUS(list.subList(1, list.size()));
		return new AAddExpression(list.get(0), right);
	}

	private PExpression recurseMUL(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseMUL(list.subList(1, list.size()));
		return new AMultiplicationExpression(list.get(0), right);
	}

	private PExpression recurseBUNION(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseBUNION(list.subList(1, list.size()));
		return new AUnionExpression(list.get(0), right);
	}

	private PExpression recurseBINTER(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseBINTER(list.subList(1, list.size()));
		return new AIntersectionExpression(list.get(0), right);
	}

	private PExpression recurseBCOMP(final List<PExpression> list) {
		final PExpression right = list.size() == 2 ? list.get(1)
				: recurseBCOMP(list.subList(1, list.size()));
		return new ARingExpression(list.get(0), right);
	}

	@Override
	public void visitAssociativePredicate(final AssociativePredicate predicate) {
		List<PPredicate> children = getSubPredicates(predicate.getChildren());
		final PPredicate result;
		switch (predicate.getTag()) {
		case Formula.LOR:
			result = recurseOR(children);
			break;
		case Formula.LAND:
			result = recurseAND(children);
			break;
		case Formula.LEQV:
			result = recurseEQV(children);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	private PPredicate recurseOR(final List<PPredicate> list) {
		final PPredicate right = list.size() == 2 ? list.get(1)
				: recurseOR(list.subList(1, list.size()));
		return new ADisjunctPredicate(list.get(0), right);
	}

	private PPredicate recurseAND(final List<PPredicate> list) {
		final PPredicate right = list.size() == 2 ? list.get(1)
				: recurseAND(list.subList(1, list.size()));
		return new AConjunctPredicate(list.get(0), right);
	}

	private PPredicate recurseEQV(final List<PPredicate> list) {
		final PPredicate right = list.size() == 2 ? list.get(1)
				: recurseEQV(list.subList(1, list.size()));
		return new AEquivalencePredicate(list.get(0), right);
	}

	@Override
	public void visitAtomicExpression(final AtomicExpression expression) {
		final PExpression result;
		switch (expression.getTag()) {
		case Formula.INTEGER:
			result = new AIntegerSetExpression();
			break;
		case Formula.NATURAL:
			result = new ANaturalSetExpression();
			break;
		case Formula.NATURAL1:
			result = new ANatural1SetExpression();
			break;
		case Formula.BOOL:
			result = new ABoolSetExpression();
			break;
		case Formula.TRUE:
			result = new ABooleanTrueExpression();
			break;
		case Formula.FALSE:
			result = new ABooleanFalseExpression();
			break;
		case Formula.EMPTYSET:
			result = new AEmptySetExpression();
			break;
		case Formula.KPRED:
			result = new APredecessorExpression();
			break;
		case Formula.KSUCC:
			result = new ASuccessorExpression();
			break;
		case Formula.KPRJ1_GEN: // see task#215
			result = new AEventBFirstProjectionV2Expression();
			break;
		case Formula.KPRJ2_GEN:
			result = new AEventBSecondProjectionV2Expression();
			break;
		case Formula.KID_GEN:
			result = new AEventBIdentityExpression();
			break;
		default:
			throw new AssertionError("Uncovered Expression " + expression);
		}
		pushExpression(expression, result);
	}

	@Override
	public void visitBecomesEqualTo(final BecomesEqualTo assignment) {
		final List<PExpression> lhs = getSubExpressions(assignment
				.getAssignedIdentifiers());
		final List<PExpression> rhs = getSubExpressions(assignment
				.getExpressions());
		substitutions.push(new AAssignSubstitution(lhs, rhs));
	}

	@Override
	public void visitBecomesMemberOf(final BecomesMemberOf assignment) {
		final List<PExpression> lhs = getSubExpressions(assignment
				.getAssignedIdentifiers());
		final PExpression set = getExpression(assignment.getSet());
		substitutions.push(new ABecomesElementOfSubstitution(lhs, set));
	}

	@Override
	public void visitBecomesSuchThat(final BecomesSuchThat assignment) {
		final List<PExpression> lhs = getSubExpressions(assignment
				.getAssignedIdentifiers());
		final int originalBoundSize = boundVariables.size();
		for (final FreeIdentifier id : assignment.getAssignedIdentifiers()) {
			boundVariables.push(id.getName() + "'");
		}
		final PPredicate predicate = getPredicate(assignment.getCondition());
		boundVariables.shrinkToSize(originalBoundSize);
		substitutions.push(new ABecomesSuchSubstitution(lhs, predicate));
	}

	@Override
	public void visitBinaryExpression(final BinaryExpression expression) {
		final PExpression exL = getExpression(expression.getLeft());
		final PExpression exR = getExpression(expression.getRight());
		final PExpression result;
		switch (expression.getTag()) {
		case Formula.MAPSTO:
			result = new ACoupleExpression(Arrays.asList(new PExpression[] {
					exL, exR }));
			break;
		case Formula.REL:
			result = new ARelationsExpression(exL, exR);
			break;
		case Formula.TREL:
			result = new ATotalRelationExpression(exL, exR);
			break;
		case Formula.SREL:
			result = new ASurjectionRelationExpression(exL, exR);
			break;
		case Formula.STREL:
			result = new ATotalSurjectionRelationExpression(exL, exR);
			break;
		case Formula.PFUN:
			result = new APartialFunctionExpression(exL, exR);
			break;
		case Formula.TFUN:
			result = new ATotalFunctionExpression(exL, exR);
			break;
		case Formula.PINJ:
			result = new APartialInjectionExpression(exL, exR);
			break;
		case Formula.TINJ:
			result = new ATotalInjectionExpression(exL, exR);
			break;
		case Formula.PSUR:
			result = new APartialSurjectionExpression(exL, exR);
			break;
		case Formula.TSUR:
			result = new ATotalSurjectionExpression(exL, exR);
			break;
		case Formula.TBIJ:
			result = new ATotalBijectionExpression(exL, exR);
			break;
		case Formula.SETMINUS:
			result = new ASetSubtractionExpression(exL, exR);
			break;
		case Formula.CPROD:
			result = new ACartesianProductExpression(exL, exR);
			break;
		case Formula.DPROD:
			result = new ADirectProductExpression(exL, exR);
			break;
		case Formula.PPROD:
			result = new AParallelProductExpression(exL, exR);
			break;
		case Formula.DOMRES:
			result = new ADomainRestrictionExpression(exL, exR);
			break;
		case Formula.DOMSUB:
			result = new ADomainSubtractionExpression(exL, exR);
			break;
		case Formula.RANRES:
			result = new ARangeRestrictionExpression(exL, exR);
			break;
		case Formula.RANSUB:
			result = new ARangeSubtractionExpression(exL, exR);
			break;
		case Formula.UPTO:
			result = new AIntervalExpression(exL, exR);
			break;
		case Formula.MINUS:
			result = new AMinusExpression(exL, exR);
			break;
		case Formula.DIV:
			result = new ADivExpression(exL, exR);
			break;
		case Formula.MOD:
			result = new AModuloExpression(exL, exR);
			break;
		case Formula.EXPN:
			result = new APowerOfExpression(exL, exR);
			break;
		case Formula.FUNIMAGE:
			result = new AFunctionExpression(exL,
					Arrays.asList(new PExpression[] { exR }));
			break;
		case Formula.RELIMAGE:
			result = new AImageExpression(exL, exR);
			break;
		default:
			throw new AssertionError("Uncovered Expression");
		}
		pushExpression(expression, result);
	}

	@Override
	public void visitBinaryPredicate(final BinaryPredicate predicate) {
		final PPredicate left = getPredicate(predicate.getLeft());
		final PPredicate right = getPredicate(predicate.getRight());
		final PPredicate result;
		switch (predicate.getTag()) {
		case Formula.LIMP:
			result = new AImplicationPredicate(left, right);
			break;
		case Formula.LEQV:
			result = new AEquivalencePredicate(left, right);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitBoolExpression(final BoolExpression expression) {
		final PPredicate pred = getPredicate(expression.getPredicate());
		pushExpression(expression, new AConvertBoolExpression(pred));
	}

	@Override
	public void visitBoundIdentDecl(final BoundIdentDecl boundIdentDecl) {
		final String name = boundIdentDecl.getName();
		boundVariables.push(name);
		expressions.push(createIdentifierExpression(name));
	}

	private AIdentifierExpression createIdentifierExpression(final String name) {
		return new AIdentifierExpression(
				Arrays.asList(new TIdentifierLiteral[] { new TIdentifierLiteral(
						name) }));
	}

	@Override
	public void visitBoundIdentifier(final BoundIdentifier identifierExpression) {
		final String name = boundVariables.get(identifierExpression
				.getBoundIndex());
		pushExpression(identifierExpression, createIdentifierExpression(name));
	}

	@Override
	public void visitFreeIdentifier(final FreeIdentifier identifierExpression) {
		pushExpression(identifierExpression,
				createIdentifierExpression(identifierExpression.getName()));
	}

	@Override
	public void visitIntegerLiteral(final IntegerLiteral expression) {
		final String value = expression.getValue().toString();
		pushExpression(expression, new AIntegerExpression(new TIntegerLiteral(
				value)));
	}

	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		final PPredicate result;
		switch (predicate.getTag()) {
		case Formula.BTRUE:
			result = new ATruthPredicate();
			break;
		case Formula.BFALSE:
			result = new AFalsityPredicate();
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitQuantifiedExpression(final QuantifiedExpression expression) {
		// Add quantified identifiers to bound list and recursively create
		// subtrees representing the identifiers
		int originalBoundSize = boundVariables.size();
		final List<PExpression> list = getSubExpressions(expression
				.getBoundIdentDecls());
		final PPredicate pr = getPredicate(expression.getPredicate());
		final PExpression ex = getExpression(expression.getExpression());
		boundVariables.shrinkToSize(originalBoundSize);

		final PExpression result;
		switch (expression.getTag()) {
		case Formula.QUNION:
			result = new AQuantifiedUnionExpression(list, pr, ex);
			break;
		case Formula.QINTER:
			result = new AQuantifiedIntersectionExpression(list, pr, ex);
			break;
		case Formula.CSET:
			result = new AEventBComprehensionSetExpression(list, ex, pr);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		pushExpression(expression, result);
	}

	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		// Add quantified identifiers to bound list and recursively create
		// subtrees representing the identifiers
		int originalBoundSize = boundVariables.size();
		final List<PExpression> list = getSubExpressions(predicate
				.getBoundIdentDecls());
		// Recursively analyze the predicate (important, bounds are already set)
		final PPredicate pred = getPredicate(predicate.getPredicate());
		boundVariables.shrinkToSize(originalBoundSize);

		final PPredicate result;
		switch (predicate.getTag()) {
		case Formula.EXISTS:
			result = new AExistsPredicate(list, pred);
			break;
		case Formula.FORALL:
			final PPredicate impl = pred instanceof AImplicationPredicate ? pred
					: new AImplicationPredicate(new ATruthPredicate(), pred);
			result = new AForallPredicate(list, impl);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) {
		// EQUAL, NOTEQUAL, LT, LE, GT, GE, IN, NOTIN, SUBSET,
		// NOTSUBSET, SUBSETEQ, NOTSUBSETEQ
		final PExpression left = getExpression(predicate.getLeft());
		final PExpression right = getExpression(predicate.getRight());
		final PPredicate result;
		switch (predicate.getTag()) {
		case Formula.EQUAL:
			result = new AEqualPredicate(left, right);
			break;
		case Formula.NOTEQUAL:
			result = new ANotEqualPredicate(left, right);
			break;
		case Formula.LT:
			result = new ALessPredicate(left, right);
			break;
		case Formula.LE:
			result = new ALessEqualPredicate(left, right);
			break;
		case Formula.GT:
			result = new AGreaterPredicate(left, right);
			break;
		case Formula.GE:
			result = new AGreaterEqualPredicate(left, right);
			break;
		case Formula.IN:
			result = new AMemberPredicate(left, right);
			break;
		case Formula.NOTIN:
			result = new ANotMemberPredicate(left, right);
			break;
		case Formula.SUBSET:
			result = new ASubsetStrictPredicate(left, right);
			break;
		case Formula.NOTSUBSET:
			result = new ANotSubsetStrictPredicate(left, right);
			break;
		case Formula.SUBSETEQ:
			result = new ASubsetPredicate(left, right);
			break;
		case Formula.NOTSUBSETEQ:
			result = new ANotSubsetPredicate(left, right);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitSetExtension(final SetExtension expression) {
		final Expression[] members = expression.getMembers();
		final List<PExpression> list = getSubExpressions(members);
		pushExpression(expression, new ASetExtensionExpression(list));
	}

	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		final PPredicate result;
		if (predicate.getTag() == Formula.KFINITE) {
			result = new AFinitePredicate(
					getExpression(predicate.getExpression()));
		} else {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitUnaryExpression(final UnaryExpression expression) {
		final PExpression exp = getExpression(expression.getChild());
		final PExpression result;
		switch (expression.getTag()) {
		case Formula.KCARD:
			result = new ACardExpression(exp);
			break;
		case Formula.POW:
			result = new APowSubsetExpression(exp);
			break;
		case Formula.POW1:
			result = new APow1SubsetExpression(exp);
			break;
		case Formula.KUNION:
			result = new AGeneralUnionExpression(exp);
			break;
		case Formula.KINTER:
			result = new AGeneralIntersectionExpression(exp);
			break;
		case Formula.KDOM:
			result = new ADomainExpression(exp);
			break;
		case Formula.KRAN:
			result = new ARangeExpression(exp);
			break;
		case Formula.KPRJ1:
			result = new AEventBFirstProjectionExpression(exp);
			break;
		case Formula.KPRJ2:
			result = new AEventBSecondProjectionExpression(exp);
			break;
		case Formula.KID:
			result = new AIdentityExpression(exp);
			break;
		case Formula.KMIN:
			result = new AMinExpression(exp);
			break;
		case Formula.KMAX:
			result = new AMaxExpression(exp);
			break;
		case Formula.CONVERSE:
			result = new AReverseExpression(exp);
			break;
		case Formula.UNMINUS:
			result = new AUnaryMinusExpression(exp);
			break;
		default:
			throw new AssertionError("Uncovered Expression");
		}
		pushExpression(expression, result);
	}

	@Override
	public void visitUnaryPredicate(final UnaryPredicate predicate) {
		final PPredicate result;
		if (predicate.getTag() == Formula.NOT) {
			result = new ANegationPredicate(getPredicate(predicate.getChild()));
		} else {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		final PPredicate result;
		if (predicate.getTag() == Formula.KPARTITION) {
			final List<PExpression> expressions = getSubExpressions(predicate
					.getChildren());
			if (expressions.size() > 0) {
				PExpression set = expressions.remove(0);
				result = new APartitionPredicate(set, expressions);
			} else {
				throw new AssertionError("to few arguments for PARTITION");
			}
		} else {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		predicates.push(result);
	}

	@Override
	public void visitExtendedExpression(final ExtendedExpression expression) {
		final String symbol = expression.getExtension().getSyntaxSymbol();
		List<PExpression> childExprs = getSubExpressions(expression
				.getChildExpressions());
		List<PPredicate> childPreds = getSubPredicates(expression
				.getChildPredicates());
		pushExpression(expression, new AExtendedExprExpression(
				new TIdentifierLiteral(symbol), childExprs, childPreds));
	}

	@Override
	public void visitExtendedPredicate(final ExtendedPredicate predicate) {
		final String symbol = predicate.getExtension().getSyntaxSymbol();
		List<PExpression> childExprs = getSubExpressions(predicate
				.getChildExpressions());
		List<PPredicate> childPreds = getSubPredicates(predicate
				.getChildPredicates());
		predicates.push(new AExtendedPredPredicate(new TIdentifierLiteral(
				symbol), childExprs, childPreds));
	}

	private List<PExpression> getSubExpressions(final Formula<?>[] children) {
		for (final Formula<?> f : children) {
			f.accept(this);
		}
		return expressions.removeLastElements(children.length);
	}

	private List<PPredicate> getSubPredicates(final Formula<?>[] children) {
		for (final Formula<?> f : children) {
			f.accept(this);
		}
		return predicates.removeLastElements(children.length);
	}

	private PPredicate getPredicate(final Predicate predicate) {
		predicate.accept(this);
		return predicates.pop();
	}

	private PExpression getExpression(final Expression expression) {
		expression.accept(this);
		return expressions.pop();
	}

	/**
	 * Lookup stack implements a stack with additional arbitrary read access.
	 * 
	 * Additionally, there is a method to remove elements until the resulting
	 * stack has a certain size.
	 * 
	 * Also there is another method to remove n elements at the same time into a
	 * list.
	 * 
	 * @param <T>
	 */
	private static class LookupStack<T> {
		private final List<T> elements = new ArrayList<T>();

		public void push(T elem) {
			elements.add(elem);
		}

		public T pop() {
			return elements.remove(elements.size() - 1);
		}

		public int size() {
			return elements.size();
		}

		public T get(int pos) {
			return elements.get(elements.size() - pos - 1);
		}

		void shrinkToSize(int size) {
			final int oSize = elements.size();
			final int rSize = oSize - size;
			for (int i = 0; i < rSize; i++) {
				elements.remove(oSize - 1 - i);
			}
		}

		List<T> removeLastElements(int toRemove) {
			final int targetSize = elements.size() - toRemove;
			List<T> result = new ArrayList<T>(toRemove);
			for (int i = 0; i < toRemove; i++) {
				result.add(elements.get(targetSize + i));
			}
			shrinkToSize(targetSize);
			return result;
		}

		@Override
		public String toString() {
			return elements.toString();
		}
	}

	public PPredicate getPredicate() {
		assertExactStacksize(predicates);
		return predicates.pop();
	}

	public PExpression getExpression() {
		assertExactStacksize(expressions);
		return expressions.pop();
	}

	public PSubstitution getSubstitution() {
		assertExactStacksize(substitutions);
		return substitutions.pop();
	}

	private void assertExactStacksize(LookupStack<?> stack) {
		if (stack.size() != 1) {
			throw new AssertionError(
					"Exactly one element on the stack expected, but were "
							+ predicates.size());

		}
	}

	public static PPredicate translatePredicate(Predicate p) {
		// Create a visitor and use it to translate the predicate
		final TranslationVisitor visitor = new TranslationVisitor();
		p.accept(visitor);
		return visitor.getPredicate();
	}

	public static PExpression translateExpression(Expression e) {
		// Create a visitor and use it to translate the predicate
		final TranslationVisitor visitor = new TranslationVisitor();
		e.accept(visitor);
		return visitor.getExpression();
	}

	public static PSubstitution translateAssignment(Assignment a) {
		// Create a visitor and use it to translate the predicate
		final TranslationVisitor visitor = new TranslationVisitor();
		a.accept(visitor);
		return visitor.getSubstitution();
	}

}
