package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides a "history" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleHistory extends CounterExampleUnaryOperator {
	public CounterExampleHistory(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("H", "History", counterExample, argument);

		CounterExampleNegation notArgument = new CounterExampleNegation(
				counterExample, argument);
		checkByOnce(counterExample, notArgument);
		checkBySince(counterExample, argument, notArgument);
	}

	private void checkBySince(final CounterExample counterExample,
			final CounterExampleProposition argument,
			CounterExampleNegation notArgument) {
		CounterExampleValueType[] trueValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(trueValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate truePredicate = new CounterExamplePredicate("",
				counterExample, Arrays.asList(trueValues));

		CounterExampleSince since = new CounterExampleSince(counterExample,
				truePredicate, notArgument);
		addCheck(new CounterExampleNegation(counterExample, since));
	}

	private void checkByOnce(final CounterExample counterExample,
			CounterExampleNegation notArgument) {
		CounterExampleOnce onceOperator = new CounterExampleOnce(
				counterExample, notArgument);

		addCheck(new CounterExampleNegation(counterExample, onceOperator));
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		return calculateHistoryOperator(position);
	}

	private CounterExampleValueType calculateHistoryOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// remove all future values
		checkedValues = checkedValues.subList(0, position + 1);

		// look for a state with a false value
		int index = checkedValues.lastIndexOf(CounterExampleValueType.FALSE);

		if (index != -1) {
			result = CounterExampleValueType.FALSE;
		} else {
			if (!checkedValues.contains(CounterExampleValueType.UNKNOWN)) {
				result = CounterExampleValueType.TRUE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), true);

		return result;
	}
}
