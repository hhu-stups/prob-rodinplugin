package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "finally" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleFinally extends CounterExampleUnaryOperator {
	public CounterExampleFinally(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("F", "Finally", counterExample, argument);
		checkByUntil(counterExample, argument);
	}

	private void checkByUntil(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		CounterExampleValueType[] firstValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(firstValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate first = new CounterExamplePredicate("",
				counterExample, Arrays.asList(firstValues));

		addCheck(new CounterExampleUntil(counterExample, first, argument));
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// add future values if a path is infinite
		if (pathType == PathType.INFINITE && position > loopEntry) {
			checkedValues.addAll(checkedValues.subList(loopEntry, position));
		}

		// remove all past values
		checkedValues = checkedValues.subList(position, checkedValues.size());

		// look for a state with a true value
		int index = checkedValues.indexOf(CounterExampleValueType.TRUE);

		if (index != -1) {
			result = CounterExampleValueType.TRUE;
		} else {
			if (pathType != PathType.REDUCED) {
				result = CounterExampleValueType.FALSE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), false);

		return result;
	}
}
