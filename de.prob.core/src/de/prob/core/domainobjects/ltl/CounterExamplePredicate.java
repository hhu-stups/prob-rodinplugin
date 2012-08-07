package de.prob.core.domainobjects.ltl;

import java.util.List;

/**
 * Provides predicates.
 * 
 * @author Andriy Tolstoy
 * 
 */
public class CounterExamplePredicate extends CounterExampleProposition {
	private final List<CounterExampleValueType> values;

	public CounterExamplePredicate(final String name,
			final CounterExample counterExample,
			final List<CounterExampleValueType> values) {
		super(name, name, counterExample);
		this.values = values;
	}

	@Override
	public List<CounterExampleValueType> calculate() {
		return values;
	}

	@Override
	public boolean hasChildren() {
		return false;
	}

	@Override
	public String toString() {
		return name;
	}

}
