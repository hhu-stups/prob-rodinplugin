/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.eventb.translator; // NOPMD
// High number of imports because it depends on AST

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.AssociativePredicate;
import org.eventb.core.ast.BinaryPredicate;
import org.eventb.core.ast.BoundIdentDecl;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.Formula;
import org.eventb.core.ast.ISimpleVisitor;
import org.eventb.core.ast.LiteralPredicate;
import org.eventb.core.ast.MultiplePredicate;
import org.eventb.core.ast.Predicate;
import org.eventb.core.ast.QuantifiedPredicate;
import org.eventb.core.ast.RelationalPredicate;
import org.eventb.core.ast.SimplePredicate;
import org.eventb.core.ast.UnaryPredicate;

import de.be4.classicalb.core.parser.analysis.pragma.internal.ClassifiedPragma;
import de.be4.classicalb.core.parser.node.AConjunctPredicate;
import de.be4.classicalb.core.parser.node.ADisjunctPredicate;
import de.be4.classicalb.core.parser.node.AEqualPredicate;
import de.be4.classicalb.core.parser.node.AEquivalencePredicate;
import de.be4.classicalb.core.parser.node.AExistsPredicate;
import de.be4.classicalb.core.parser.node.AFalsityPredicate;
import de.be4.classicalb.core.parser.node.AFinitePredicate;
import de.be4.classicalb.core.parser.node.AForallPredicate;
import de.be4.classicalb.core.parser.node.AGreaterEqualPredicate;
import de.be4.classicalb.core.parser.node.AGreaterPredicate;
import de.be4.classicalb.core.parser.node.AImplicationPredicate;
import de.be4.classicalb.core.parser.node.ALessEqualPredicate;
import de.be4.classicalb.core.parser.node.ALessPredicate;
import de.be4.classicalb.core.parser.node.AMemberPredicate;
import de.be4.classicalb.core.parser.node.ANegationPredicate;
import de.be4.classicalb.core.parser.node.ANotEqualPredicate;
import de.be4.classicalb.core.parser.node.ANotMemberPredicate;
import de.be4.classicalb.core.parser.node.ANotSubsetPredicate;
import de.be4.classicalb.core.parser.node.ANotSubsetStrictPredicate;
import de.be4.classicalb.core.parser.node.APartitionPredicate;
import de.be4.classicalb.core.parser.node.ASubsetPredicate;
import de.be4.classicalb.core.parser.node.ASubsetStrictPredicate;
import de.be4.classicalb.core.parser.node.ATruthPredicate;
import de.be4.classicalb.core.parser.node.PExpression;
import de.be4.classicalb.core.parser.node.PPredicate;
import de.hhu.stups.sablecc.patch.SourcePosition;
import de.prob.eventb.translator.internal.SimpleVisitorAdapter;

public class PredicateVisitor extends SimpleVisitorAdapter implements // NOPMD
		ISimpleVisitor {

	private static final String UNCOVERED_PREDICATE = "Uncovered Predicate";

	private PPredicate p;

	private final LinkedList<String> bounds; // NOPMD
	// we need properties of linked lists, List is not an option

	private boolean predicateSet = false;

	public PPredicate getPredicate() {
		return p;
	}

	public void setPredicate(final PPredicate p) {
		synchronized (bounds) {
			if (predicateSet) {
				throw new AssertionError("The Visitor must not be used twice!");
			}
			predicateSet = true;
			this.p = p;
		}
		//public ClassifiedPragma(String name, Node attachedTo, List<String> arguments, List<String> warnings, SourcePosition start, SourcePosition end) {
		
    //     new ClassifiedPragma("discharged", p, proof, Collections.emptyList(), new SourcePosition(-1, -1), new SourcePosition(-1, -1));
	}

	public PredicateVisitor(final LinkedList<String> bounds) {
		super();
		if (bounds == null) {
			this.bounds = new LinkedList<String>();
		} else {
			this.bounds = bounds;
		}
	}

	public PredicateVisitor() {
		this(null);
	}

	@Override
	public void visitQuantifiedPredicate(final QuantifiedPredicate predicate) {
		final int tag = predicate.getTag();

		// Add quantified identifiers to bound list and recursively create
		// subtrees representing the identifiers
		final List<ExpressionVisitor> ev = new LinkedList<ExpressionVisitor>();
		final BoundIdentDecl[] decls = predicate.getBoundIdentDecls();
		for (final BoundIdentDecl boundIdentDecl : decls) {
			final ExpressionVisitor visitor = new ExpressionVisitor(bounds);
			boundIdentDecl.accept(visitor);
			ev.add(visitor);
			bounds.addFirst(boundIdentDecl.getName());
		}

		// Collect Subtrees in a list
		final LinkedList<PExpression> list = new LinkedList<PExpression>();
		for (final ExpressionVisitor visitor : ev) {
			list.add(visitor.getExpression());
		}

		// Recursively analyze the predicate (important, bounds are already set)
		final PredicateVisitor predicateVisitor = new PredicateVisitor(bounds);
		predicate.getPredicate().accept(predicateVisitor);

		switch (tag) {
		case Formula.EXISTS:
			final AExistsPredicate existentialQuantificationPredicate = new AExistsPredicate();
			existentialQuantificationPredicate.setIdentifiers(list);
			existentialQuantificationPredicate.setPredicate(predicateVisitor
					.getPredicate());
			setPredicate(existentialQuantificationPredicate);
			break;
		case Formula.FORALL:
			final AForallPredicate universalQuantificationPredicate = new AForallPredicate();
			universalQuantificationPredicate.setIdentifiers(list);
			PPredicate pred = predicateVisitor.getPredicate();
			if (!(pred instanceof AImplicationPredicate)) {
				pred = new AImplicationPredicate(new ATruthPredicate(), pred);
			}
			universalQuantificationPredicate.setImplication(pred);
			setPredicate(universalQuantificationPredicate);
			break;

		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		// remove quantified identifiers from bound list (leaving scope)
		for (int i = 0; i < decls.length; i++) {
			bounds.removeFirst();
		}

	}

	@Override
	public void visitAssociativePredicate(final AssociativePredicate predicate) {
		// {LAND, LOR, LEQV}
		final int tag = predicate.getTag();
		final Predicate[] children = predicate.getChildren();
		if (children.length < 2) {
			throw new AssertionError(
					"Predicate must have at least 2 subpredicates.");
		}

		final LinkedList<PredicateVisitor> pv = new LinkedList<PredicateVisitor>();

		for (final Predicate pr : children) {
			final PredicateVisitor p = new PredicateVisitor(bounds);
			pv.add(p);
			pr.accept(p);
		}

		switch (tag) {
		case Formula.LOR:
			setPredicate(recurseOR(pv));
			break;

		case Formula.LAND:
			setPredicate(recurseAND(pv));
			break;
		case Formula.LEQV:
			setPredicate(recurseEQV(pv));
			break;

		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}

	}

	private PPredicate recurseOR(final List<PredicateVisitor> list) {
		final ADisjunctPredicate r = new ADisjunctPredicate();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(list.get(1).getPredicate());
		} else {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(recurseOR(list.subList(1, list.size())));
		}
		return r;
	}

	private PPredicate recurseAND(final List<PredicateVisitor> list) {
		final AConjunctPredicate r = new AConjunctPredicate();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(list.get(1).getPredicate());
		} else {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(recurseAND(list.subList(1, list.size())));
		}
		return r;
	}

	private PPredicate recurseEQV(final List<PredicateVisitor> list) {
		final AEquivalencePredicate r = new AEquivalencePredicate();
		if (list.size() == 2) {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(list.get(1).getPredicate());
		} else {
			r.setLeft(list.get(0).getPredicate());
			r.setRight(recurseEQV(list.subList(1, list.size())));
		}
		return r;
	}

	@Override
	public void visitBinaryPredicate(final BinaryPredicate predicate) {
		final int tag = predicate.getTag();

		final PredicateVisitor subLeft = new PredicateVisitor(bounds);
		predicate.getLeft().accept(subLeft);
		final PredicateVisitor subRight = new PredicateVisitor(bounds);
		predicate.getRight().accept(subRight);

		switch (tag) {
		case Formula.LIMP:
			final AImplicationPredicate limp = new AImplicationPredicate();
			limp.setLeft(subLeft.getPredicate());
			limp.setRight(subRight.getPredicate());
			setPredicate(limp);
			break;
		case Formula.LEQV:
			final AEquivalencePredicate leqv = new AEquivalencePredicate();
			leqv.setLeft(subLeft.getPredicate());
			leqv.setRight(subRight.getPredicate());
			setPredicate(leqv);
			break;
		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
	}

	@Override
	public void visitLiteralPredicate(final LiteralPredicate predicate) {
		final int tag = predicate.getTag();
		switch (tag) {
		case Formula.BTRUE:
			setPredicate(new ATruthPredicate());
			break;
		case Formula.BFALSE:
			setPredicate(new AFalsityPredicate());
			break;

		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
	}

	@Override
	public void visitRelationalPredicate(final RelationalPredicate predicate) { // NOPMD
		// High complexity is ok
		// EQUAL, NOTEQUAL, LT, LE, GT, GE, IN, NOTIN, SUBSET,
		// NOTSUBSET, SUBSETEQ, NOTSUBSETEQ
		final ExpressionVisitor subLeft = new ExpressionVisitor(bounds);
		predicate.getLeft().accept(subLeft);
		final ExpressionVisitor subRight = new ExpressionVisitor(bounds);
		predicate.getRight().accept(subRight);

		final int tag = predicate.getTag();

		switch (tag) {
		case Formula.EQUAL:
			final AEqualPredicate equalPredicate = new AEqualPredicate();
			equalPredicate.setLeft(subLeft.getExpression());
			equalPredicate.setRight(subRight.getExpression());
			setPredicate(equalPredicate);
			break;
		case Formula.NOTEQUAL:
			final ANotEqualPredicate unequalPredicate = new ANotEqualPredicate();
			unequalPredicate.setLeft(subLeft.getExpression());
			unequalPredicate.setRight(subRight.getExpression());
			setPredicate(unequalPredicate);
			break;

		case Formula.LT:
			final ALessPredicate ltPredicate = new ALessPredicate();
			ltPredicate.setLeft(subLeft.getExpression());
			ltPredicate.setRight(subRight.getExpression());
			setPredicate(ltPredicate);
			break;
		case Formula.LE:
			final ALessEqualPredicate lePredicate = new ALessEqualPredicate();
			lePredicate.setLeft(subLeft.getExpression());
			lePredicate.setRight(subRight.getExpression());
			setPredicate(lePredicate);
			break;
		case Formula.GT:
			final AGreaterPredicate gtPredicate = new AGreaterPredicate();
			gtPredicate.setLeft(subLeft.getExpression());
			gtPredicate.setRight(subRight.getExpression());
			setPredicate(gtPredicate);
			break;
		case Formula.GE:
			final AGreaterEqualPredicate gePredicate = new AGreaterEqualPredicate();
			gePredicate.setLeft(subLeft.getExpression());
			gePredicate.setRight(subRight.getExpression());
			setPredicate(gePredicate);
			break;

		case Formula.IN:
			final AMemberPredicate inPredicate = new AMemberPredicate();
			inPredicate.setLeft(subLeft.getExpression());
			inPredicate.setRight(subRight.getExpression());
			setPredicate(inPredicate);
			break;
		case Formula.NOTIN:
			final ANotMemberPredicate ninPredicate = new ANotMemberPredicate();
			ninPredicate.setLeft(subLeft.getExpression());
			ninPredicate.setRight(subRight.getExpression());
			setPredicate(ninPredicate);
			break;
		case Formula.SUBSET:
			final ASubsetStrictPredicate strictSubsetPredicate = new ASubsetStrictPredicate();
			strictSubsetPredicate.setLeft(subLeft.getExpression());
			strictSubsetPredicate.setRight(subRight.getExpression());
			setPredicate(strictSubsetPredicate);
			break;
		case Formula.NOTSUBSET:
			final ANotSubsetStrictPredicate notStrictSubsetPredicate = new ANotSubsetStrictPredicate();
			notStrictSubsetPredicate.setLeft(subLeft.getExpression());
			notStrictSubsetPredicate.setRight(subRight.getExpression());
			setPredicate(notStrictSubsetPredicate);
			break;
		case Formula.SUBSETEQ:
			final ASubsetPredicate subsetPredicate = new ASubsetPredicate();
			subsetPredicate.setLeft(subLeft.getExpression());
			subsetPredicate.setRight(subRight.getExpression());
			setPredicate(subsetPredicate);
			break;
		case Formula.NOTSUBSETEQ:
			final ANotSubsetPredicate notSubsetPredicate = new ANotSubsetPredicate();
			notSubsetPredicate.setLeft(subLeft.getExpression());
			notSubsetPredicate.setRight(subRight.getExpression());
			setPredicate(notSubsetPredicate);
			break;

		default:
			throw new AssertionError(UNCOVERED_PREDICATE);
		}

	}

	@Override
	public void visitSimplePredicate(final SimplePredicate predicate) {
		if (predicate.getTag() != Formula.KFINITE) {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		final AFinitePredicate finite = new AFinitePredicate();
		final ExpressionVisitor subEx = new ExpressionVisitor(bounds);
		predicate.getExpression().accept(subEx);
		finite.setSet(subEx.getExpression());
		setPredicate(finite);
	}

	@Override
	public void visitUnaryPredicate(final UnaryPredicate predicate) {
		if (predicate.getTag() != Formula.NOT) {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		final ANegationPredicate negationPredicate = new ANegationPredicate();
		final PredicateVisitor sub = new PredicateVisitor(bounds);
		predicate.getChild().accept(sub);
		negationPredicate.setPredicate(sub.getPredicate());
		setPredicate(negationPredicate);
	}

	@Override
	public void visitMultiplePredicate(final MultiplePredicate predicate) {
		final Expression[] subs = predicate.getChildren();
		final List<PExpression> expressions = new ArrayList<PExpression>(
				subs.length);
		for (Expression e : subs) {
			final ExpressionVisitor sub = new ExpressionVisitor(bounds);
			e.accept(sub);
			expressions.add(sub.getExpression());
		}

		final PPredicate result;
		if (predicate.getTag() == Formula.KPARTITION) {
			if (expressions.size() > 0) {
				PExpression set = expressions.remove(0);
				result = new APartitionPredicate(set, expressions);
			} else {
				throw new AssertionError("to few arguments for PARTITION");
			}
		} else {
			throw new AssertionError(UNCOVERED_PREDICATE);
		}
		setPredicate(result);
	}

}
