package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.logging.Logger;

/**
 * Provides operators with two parameters.
 * 
 * @author Andriy Tolstoy
 * 
 */
public abstract class CounterExampleBinaryOperator extends
		CounterExampleProposition {
	protected final CounterExampleProposition firstArgument;
	protected final CounterExampleProposition secondArgument;
	protected List<List<Integer>> firstHighlightedPositions = new ArrayList<List<Integer>>();
	protected List<List<Integer>> secondHighlightedPositions = new ArrayList<List<Integer>>();

	public CounterExampleBinaryOperator(final String name,
			final String fullName, final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super(name, fullName, counterExample);
		this.firstArgument = firstArgument;
		this.secondArgument = secondArgument;
	}

	protected abstract CounterExampleValueType calculate(int position);

	@Override
	protected List<CounterExampleValueType> calculate() {
		final List<CounterExampleValueType> first = firstArgument.getValues();
		final List<CounterExampleValueType> second = secondArgument.getValues();
		final int size = first.size();

		Logger.assertProB("Sizes of traces do not match", size == second.size());

		final List<CounterExampleValueType> values = new ArrayList<CounterExampleValueType>();

		for (int i = 0; i < size; i++) {
			values.add(calculate(i));
		}

		return values;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public List<CounterExampleProposition> getChildren() {
		List<CounterExampleProposition> children = super.getChildren();
		children.addAll(firstArgument.getChildren());
		children.addAll(secondArgument.getChildren());
		return children;
	}

	public CounterExampleProposition getFirstArgument() {
		return firstArgument;
	}

	public CounterExampleProposition getSecondArgument() {
		return secondArgument;
	}

	public List<List<Integer>> getFirstHighlightedPositions() {
		return firstHighlightedPositions;
	}

	public List<List<Integer>> getSecondHighlightedPositions() {
		return secondHighlightedPositions;
	}

	@Override
	public String toString() {
		return new StringBuilder("(" + firstArgument + ")")
				.append(" " + name + " ").append("(" + secondArgument + ")")
				.toString();
	}

	protected void fillHighlightedPositions(final int position,
			final int firstIndex, final int secondIndex,
			final int firstCheckedSize, int secondCheckedSize, boolean isPast) {
		firstHighlightedPositions.add(fillPositions(position, firstIndex,
				firstCheckedSize, isPast));
		secondHighlightedPositions.add(fillPositions(position, secondIndex,
				secondCheckedSize, isPast));
	}

	protected int indexOfUnknownState(
			final List<CounterExampleValueType> firstCheckedValues,
			final List<CounterExampleValueType> secondCheckedValues,
			boolean past) {
		int unknownStateIndex = -1;

		if (past) {
			for (int i = firstCheckedValues.size() - 1; i >= 0; i--) {
				if (firstCheckedValues.get(i).equals(
						CounterExampleValueType.UNKNOWN)
						&& secondCheckedValues.get(i).equals(
								CounterExampleValueType.UNKNOWN)) {
					unknownStateIndex = i;
					break;
				}
			}
		} else {
			for (int i = 0; i < firstCheckedValues.size(); i++) {
				if (firstCheckedValues.get(i).equals(
						CounterExampleValueType.UNKNOWN)
						&& secondCheckedValues.get(i).equals(
								CounterExampleValueType.UNKNOWN)) {
					unknownStateIndex = i;
					break;
				}
			}
		}

		return unknownStateIndex;
	}
}
