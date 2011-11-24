package de.prob.core.domainobjects.ltl;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides a "Not" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleNegation extends CounterExampleUnaryOperator {
	public CounterExampleNegation(final PathType pathType, final int loopEntry,
			final CounterExampleProposition argument) {
		super("not", "Negation", pathType, loopEntry, argument);
	}

	public CounterExampleNegation(final PathType pathType,
			final CounterExampleProposition argument) {
		this(pathType, -1, argument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType value = argument.getValues().get(position);
		CounterExampleValueType result = calculateNotOperator(value);

		fillHighlightedPositions(position, -1, 1, false);

		return result;
	}

	public static CounterExampleValueType calculateNotOperator(
			final CounterExampleValueType value) {
		CounterExampleValueType result = CounterExampleValueType.UNDEFINED;

		if (value == CounterExampleValueType.TRUE) {
			result = CounterExampleValueType.FALSE;
		} else if (value == CounterExampleValueType.FALSE) {
			result = CounterExampleValueType.TRUE;
		}

		return result;
	}
}
