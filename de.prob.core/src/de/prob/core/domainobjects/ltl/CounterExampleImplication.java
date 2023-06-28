package de.prob.core.domainobjects.ltl;


/**
 * Provides an "imply" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleImplication extends
		CounterExampleBinaryOperator {
	public CounterExampleImplication(final CounterExample counterExample,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("=>", "Implication", counterExample, firstArgument,
				secondArgument);
	}

	@Override
	protected CounterExampleValueType calculate(final int position) {
		CounterExampleValueType firstValue = getFirstArgument().getValues()
				.get(position);
		firstValue = CounterExampleNegation.calculateNotOperator(firstValue);

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

		return CounterExampleDisjunction.calculateOr(firstValue, secondValue);
	}
}
