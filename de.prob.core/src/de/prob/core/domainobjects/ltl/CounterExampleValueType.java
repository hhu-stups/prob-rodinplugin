package de.prob.core.domainobjects.ltl;

public enum CounterExampleValueType {
	TRUE, FALSE, UNKNOWN;

	@Override
	public String toString() {
		return name().substring(0, 1);
	}
};
