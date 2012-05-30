package de.prob.core.domainobjects.ltl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "globally" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleGlobally extends CounterExampleUnaryOperator {
	public CounterExampleGlobally(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("G", "Globally", counterExample, argument);

		checkByRelease(counterExample, argument);
		CounterExampleNegation notArgument = new CounterExampleNegation(
				counterExample, argument);
		checkByFinally(counterExample, notArgument);
		checkByUntil(counterExample, argument, notArgument);
	}

	private void checkByUntil(final CounterExample counterExample,
			final CounterExampleProposition argument,
			CounterExampleNegation notArgument) {
		CounterExampleValueType[] trueValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(trueValues, CounterExampleValueType.TRUE);

		CounterExamplePredicate truePredicate = new CounterExamplePredicate("",
				counterExample, Arrays.asList(trueValues));

		CounterExampleUntil until = new CounterExampleUntil(counterExample,
				truePredicate, notArgument);
		addCheck(new CounterExampleNegation(counterExample, until));
	}

	private void checkByFinally(final CounterExample counterExample,
			CounterExampleNegation notArgument) {
		CounterExampleFinally finallyOperator = new CounterExampleFinally(
				counterExample, notArgument);

		addCheck(new CounterExampleNegation(counterExample, finallyOperator));
	}

	private void checkByRelease(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		CounterExampleValueType[] falseValues = new CounterExampleValueType[argument
				.getValues().size()];
		Arrays.fill(falseValues, CounterExampleValueType.FALSE);

		CounterExamplePredicate falsePredicate = new CounterExamplePredicate(
				"", counterExample, Arrays.asList(falseValues));

		addCheck(new CounterExampleRelease(counterExample, falsePredicate,
				argument));
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
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
