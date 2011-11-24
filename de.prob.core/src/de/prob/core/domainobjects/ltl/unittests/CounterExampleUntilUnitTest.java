package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUntil;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for an "Until" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public class CounterExampleUntilUnitTest {
	/*
	 * f-TTTF, g-FFFT, f U g-TTTT
	 */
	@Test
	public void testUntilOnFinitePathTrueDefinition() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 0);
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TTTF, g-FFFF, f U g-FFFF
	 */
	@Test
	public void testUntilOnFinitePathFalseDefinition1() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

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
	 * f-TTFF, g-FFFT, f U g-FFFT
	 */
	@Test
	public void testUntilOnFinitePathFalseDefinition2() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));

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
		assertTrue(firstHighlightedPositions.get(3).size() == 0);
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TTTT, g-FFFF, f U g-FFFF
	 */
	@Test
	public void testUntilOnFinitePathFalseDefinition3() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

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
	 * f - TFFT g - FTFF f U g - TTFF
	 */
	@Test
	public void testUntilOnFinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
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
				new Integer[] { 1 }));

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

	/*
	 * f - TFFT g - FTFF
	 */
	@Test
	public void testUntilOnInfinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// Loop entry = 0
		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.INFINITE, 0, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.INFINITE, 0, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.INFINITE, 0, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
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
				new Integer[] { 1 }));

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
		assertTrue(firstHighlightedPositions.get(3).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 0 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// Loop entry = 1
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				secondArgumentValues);

		// create an operator
		untilOperator = new CounterExampleUntil(PathType.INFINITE, 1,
				firstArgument, secondArgument);

		// check result values
		values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = untilOperator
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
				new Integer[] { 1 }));

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
				new Integer[] { 1 }));

		// Loop entry = 2
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				secondArgumentValues);

		// create an operator
		untilOperator = new CounterExampleUntil(PathType.INFINITE, 2,
				firstArgument, secondArgument);

		// check result values
		values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = untilOperator
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
				new Integer[] { 1 }));

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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2 }));

		// Loop entry = 3
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				secondArgumentValues);

		// create an operator
		untilOperator = new CounterExampleUntil(PathType.INFINITE, 3,
				firstArgument, secondArgument);

		// check result values
		values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = untilOperator
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
				new Integer[] { 1 }));

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

	/*
	 * f-TTTU, g-UUUT, f U g-TTTT
	 */
	@Test
	public void testUntilOnReducedPathTrueDefinition() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 0);
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-UUFU, g-FFFT, f U g-FFFT
	 */
	@Test
	public void testUntilOnReducedPathFalseDefinition1() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));

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
		assertTrue(firstHighlightedPositions.get(3).size() == 0);
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-UUUF, g-FFFF, f U g-FFFF
	 */
	@Test
	public void testUntilOnReducedPathFalseDefinition2() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

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
	 * f-UTUU, g-FUTU, f U g-UTTU
	 */
	@Test
	public void testUntilOnReducedPathUnknownDefinition1() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 0);
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

	/*
	 * f-UUFU, g-FFUU, f U g-UUUU
	 */
	@Test
	public void testUntilOnReducedPathUnknownDefinition2() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));

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

	/*
	 * f-UTUU, g-FUUU, f U g-UUUU
	 */
	@Test
	public void testUntilOnReducedPathUnknownDefinition3() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2 }));

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

	/*
	 * f-TTTT, g-FFFF, f U g-UUUU
	 */
	@Test
	public void testUntilOnReducedPathUnknownDefinition4() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

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
	 * f-UTUT, g-FUFU, f U g-UUUU
	 */
	@Test
	public void testUntilOnReducedPathUnknownDefinition5() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
				.getSecondHighlightedPositions();
		assertTrue(firstHighlightedPositions.size() == firstArgumentValues
				.size());
		assertTrue(secondHighlightedPositions.size() == secondArgumentValues
				.size());

		// State 0
		assertTrue(firstHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(0).size() == 4);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1, 2, 3 }));

		// State 1
		assertTrue(firstHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));
		assertTrue(secondHighlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

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
	 * f - UTFU g - FTFF f U g - UTFU
	 */
	@Test
	public void testUntilOnReducedPath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = untilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = untilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = untilOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0, 1 }));

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
