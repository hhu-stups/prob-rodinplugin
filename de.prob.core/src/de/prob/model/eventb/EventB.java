package de.prob.model.eventb;

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

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.eventb.translator.ExpressionVisitor;
import de.prob.eventb.translator.PredicateVisitor;
import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.node.Node;
import de.prob.model.representation.FormulaUUID;
import de.prob.model.representation.IEntity;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.unicode.UnicodeTranslator;

public class EventB implements IEvalElement, IEntity {

	public FormulaUUID uuid = new FormulaUUID();

	private final String code;
	private String kind;
	private Node ast = null;

	public EventB(final String code) {
		this.code = code;
	}

	private void ensureParsed() {
		final String unicode = UnicodeTranslator.toUnicode(code);
		kind = PREDICATE.toString();
		IParseResult parseResult = FormulaFactory.getDefault().parsePredicate(
				unicode, LanguageVersion.LATEST, null);

		if (!parseResult.hasProblem()) {
			ast = preparePredicateAst(parseResult);

		} else {
			kind = EXPRESSION.toString();
			parseResult = FormulaFactory.getDefault().parseExpression(unicode,
					LanguageVersion.LATEST, null);
			ast = prepareExpressionAst(parseResult);
		}
		if (parseResult.hasProblem()) {
			throwException(code, parseResult);
		}
	}

	private Node prepareExpressionAst(final IParseResult parseResult) {
		final Expression expr = parseResult.getParsedExpression();
		final ExpressionVisitor visitor = new ExpressionVisitor(
				new LinkedList<String>());
		expr.accept(visitor);
		final Node expression = visitor.getExpression();
		return expression;
	}

	private Node preparePredicateAst(final IParseResult parseResult) {
		final Predicate parsedPredicate = parseResult.getParsedPredicate();
		final PredicateVisitor visitor = new PredicateVisitor();
		parsedPredicate.accept(visitor);
		return visitor.getPredicate();
	}

	private void throwException(final String code,
			final IParseResult parseResult) {
		final List<ASTProblem> problems = parseResult.getProblems();
		final ArrayList<String> msgs = new ArrayList<String>();
		for (final ASTProblem astProblem : problems) {
			msgs.add(astProblem.getMessage().toString());
		}
		StringBuilder sb = new StringBuilder();
		for (String string : msgs) {
			sb.append(string+"\n");
		}
		final String error = sb.toString();
		throw new RuntimeException("Cannot parse " + code + ":\n " + error); // FIXME
	}

	@Override
	public String getCode() {
		return code;
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
	public List<IEntity> getChildren() {
		return new ArrayList<IEntity>();
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

}
