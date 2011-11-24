package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides an "Once" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleOnce extends CounterExampleUnaryOperator {
	private final CounterExampleSince since;

	public CounterExampleOnce(final PathType pathType, final int loopEntry,
			final CounterExampleProposition argument) {
		super("O", "Once", pathType, loopEntry, argument);

		CounterExampleValueType[] firstValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(firstValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate first = new CounterExamplePredicate(pathType,
				loopEntry, Arrays.asList(firstValues));

		since = new CounterExampleSince(pathType, loopEntry, first, argument);
	}

	public CounterExampleOnce(final PathType pathType,
			final CounterExampleProposition argument) {
		this(pathType, -1, argument);
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateOnceOperator(position);

		List<CounterExampleValueType> sinceValues = since.getValues();

		Logger.assertProB("Once invalid", value == sinceValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateOnceOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// remove all future values
		checkedValues = checkedValues.subList(0, position + 1);

		// look for a state with a true value
		int index = checkedValues.lastIndexOf(CounterExampleValueType.TRUE);

		if (index != -1) {
			result = CounterExampleValueType.TRUE;
		} else {
			if (!checkedValues.contains(CounterExampleValueType.UNDEFINED)) {
				result = CounterExampleValueType.FALSE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), true);

		return result;
	}
}
