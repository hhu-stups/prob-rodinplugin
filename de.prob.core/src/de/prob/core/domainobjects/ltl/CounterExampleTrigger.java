package de.prob.core.domainobjects.ltl;

import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "Trigger" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleTrigger extends CounterExampleBinaryOperator {
	private final CounterExampleNegation notSince;

	public CounterExampleTrigger(final PathType pathType, final int loopEntry,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("T", "Trigger", pathType, loopEntry, firstArgument,
				secondArgument);
		CounterExampleNegation notFirst = new CounterExampleNegation(pathType,
				loopEntry, firstArgument);
		CounterExampleNegation notSecond = new CounterExampleNegation(pathType,
				loopEntry, secondArgument);
		CounterExampleSince since = new CounterExampleSince(pathType,
				loopEntry, notFirst, notSecond);
		notSince = new CounterExampleNegation(pathType, loopEntry, since);
	}

	public CounterExampleTrigger(final PathType pathType,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		this(pathType, -1, firstArgument, secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateTriggerOperator(position);

		List<CounterExampleValueType> notSinceValues = notSince.getValues();

		Logger.assertProB("Trigger invalid",
				value == notSinceValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateTriggerOperator(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

		// remove all future values
		List<CounterExampleValueType> firstCheckedValues = getFirstArgument()
				.getValues().subList(0, position + 1);
		List<CounterExampleValueType> secondCheckedValues = getSecondArgument()
				.getValues().subList(0, position + 1);

		int secondIndex = -1;

		boolean trueOrUnknown = false;

		// look for a state with a true value in first argument
		int firstIndex = firstCheckedValues
				.lastIndexOf(CounterExampleValueType.TRUE);

		if (firstIndex != -1) {
			// look for a state with a false value in second argument
			secondCheckedValues = secondCheckedValues.subList(firstIndex,
					position + 1);
			secondIndex = secondCheckedValues
					.lastIndexOf(CounterExampleValueType.FALSE);

			if (secondIndex == -1) {
				trueOrUnknown = true;

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.TRUE;
				} else {
					// look for the state with an unknown value in second
					// argument
					if (secondCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						firstCheckedValues = firstCheckedValues.subList(
								firstIndex, position + 1);
						firstIndex = -1;
					} else {
						result = CounterExampleValueType.TRUE;
					}
				}
			}
		} else {
			if (!firstCheckedValues.contains(CounterExampleValueType.UNDEFINED)
					&& !secondCheckedValues
							.contains(CounterExampleValueType.FALSE)
					&& !secondCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
				trueOrUnknown = true;
				result = CounterExampleValueType.TRUE;
			}
		}

		if (!trueOrUnknown) {
			firstCheckedValues = getFirstArgument().getValues().subList(0,
					position + 1);
			secondCheckedValues = getSecondArgument().getValues().subList(0,
					position + 1);

			// look for a state with a false value in second argument
			secondIndex = secondCheckedValues
					.lastIndexOf(CounterExampleValueType.FALSE);

			if (secondIndex != -1) {
				firstCheckedValues = firstCheckedValues.subList(
						secondIndex + 1, position + 1);
				firstIndex = -1;

				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.FALSE;
				} else {
					// look for a state with an unknown value in first argument
					if (firstCheckedValues
							.contains(CounterExampleValueType.UNDEFINED)) {
						secondCheckedValues = secondCheckedValues.subList(
								secondIndex, position + 1);
						secondIndex = -1;
					} else {
						result = CounterExampleValueType.FALSE;
					}
				}
			} else {
				if (pathType != PathType.REDUCED) {
					result = CounterExampleValueType.FALSE;
				} else {
					for (int i = firstCheckedValues.size() - 1; i >= 0; i--) {
						if (firstCheckedValues.get(i).equals(
								CounterExampleValueType.UNDEFINED)
								&& secondCheckedValues.get(i).equals(
										CounterExampleValueType.UNDEFINED)) {
							firstCheckedValues = firstCheckedValues.subList(i,
									position + 1);
							secondCheckedValues = secondCheckedValues.subList(
									i, position + 1);

							break;
						}
					}
				}
			}
		}

		fillHighlightedPositions(position, firstIndex, secondIndex,
				firstCheckedValues.size(), secondCheckedValues.size(), true);

		return result;
	}
}
