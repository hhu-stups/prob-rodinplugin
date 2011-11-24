package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "Finally" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleFinally extends CounterExampleUnaryOperator {
	private final CounterExampleUntil until;

	public CounterExampleFinally(final PathType pathType, final int loopEntry,
			final CounterExampleProposition argument) {
		super("F", "Finally", pathType, loopEntry, argument);

		CounterExampleValueType[] firstValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(firstValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate first = new CounterExamplePredicate(pathType,
				loopEntry, Arrays.asList(firstValues));

		until = new CounterExampleUntil(pathType, loopEntry, first, argument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateFinallyOperator(position);

		List<CounterExampleValueType> untilValues = until.getValues();

		Logger.assertProB("Finally invalid", value == untilValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateFinallyOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

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
