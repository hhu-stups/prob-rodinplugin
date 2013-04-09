package de.prob.model.eventb;

import de.prob.animator.domainobjects.EventB;
import de.prob.animator.domainobjects.IEvalElement;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.IEval;

public class Variant extends AbstractElement implements IEval {
	private final IEvalElement expression;

	public Variant(final String code) {
		expression = new EventB(code);
	}

	public IEvalElement getExpression() {
		return expression;
	}

	@Override
	public IEvalElement getEvaluate() {
		return expression;
	}
}
