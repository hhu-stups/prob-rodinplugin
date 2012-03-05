package de.prob.core.domainobjects.ltl;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides an "imply" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleImplication extends
		CounterExampleBinaryOperator {
	public CounterExampleImplication(final PathType pathType,
			final int loopEntry, final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("=>", "Implication", pathType, loopEntry, firstArgument,
				secondArgument);
	}

	public CounterExampleImplication(final PathType pathType,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		this(pathType, -1, firstArgument, secondArgument);
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
