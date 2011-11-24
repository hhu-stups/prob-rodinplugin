package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "Release" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleRelease extends CounterExampleBinaryOperator {
	private final CounterExampleNegation notUntil;

	public CounterExampleRelease(final PathType pathType, final int loopEntry,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("R", "Release", pathType, loopEntry, firstArgument,
				secondArgument);
		CounterExampleNegation notFirst = new CounterExampleNegation(pathType,
				loopEntry, firstArgument);
		CounterExampleNegation notSecond = new CounterExampleNegation(pathType,
				loopEntry, secondArgument);
		CounterExampleUntil until = new CounterExampleUntil(pathType,
				loopEntry, notFirst, notSecond);
		notUntil = new CounterExampleNegation(pathType, loopEntry, until);
	}

	public CounterExampleRelease(final PathType pathType,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		this(pathType, -1, firstArgument, secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateReleaseOperator(position);

		List<CounterExampleValueType> notUntilValues = notUntil.getValues();

		Logger.assertProB("Release invalid",
				value == notUntilValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateReleaseOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

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
			secondCheckedValues = secondCheckedValues
					.subList(0, firstIndex + 1);
			secondIndex = secondCheckedValues
					.indexOf(CounterExampleValueType.FALSE);

			if (secondIndex == -1) {
				trueOrUnknown = true;

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.TRUE;
				} else {
					// look for the state with an unknown value in second
					// argument
					if (secondCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						firstCheckedValues = firstCheckedValues.subList(0,
								firstIndex + 1);
						firstIndex = -1;
					} else {
						result = CounterExampleValueType.TRUE;
					}
				}
			}
		} else {
			// look for a state with a false value in second argument
			if (!secondCheckedValues.contains(CounterExampleValueType.FALSE)) {
				if (pathType != PathType.REDUCED) {
					trueOrUnknown = true;
					result = CounterExampleValueType.TRUE;
				}
			}
		}

		if (!trueOrUnknown) {
			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues
					.indexOf(CounterExampleValueType.FALSE);

			if (secondIndex != -1) {
				firstCheckedValues = firstCheckedValues.subList(0, secondIndex);
				firstIndex = -1;

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.FALSE;
				} else {
					// look for a state with an unknown value in first argument
					if (firstCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						secondCheckedValues = secondCheckedValues.subList(0,
								secondIndex + 1);
						secondIndex = -1;
					} else {
						result = CounterExampleValueType.FALSE;
					}
				}
			} else {
				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.FALSE;
				} else {
					for (int i = 0; i < firstCheckedValues.size(); i++) {
						if (firstCheckedValues.get(i).equals(
								CounterExampleValueType.UNDEFINED)
								&& secondCheckedValues.get(i).equals(
										CounterExampleValueType.UNDEFINED)) {
							firstCheckedValues = firstCheckedValues.subList(0,
									i + 1);
							secondCheckedValues = secondCheckedValues.subList(
									0, i + 1);

							break;
						}
					}
				}
			}
		}

		fillHighlightedPositions(position, firstIndex, secondIndex,
				firstCheckedValues.size(), secondCheckedValues.size(), false);

		return result;
	}
}
