package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "release" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleRelease extends CounterExampleBinaryOperator {
	public CounterExampleRelease(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("R", "Release", counterExample, firstArgument, secondArgument);
		checkByUntil(counterExample, firstArgument, secondArgument);
	}

	private void checkByUntil(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		CounterExampleNegation notFirst = new CounterExampleNegation(
				counterExample, firstArgument);
		CounterExampleNegation notSecond = new CounterExampleNegation(
				counterExample, secondArgument);
		CounterExampleUntil until = new CounterExampleUntil(counterExample,
				notFirst, notSecond);
		addCheck(new CounterExampleNegation(counterExample, until));
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		return calculateReleaseOperator(position);
	}

	private CounterExampleValueType calculateReleaseOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> firstCheckedValues = new ArrayList<CounterExampleValueType>(
				getFirstArgument().getValues());
		List<CounterExampleValueType> secondCheckedValues = new ArrayList<CounterExampleValueType>(
				getSecondArgument().getValues());

		// add future values if a path is infinite
		if (pathType == PathType.INFINITE && position > loopEntry) {
			firstCheckedValues.addAll(firstCheckedValues.subList(loopEntry,
					position));
			secondCheckedValues.addAll(secondCheckedValues.subList(loopEntry,
					position));
		}

		// remove all past values
		firstCheckedValues = firstCheckedValues.subList(position,
				firstCheckedValues.size());
		secondCheckedValues = secondCheckedValues.subList(position,
				secondCheckedValues.size());

		int secondIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in first argument
		int firstIndex = firstCheckedValues
				.indexOf(CounterExampleValueType.TRUE);

		if (firstIndex != -1) {
			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues.subList(0, firstIndex + 1)
					.indexOf(CounterExampleValueType.FALSE);

			if (secondIndex == -1) {
				trueOrUnknown = true;

				firstCheckedValues = firstCheckedValues.subList(0,
						firstIndex + 1);
				secondCheckedValues = secondCheckedValues.subList(0,
						firstIndex + 1);

				// look for a state with an unknown value in first and
				// second argument
				final int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues, secondCheckedValues, false);

				if (unknownStateIndex != -1) {
					firstCheckedValues = firstCheckedValues.subList(0,
							unknownStateIndex + 1);
					secondCheckedValues = secondCheckedValues.subList(0,
							unknownStateIndex + 1);

					firstIndex = -1;
				} else {
					// look for the state with an unknown value in second
					// argument
					if (!secondCheckedValues
							.contains(CounterExampleValueType.UNKNOWN)) {
						result = CounterExampleValueType.TRUE;
					} else {
						firstIndex = -1;
					}
				}
			}
		} else {
			// all states of first argument are invalid and all states of second
			// argument are valid on a finite or an infinite path
			if (pathType != PathType.REDUCED
					&& !secondCheckedValues
							.contains(CounterExampleValueType.FALSE)) {
				trueOrUnknown = true;
				result = CounterExampleValueType.TRUE;
				firstCheckedValues.clear();
			}
		}

		if (!trueOrUnknown) {
			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues
					.indexOf(CounterExampleValueType.FALSE);

			if (secondIndex != -1) {
				firstCheckedValues = firstCheckedValues.subList(0, secondIndex);
				firstIndex = -1;

				// look for a state with an unknown value in first argument
				if (!firstCheckedValues
						.contains(CounterExampleValueType.UNKNOWN)) {
					result = CounterExampleValueType.FALSE;
				} else {
					// look for a state with an unknown value in first and
					// second argument
					final int unknownStateIndex = indexOfUnknownState(
							firstCheckedValues,
							secondCheckedValues.subList(0, secondIndex), false);

					if (unknownStateIndex != -1) {
						firstCheckedValues = firstCheckedValues.subList(0,
								unknownStateIndex + 1);
						secondCheckedValues = secondCheckedValues.subList(0,
								unknownStateIndex + 1);
					} else {
						secondCheckedValues = secondCheckedValues.subList(0,
								secondIndex + 1);
						secondIndex = -1;
					}

					secondIndex = -1;
				}
			} else {
				// look for a state with an unknown value in first and
				// second argument
				final int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues, secondCheckedValues, false);

				if (unknownStateIndex != -1) {
					firstCheckedValues = firstCheckedValues.subList(0,
							unknownStateIndex + 1);
					secondCheckedValues = secondCheckedValues.subList(0,
							unknownStateIndex + 1);
				}
			}
		}

		fillHighlightedPositions(position, firstIndex, secondIndex,
				firstCheckedValues.size(), secondCheckedValues.size(), false);

		return result;
	}
}
