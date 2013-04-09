package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public abstract class Variable extends AbstractElement implements IEval {

	protected final IEvalElement expression;

	public Variable(final IEvalElement expression) {
		this.expression = expression;
	}

	public IEvalElement getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getEvaluate() {
		return expression;
	}

	public String getName() {
		return expression.getCode();
	}
	
	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(expression.getCode());
	}
}
