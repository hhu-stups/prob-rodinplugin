package de.prob.animator.domainobjects;

import static de.prob.animator.domainobjects.EvalElementType.EXPRESSION;
import static de.prob.animator.domainobjects.EvalElementType.PREDICATE;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eventb.core.ast.ASTProblem;
import org.eventb.core.ast.Expression;
import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.IParseResult;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.Predicate;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.FormulaUUID;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.unicode.UnicodeTranslator;

/**
 * Representation of an Event-B formula
 * 
 * @author joy
 * 
 */
public class EventB implements IEvalElement {

	public FormulaUUID uuid = new FormulaUUID();

	private final String code;
	private String kind;
	private Node ast = null;

	/**
	 * @param code
	 *            - The String which is a representation of the desired Event-B
	 *            formula
	 */
	public EventB(final String code) {
		this.code = UnicodeTranslator.toAscii(code);
	}

	private void ensureParsed() {
//		final String unicode = UnicodeTranslator.toUnicode(code);
//		kind = PREDICATE.toString();
//		IParseResult parseResult = FormulaFactory.getDefault().parsePredicate(
//				unicode, LanguageVersion.LATEST, null);
//
//		if (!parseResult.hasProblem()) {
//			ast = preparePredicateAst(parseResult);
//
//		} else {
//			kind = EXPRESSION.toString();
//			parseResult = FormulaFactory.getDefault().parseExpression(unicode,
//					LanguageVersion.LATEST, null);
//			ast = prepareExpressionAst(parseResult);
//		}
//		if (parseResult.hasProblem()) {
//			throwException(code, parseResult);
//		}
	}

//	private Node prepareExpressionAst(final IParseResult parseResult) {
//		final Expression expr = parseResult.getParsedExpression();
//		final ExpressionVisitor visitor = new ExpressionVisitor(
//				new LinkedList<String>());
//		expr.accept(visitor);
//		final Node expression = visitor.getExpression();
//		return expression;
//	}
//
//	private Node preparePredicateAst(final IParseResult parseResult) {
//		final Predicate parsedPredicate = parseResult.getParsedPredicate();
//		final PredicateVisitor visitor = new PredicateVisitor();
//		parsedPredicate.accept(visitor);
//		return visitor.getPredicate();
//	}
//
//	private void throwException(final String code,
//			final IParseResult parseResult) {
//		final List<ASTProblem> problems = parseResult.getProblems();
//		final ArrayList<String> msgs = new ArrayList<String>();
//		for (final ASTProblem astProblem : problems) {
//			msgs.add(astProblem.getMessage().toString());
//		}
//		final String error = Joiner.on(", \n").join(msgs);
//		throw new EvaluationException("Cannot parse " + code + ":\n " + error);
//	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public boolean equals(final Object that) {
		if (that instanceof EventB) {
			return ((EventB) that).getCode().equals(this.getCode());
		}
		return false;
	}

	@Override
	public void printProlog(final IPrologTermOutput pout) {
		if (ast == null) {
			ensureParsed();
		}

		assert ast != null;
		final ASTProlog prolog = new ASTProlog(pout, null);
		ast.apply(prolog);
	}

	@Override
	public String getKind() {
		return kind;
	}

	@Override
	public String toString() {
		return getCode();
	}

}
