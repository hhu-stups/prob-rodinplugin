package de.prob.core.domainobjects.ltl;

public enum CounterExampleValueType {
	TRUE, FALSE, UNDEFINED;

	@Override
	public String toString() {
		return name().substring(0, 1);
	}
};
