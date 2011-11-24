package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "History" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleHistory extends CounterExampleUnaryOperator {
	private final CounterExampleNegation notOnce;
	private final CounterExampleNegation notSince;

	public CounterExampleHistory(final PathType pathType, final int loopEntry,
			final CounterExampleProposition argument) {
		super("H", "History", pathType, loopEntry, argument);

		CounterExampleNegation notArgument = new CounterExampleNegation(pathType,
				loopEntry, argument);

		CounterExampleOnce onceOperator = new CounterExampleOnce(pathType,
				loopEntry, notArgument);

		notOnce = new CounterExampleNegation(pathType, loopEntry, onceOperator);

		CounterExampleValueType[] trueValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(trueValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate truePredicate = new CounterExamplePredicate(
				pathType, loopEntry, Arrays.asList(trueValues));

		CounterExampleSince since = new CounterExampleSince(pathType,
				loopEntry, truePredicate, notArgument);
		notSince = new CounterExampleNegation(pathType, loopEntry, since);
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateHistoryOperator(position);

		List<CounterExampleValueType> notOnceValues = notOnce.getValues();
		List<CounterExampleValueType> notSinceValues = notSince.getValues();

		Logger.assertProB("History invalid",
				value == notOnceValues.get(position));

		Logger.assertProB("History invalid",
				value == notSinceValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateHistoryOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// remove all future values
		checkedValues = checkedValues.subList(0, position + 1);

		// look for a state with a false value
		int index = checkedValues.lastIndexOf(CounterExampleValueType.FALSE);

		if (index != -1) {
			result = CounterExampleValueType.FALSE;
		} else {
			if (!checkedValues.contains(CounterExampleValueType.UNDEFINED)) {
				result = CounterExampleValueType.TRUE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), true);

		return result;
	}
}
