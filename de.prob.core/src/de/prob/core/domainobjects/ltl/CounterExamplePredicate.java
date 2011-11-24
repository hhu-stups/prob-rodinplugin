package de.prob.core.domainobjects.ltl;

import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides predicates.
 * 
 * @author Andriy Tolstoy
 * 
 */
public class CounterExamplePredicate extends CounterExampleProposition {
	private final List<CounterExampleValueType> values;

	public CounterExamplePredicate(final String name, final PathType pathType,
			final int loopEntry, final List<CounterExampleValueType> values) {
		super(name, name, pathType, loopEntry);
		this.values = values;
	}

	public CounterExamplePredicate(final String name, final PathType pathType,
			final List<CounterExampleValueType> values) {
		this(name, pathType, -1, values);
	}

	public CounterExamplePredicate(final PathType pathType,
			final int loopEntry, final List<CounterExampleValueType> values) {
		this("", pathType, loopEntry, values);
	}

	public CounterExamplePredicate(final PathType pathType,
			final List<CounterExampleValueType> values) {
		this("", pathType, values);
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

	@Override
	protected int calculatePosition(int pos) {
		int size = values.size();
		return pos < size ? pos : pos - (size - loopEntry);
	}
}
