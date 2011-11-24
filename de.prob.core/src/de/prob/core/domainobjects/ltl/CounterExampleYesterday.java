package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "Yesterday" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleYesterday extends CounterExampleUnaryOperator {
	public CounterExampleYesterday(final PathType pathType,
			final int loopEntry, final CounterExampleProposition argument) {
		super("Y", "Yesterday", pathType, loopEntry, argument);
	}

	public CounterExampleYesterday(final PathType pathType,
			final CounterExampleProposition argument) {
		this(pathType, -1, argument);
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
