package de.prob.model.representation;

import de.prob.animator.domainobjects.IEvalElement;
import de.prob.unicode.UnicodeTranslator;

public abstract class Axiom extends AbstractElement implements IEval {

	private final IEvalElement predicate;

	public Axiom(final IEvalElement predicate) {
		this.predicate = predicate;
	}

	public IEvalElement getPredicate() {
		return predicate;
	}

	@Override
	public IEvalElement getEvaluate() {
		return predicate;
	}
	
	@Override
	public String toString() {
		return UnicodeTranslator.toUnicode(predicate.getCode());
	}
}
