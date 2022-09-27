package de.prob.core.domainobjects.ltl;

import java.util.List;

/**
 * Provides transitions.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleTransition extends CounterExamplePredicate {
	public CounterExampleTransition(final String name,
			final CounterExample counterExample,
			final List<CounterExampleValueType> values) {
		super(name, counterExample, values);
	}

	@Override
	public boolean isTransition() {
		return true;
	}
}
