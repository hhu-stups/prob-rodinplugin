package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleDisjunction;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for an "or" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleOrUnitTest {
	/*
	 * f-FTTF, g-TTFF, f Or g-TTTF
	 */
	@Test
	public void testOrOnFinitePath() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.FINITE, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.FINITE, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator orOperator = new CounterExampleDisjunction(
				PathType.FINITE, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = orOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = orOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-FTTF, g-TTFF, f Or g-FTFF
	 */
	@Test
	public void testOrOnInfinitePath() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// Loop entry = 0
		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.INFINITE, 0, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.INFINITE, 0, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator orOperator = new CounterExampleDisjunction(
				PathType.INFINITE, 0, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = orOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = orOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 1
		// create first argument
		firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
				secondArgumentValues);

		// create an operator
		orOperator = new CounterExampleDisjunction(PathType.INFINITE, 1,
				firstArgument, secondArgument);

		// check result values
		values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = orOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = orOperator.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 2
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				secondArgumentValues);

		// create an operator
		orOperator = new CounterExampleDisjunction(PathType.INFINITE, 2,
				firstArgument, secondArgument);

		// check result values
		values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = orOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = orOperator.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 3
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				secondArgumentValues);

		// create an operator
		orOperator = new CounterExampleDisjunction(PathType.INFINITE, 3,
				firstArgument, secondArgument);

		// check result values
		values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = orOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = orOperator.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-FTTF, g-TTFF, f Or g-TTTF
	 */
	@Test
	public void testOrOnReducedPath1() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.REDUCED, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.REDUCED, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator orOperator = new CounterExampleDisjunction(
				PathType.REDUCED, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = orOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = orOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 0);
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 0);

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-FUUU, g-UTUF, f Or g-UTUU
	 */
	@Test
	public void testOrOnReducedPath2() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.UNKNOWN });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.FALSE });

		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.REDUCED, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.REDUCED, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator orOperator = new CounterExampleDisjunction(
				PathType.REDUCED, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = orOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = orOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = orOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 0);
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}
}
