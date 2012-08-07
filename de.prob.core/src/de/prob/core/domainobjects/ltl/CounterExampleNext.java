package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "next" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleNext extends CounterExampleUnaryOperator {
	public CounterExampleNext(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("X", "Next", counterExample, argument);
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		return calculateNextOperator(position);
	}

	private CounterExampleValueType calculateNextOperator(int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// add future values if a path is infinite
		if (pathType == PathType.INFINITE && position > loopEntry) {
			checkedValues.addAll(checkedValues.subList(loopEntry, position));
		}

		// remove all past values
		checkedValues = checkedValues.subList(position, checkedValues.size());

		int index = -1;

		if (checkedValues.size() > 1) {
			index = 1;
			result = checkedValues.get(index);
		} else {
			if (pathType == PathType.FINITE) {
				result = CounterExampleValueType.FALSE;
			} else if (pathType == PathType.INFINITE) {
				index = 0;
				result = checkedValues.get(0);
			}
		}

		fillHighlightedPositions(position, index, -1, false);

		return result;
	}
}
