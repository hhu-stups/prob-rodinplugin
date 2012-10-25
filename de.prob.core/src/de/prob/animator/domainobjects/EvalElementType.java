package de.prob.animator.domainobjects;

public enum EvalElementType {
	PREDICATE, EXPRESSION;

	@Override
	public String toString() {
		return "#" + super.toString();
	}

}
