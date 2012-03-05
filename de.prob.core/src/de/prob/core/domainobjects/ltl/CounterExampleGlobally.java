package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides a "globally" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleGlobally extends CounterExampleUnaryOperator {
	private final CounterExampleRelease release;
	private final CounterExampleNegation notFinally;
	private final CounterExampleNegation notUntil;

	public CounterExampleGlobally(final PathType pathType, final int loopEntry,
			final CounterExampleProposition argument) {
		super("G", "Globally", pathType, loopEntry, argument);

		CounterExampleValueType[] falseValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(falseValues, CounterExampleValueType.FALSE);

		CounterExamplePredicate falsePredicate = new CounterExamplePredicate(
				pathType, loopEntry, Arrays.asList(falseValues));

		release = new CounterExampleRelease(pathType, loopEntry,
				falsePredicate, argument);

		CounterExampleNegation notArgument = new CounterExampleNegation(
				pathType, loopEntry, argument);

		CounterExampleFinally finallyOperator = new CounterExampleFinally(
				pathType, loopEntry, notArgument);

		notFinally = new CounterExampleNegation(pathType, loopEntry,
				finallyOperator);

		CounterExampleValueType[] trueValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(trueValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate truePredicate = new CounterExamplePredicate(
				pathType, loopEntry, Arrays.asList(trueValues));

		CounterExampleUntil until = new CounterExampleUntil(pathType,
				loopEntry, truePredicate, notArgument);
		notUntil = new CounterExampleNegation(pathType, loopEntry, until);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = calculateGlobally(position);

		List<CounterExampleValueType> releaseValues = release.getValues();
		List<CounterExampleValueType> notFinallyValues = notFinally.getValues();
		List<CounterExampleValueType> notUntilValues = notUntil.getValues();

		Logger.assertProB("Globally invalid",
				value == releaseValues.get(position));

		Logger.assertProB("Globally invalid",
				value == notFinallyValues.get(position));

		Logger.assertProB("Globally invalid",
				value == notUntilValues.get(position));

		return value;
	}

	private CounterExampleValueType calculateGlobally(final int position) {
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		List<CounterExampleValueType> checkedValues = new ArrayList<CounterExampleValueType>(
				argument.getValues());

		// add future values if a path is infinite
		if (pathType == PathType.INFINITE && position > loopEntry) {
			checkedValues.addAll(checkedValues.subList(loopEntry, position));
		}

		// remove all past values
		checkedValues = checkedValues.subList(position, checkedValues.size());

		// look for a state with a false value
		int index = checkedValues.indexOf(CounterExampleValueType.FALSE);

		if (index != -1) {
			result = CounterExampleValueType.FALSE;
		} else {
			if (pathType != PathType.REDUCED) {
				result = CounterExampleValueType.TRUE;
			}
		}

		fillHighlightedPositions(position, index, checkedValues.size(), false);

		return result;
	}
}
