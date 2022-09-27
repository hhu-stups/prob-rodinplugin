package de.prob.core.domainobjects.ltl;


/**
 * Provides an "or" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleDisjunction extends
		CounterExampleBinaryOperator {
	public CounterExampleDisjunction(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("or", "Disjunction", counterExample, firstArgument,
				secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType firstValue = getFirstArgument().getValues()
				.get(position);
		CounterExampleValueType secondValue = getSecondArgument().getValues()
				.get(position);

		int firstCheckedSize = 1;
		int secondCheckedSize = 1;

		if (firstValue == CounterExampleValueType.TRUE) {
			secondCheckedSize = 0;
		} else if (secondValue == CounterExampleValueType.TRUE) {
			firstCheckedSize = 0;
		}

		fillHighlightedPositions(position, -1, -1, firstCheckedSize,
				secondCheckedSize, false);

		return calculateOr(firstValue, secondValue);
	}

	public static CounterExampleValueType calculateOr(
			final CounterExampleValueType firstValue,
			final CounterExampleValueType secondValue) {
		CounterExampleValueType result = CounterExampleValueType.FALSE;

		if (firstValue == CounterExampleValueType.TRUE
				|| secondValue == CounterExampleValueType.TRUE) {
			result = CounterExampleValueType.TRUE;
		} else if (firstValue == CounterExampleValueType.UNKNOWN
				|| secondValue == CounterExampleValueType.UNKNOWN) {
			result = CounterExampleValueType.UNKNOWN;
		}

		return result;
	}
}
