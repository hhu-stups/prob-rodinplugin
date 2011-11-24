package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "Weak Until" operator.
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

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateWeakUntilOperator(position);

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

		int firstIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in second argument
		int secondIndex = secondCheckedValues
				.indexOf(CounterExampleValueType.TRUE);

		if (secondIndex != -1) {
			// look for a state with a false value in first argument
			firstCheckedValues = firstCheckedValues.subList(0, secondIndex);
			firstIndex = firstCheckedValues
					.indexOf(CounterExampleValueType.FALSE);

			if (firstIndex == -1) {
				trueOrUnknown = true;

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.TRUE;
				} else {
					// look for the state with an unknown value in first
					// argument
					if (firstCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						secondCheckedValues = secondCheckedValues.subList(0,
								secondIndex + 1);
						secondIndex = -1;
					} else {
						result = CounterExampleValueType.TRUE;
					}
				}
			}
		} else {
			// look for a state with a false value in first argument
			if (!firstCheckedValues.contains(CounterExampleValueType.FALSE)) {
				if (pathType != PathType.REDUCED) {
					trueOrUnknown = true;
					result = CounterExampleValueType.TRUE;
				}
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

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.FALSE;
				} else {
					// look for a state with an unknown value in second argument
					if (secondCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						firstCheckedValues = firstCheckedValues.subList(0,
								firstIndex + 1);
						firstIndex = -1;
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
