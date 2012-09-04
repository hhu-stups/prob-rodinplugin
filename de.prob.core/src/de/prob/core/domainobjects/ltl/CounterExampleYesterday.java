package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a "yesterday" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleYesterday extends CounterExampleUnaryOperator {
	public CounterExampleYesterday(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("Y", "Yesterday", counterExample, argument);
	}

	@Override
	public CounterExampleValueType calculate(final int position) {
		CounterExampleValueType result = calculateYesterday(position);
		return result;
	}

	private CounterExampleValueType calculateYesterday(int position) {
		CounterExampleValueType result = CounterExampleValueType.FALSE;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// remove all future values
		checkedValues = checkedValues.subList(0, position + 1);

		int index = -1;

		if (checkedValues.size() > 1) {
			index = position - 1;
			result = argument.getValues().get(index);
		}

		fillHighlightedPositions(position, index, -1, true);

		return result;
	}
}
