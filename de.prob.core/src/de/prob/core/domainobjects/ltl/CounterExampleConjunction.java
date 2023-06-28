package de.prob.core.domainobjects.ltl;


/**
 * Provides an "and" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleConjunction extends
		CounterExampleBinaryOperator {
	public CounterExampleConjunction(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("and", "Conjunction", counterExample, firstArgument,
				secondArgument);
		addCheckByDeMorgan(counterExample, firstArgument, secondArgument);
	}

	private void addCheckByDeMorgan(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		CounterExampleNegation notFirstArgument = new CounterExampleNegation(
				counterExample, firstArgument);
		CounterExampleNegation notSecondArgument = new CounterExampleNegation(
				counterExample, secondArgument);
		CounterExampleDisjunction or = new CounterExampleDisjunction(
				counterExample, notFirstArgument, notSecondArgument);
		addCheck(new CounterExampleNegation(counterExample, or));
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType firstValue = getFirstArgument().getValues()
				.get(position);
		CounterExampleValueType secondValue = getSecondArgument().getValues()
				.get(position);

		int firstCheckedSize = 1;
		int secondCheckedSize = 1;

		if (firstValue == CounterExampleValueType.FALSE) {
			secondCheckedSize = 0;
		} else if (secondValue == CounterExampleValueType.FALSE) {
			firstCheckedSize = 0;
		}

		fillHighlightedPositions(position, -1, -1, firstCheckedSize,
				secondCheckedSize, false);

		CounterExampleValueType value = calculateAnd(firstValue, secondValue);

		return value;
	}

	public static CounterExampleValueType calculateAnd(
			final CounterExampleValueType firstValue,
			final CounterExampleValueType secondValue) {
		CounterExampleValueType result = CounterExampleValueType.TRUE;

		if (firstValue == CounterExampleValueType.FALSE
				|| secondValue == CounterExampleValueType.FALSE) {
			result = CounterExampleValueType.FALSE;
		} else if (firstValue == CounterExampleValueType.UNKNOWN
				|| secondValue == CounterExampleValueType.UNKNOWN) {
			result = CounterExampleValueType.UNKNOWN;
		}

		return result;
	}
}
