package de.prob.core.domainobjects.ltl;


/**
 * Provides a "not" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleNegation extends CounterExampleUnaryOperator {
	public CounterExampleNegation(final CounterExample counterExample,
			final CounterExampleProposition argument) {
		super("not", "Negation", counterExample, argument);
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
		CounterExampleValueType result = CounterExampleValueType.UNKNOWN;

		if (value == CounterExampleValueType.TRUE) {
			result = CounterExampleValueType.FALSE;
		} else if (value == CounterExampleValueType.FALSE) {
			result = CounterExampleValueType.TRUE;
		}

		return result;
	}
}
