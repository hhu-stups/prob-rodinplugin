package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides an "once" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleOnce extends CounterExampleUnaryOperator {
	public CounterExampleOnce(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("O", "Once", counterExample, argument);
		checkBySince(counterExample, argument);
	}

	private void checkBySince(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		CounterExampleValueType[] firstValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(firstValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate first = new CounterExamplePredicate("",
				counterExample, Arrays.asList(firstValues));

		addCheck(new CounterExampleSince(counterExample, first, argument));
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		return calculateOnceOperator(position);
	}

	private CounterExampleValueType calculateOnceOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// remove all future values
		checkedValues = checkedValues.subList(0, position + 1);

		// look for a state with a true value
		int index = checkedValues.lastIndexOf(CounterExampleValueType.TRUE);

		if (index != -1) {
			result = CounterExampleValueType.TRUE;
		} else {
			if (!checkedValues.contains(CounterExampleValueType.UNKNOWN)) {
				result = CounterExampleValueType.FALSE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), true);

		return result;
	}
}
