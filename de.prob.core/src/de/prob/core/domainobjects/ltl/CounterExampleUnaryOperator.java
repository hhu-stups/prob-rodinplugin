package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides operators with one parameter.
 * 
 * @author Andriy Tolstoy
 * 
 */
public abstract class CounterExampleUnaryOperator extends
		CounterExampleProposition {
	protected final CounterExampleProposition argument;
	protected List<List<Integer>> highlightedPositions = new ArrayList<List<Integer>>();

	public CounterExampleUnaryOperator(final String name,
			final String fullName, final PathType pathType,
			final int loopEntry, final CounterExampleProposition argument) {
		super(name, fullName, pathType, loopEntry);
		this.argument = argument;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<CounterExampleProposition> getChildren() {
		List<CounterExampleProposition> children = super.getChildren();
		children.addAll(argument.getChildren());
		return children;
	}

	public CounterExampleProposition getArgument() {
		return argument;
	}

	public List<List<Integer>> getHighlightedPositions() {
		return highlightedPositions;
	}

	@Override
	public String toString() {
		return new StringBuilder(name).append(argument).toString();
	}

	@Override
	protected List<CounterExampleValueType> calculate() {
		final List<CounterExampleValueType> argumentValues = argument
				.getValues();

		final int size = argumentValues.size();
		final List<CounterExampleValueType> values = new ArrayList<CounterExampleValueType>();

		for (int i = 0; i < size; i++) {
			values.add(calculate(i));
		}

		return values;
	}

	protected abstract CounterExampleValueType calculate(int position);

	protected void fillHighlightedPositions(final int position,
			final int index, final int checkedSize, boolean isPast) {
		highlightedPositions.add(fillPositions(position, index, checkedSize,
				isPast));
	}

	@Override
	protected int calculatePosition(int pos) {
		int size = getArgument().getValues().size();
		return pos < size ? pos : pos - (size - loopEntry);
	}
}
