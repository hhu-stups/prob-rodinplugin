package de.prob.core.domainobjects.ltl;

import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides transitions.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleTransition extends CounterExamplePredicate {
	public CounterExampleTransition(final String name, final PathType pathType,
			final int loopEntry, final List<CounterExampleValueType> values) {
		super(name, pathType, loopEntry, values);
	}

	public CounterExampleTransition(final String name, final PathType pathType,
			final List<CounterExampleValueType> values) {
		this(name, pathType, -1, values);
	}

	public CounterExampleTransition(final PathType pathType,
			final int loopEntry, final List<CounterExampleValueType> values) {
		this("", pathType, loopEntry, values);
	}

	public CounterExampleTransition(final PathType pathType,
			final List<CounterExampleValueType> values) {
		this("", pathType, values);
	}

	@Override
	public boolean isTransition() {
		return true;
	}
}
