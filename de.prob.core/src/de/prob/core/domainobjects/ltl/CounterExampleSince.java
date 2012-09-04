package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a "since" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleSince extends CounterExampleBinaryOperator {
	public CounterExampleSince(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("S", "Since", counterExample, firstArgument, secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		final CounterExampleValueType result = calculateSinceOperator(position);
		return result;
	}

	private CounterExampleValueType calculateSinceOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		// remove all future values
		List<CounterExampleValueType> firstCheckedValues = new ArrayList<CounterExampleValueType>(
				getFirstArgument().getValues().subList(0, position + 1));
		List<CounterExampleValueType> secondCheckedValues = new ArrayList<CounterExampleValueType>(
				getSecondArgument().getValues().subList(0, position + 1));

		int firstIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in second argument
		int secondIndex = secondCheckedValues
				.lastIndexOf(CounterExampleValueType.TRUE);

		if (secondIndex != -1) {
			// look for a state with a false value in first argument
			firstIndex = firstCheckedValues.subList(secondIndex + 1,
					firstCheckedValues.size()).lastIndexOf(
					CounterExampleValueType.FALSE);

			if (firstIndex == -1) {
				trueOrUnknown = true;

				// look for a state with an unknown value in first and
				// second argument
				int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues.subList(secondIndex + 1,
								firstCheckedValues.size()),
						secondCheckedValues.subList(secondIndex + 1,
								secondCheckedValues.size()), true);

				if (unknownStateIndex != -1) {
					unknownStateIndex += (secondIndex + 1);
					firstCheckedValues = firstCheckedValues.subList(
							unknownStateIndex, firstCheckedValues.size());
					secondCheckedValues = secondCheckedValues.subList(
							unknownStateIndex, secondCheckedValues.size());

					secondIndex = -1;
				} else {
					firstCheckedValues = firstCheckedValues.subList(
							secondIndex + 1, firstCheckedValues.size());

					// look for the state with an unknown value in first
					// argument
					if (!firstCheckedValues
							.contains(CounterExampleValueType.UNKNOWN)) {
						result = CounterExampleValueType.TRUE;
					} else {
						secondCheckedValues = secondCheckedValues.subList(
								secondIndex, secondCheckedValues.size());
						secondIndex = -1;
					}
				}
			}
		}

		if (!trueOrUnknown) {
			// look for a state with a false value in first argument
			firstIndex = firstCheckedValues
					.lastIndexOf(CounterExampleValueType.FALSE);

			if (firstIndex != -1) {
				secondCheckedValues = secondCheckedValues.subList(firstIndex,
						secondCheckedValues.size());
				secondIndex = -1;

				// look for a state with an unknown value in second argument
				if (!secondCheckedValues
						.contains(CounterExampleValueType.UNKNOWN)) {
					result = CounterExampleValueType.FALSE;
				} else {
					firstCheckedValues = firstCheckedValues.subList(firstIndex,
							firstCheckedValues.size());

					// look for a state with an unknown value in first and
					// second argument
					int unknownStateIndex = indexOfUnknownState(
							firstCheckedValues, secondCheckedValues, true);

					if (unknownStateIndex != -1) {
						firstCheckedValues = firstCheckedValues.subList(
								unknownStateIndex, firstCheckedValues.size());
						secondCheckedValues = secondCheckedValues.subList(
								unknownStateIndex, secondCheckedValues.size());
					}

					firstIndex = -1;
				}
			} else {
				// all states of first argument are valid and all states of
				// second argument are invalid
				if (!firstCheckedValues.contains(CounterExampleValueType.FALSE)
						&& !firstCheckedValues
								.contains(CounterExampleValueType.UNKNOWN)
						&& !secondCheckedValues
								.contains(CounterExampleValueType.TRUE)
						&& !secondCheckedValues
								.contains(CounterExampleValueType.UNKNOWN)) {
					result = CounterExampleValueType.FALSE;
					firstCheckedValues.clear();
				} else {
					// look for a state with an unknown value in first and
					// second argument
					final int unknownStateIndex = indexOfUnknownState(
							firstCheckedValues, secondCheckedValues, true);

					if (unknownStateIndex != -1) {
						firstCheckedValues = firstCheckedValues.subList(
								unknownStateIndex, firstCheckedValues.size());
						secondCheckedValues = secondCheckedValues.subList(
								unknownStateIndex, secondCheckedValues.size());
					}
				}
			}
		}

		fillHighlightedPositions(position, firstIndex, secondIndex,
				firstCheckedValues.size(), secondCheckedValues.size(), true);

		return result;
	}
}
