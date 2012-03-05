package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "weak until" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleWeakUntil extends CounterExampleBinaryOperator {
	private final CounterExampleRelease release;
	private final CounterExampleDisjunction or1;
	private final CounterExampleDisjunction or2;

	public CounterExampleWeakUntil(final PathType pathType,
			final int loopEntry, final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("W", "Weak Until", pathType, loopEntry, firstArgument,
				secondArgument);

		release = new CounterExampleRelease(pathType, loopEntry,
				secondArgument, new CounterExampleDisjunction(pathType,
						loopEntry, secondArgument, firstArgument));

		CounterExampleNegation not = new CounterExampleNegation(pathType,
				loopEntry, firstArgument);

		CounterExampleValueType[] trueValues = new CounterExampleValueType[firstArgument
				.getValues().size()];
		Arrays.fill(trueValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate truePredicate = new CounterExamplePredicate(
				pathType, loopEntry, Arrays.asList(trueValues));

		CounterExampleNegation notUntil = new CounterExampleNegation(pathType,
				loopEntry, new CounterExampleUntil(pathType, loopEntry,
						truePredicate, not));

		CounterExampleUntil until = new CounterExampleUntil(pathType,
				loopEntry, firstArgument, secondArgument);

		or1 = new CounterExampleDisjunction(pathType, loopEntry, notUntil,
				until);
		or2 = new CounterExampleDisjunction(pathType, loopEntry, until,
				new CounterExampleGlobally(pathType, loopEntry, firstArgument));
	}

	public CounterExampleWeakUntil(final PathType pathType,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		this(pathType, -1, firstArgument, secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		final CounterExampleValueType value = calculateWeakUntilOperator(position);

		Logger.assertProB("Weak Until invalid", value == release.getValues()
				.get(position));

		Logger.assertProB("Weak Until invalid",
				value == or1.getValues().get(position));

		Logger.assertProB("Weak Until invalid",
				value == or2.getValues().get(position));

		return value;
	}

	private CounterExampleValueType calculateWeakUntilOperator(
			final int position) {
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

		int firstIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in second argument
		int secondIndex = secondCheckedValues
				.indexOf(CounterExampleValueType.TRUE);

		if (secondIndex != -1) {
			// look for a state with a false value in first argument
			firstIndex = firstCheckedValues.subList(0, secondIndex).indexOf(
					CounterExampleValueType.FALSE);

			if (firstIndex == -1) {
				trueOrUnknown = true;

				firstCheckedValues = firstCheckedValues.subList(0,
						secondIndex + 1);
				secondCheckedValues = secondCheckedValues.subList(0,
						secondIndex + 1);

				// look for a state with an unknown value in first and
				// second argument
				final int unknownStateIndex = indexOfUnknownState(
						firstCheckedValues, secondCheckedValues, false);

				if (unknownStateIndex != -1) {
					firstCheckedValues = firstCheckedValues.subList(0,
							unknownStateIndex + 1);
					secondCheckedValues = secondCheckedValues.subList(0,
							unknownStateIndex + 1);

					secondIndex = -1;
				} else {
					firstCheckedValues = firstCheckedValues.subList(0,
							secondIndex);

					// look for a state with an unknown value in first argument
					if (!firstCheckedValues
							.contains(CounterExampleValueType.UNKNOWN)) {
						result = CounterExampleValueType.TRUE;
					} else {
						secondIndex = -1;
					}
				}
			}
		} else {
			// all states of first argument are valid and all states of second
			// argument are invalid on a finite or an infinite path
			if (pathType != PathType.REDUCED
					&& !firstCheckedValues
							.contains(CounterExampleValueType.FALSE)) {
				trueOrUnknown = true;
				result = CounterExampleValueType.TRUE;
				secondCheckedValues.clear();
			}
		}

		if (!trueOrUnknown) {
			// look for a state with a false value in first argument
			firstIndex = firstCheckedValues
					.indexOf(CounterExampleValueType.FALSE);

			if (firstIndex != -1) {
				secondCheckedValues = secondCheckedValues.subList(0,
						firstIndex + 1);
				secondIndex = -1;

				// look for a state with an unknown value in second argument
				if (!secondCheckedValues
						.contains(CounterExampleValueType.UNKNOWN)) {
					result = CounterExampleValueType.FALSE;
				} else {
					firstCheckedValues = firstCheckedValues.subList(0,
							firstIndex + 1);
					firstIndex = -1;

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
