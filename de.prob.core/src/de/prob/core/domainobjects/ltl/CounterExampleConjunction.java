package de.prob.core.domainobjects.ltl;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.logging.Logger;

/**
 * Provides an "and" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleConjunction extends
		CounterExampleBinaryOperator {
	private final CounterExampleNegation not;

	public CounterExampleConjunction(final PathType pathType,
			final int loopEntry, final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		super("and", "Conjunction", pathType, loopEntry, firstArgument,
				secondArgument);

		CounterExampleNegation notFirstArgument = new CounterExampleNegation(
				pathType, loopEntry, firstArgument);
		CounterExampleNegation notSecondArgument = new CounterExampleNegation(
				pathType, loopEntry, secondArgument);
		CounterExampleDisjunction or = new CounterExampleDisjunction(pathType,
				loopEntry, notFirstArgument, notSecondArgument);
		not = new CounterExampleNegation(pathType, loopEntry, or);
	}

	public CounterExampleConjunction(final PathType pathType,
			final CounterExampleProposition firstArgument,
			final CounterExampleProposition secondArgument) {
		this(pathType, -1, firstArgument, secondArgument);
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

		Logger.assertProB("And invalid", value == not.getValues().get(position));

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
