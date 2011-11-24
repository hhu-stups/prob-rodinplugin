package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;
import de.prob.core.domainobjects.ltl.CounterExampleWeakUntil;

public class CounterExampleWeakUntilUnitTest {
	/*
	 * f-TTTF, g-FFFT, f U g-TTTT
	 */
	@Test
	public void testWeakUntilOnFinitePathTrueDefinition1() {
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
	 * f-TTTT, g-FFFF, f U g-TTTT
	 */
	@Test
	public void testWeakUntilOnFinitePathTrueDefinition2() {
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
	 * f-TTTF, g-FFFF, f U g-FFFF
	 */
	@Test
	public void testWeakUntilOnFinitePathFalseDefinition1() {
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
	public void testWeakUntilOnFinitePathFalseDefinition2() {
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
	 * f - FFTT g - TFFF f U g - TFTT
	 */
	@Test
	public void testWeakUntilOnFinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

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
	 * f - FFTT g - TFFF
	 */
	@Test
	public void testWeakUntilOnInfinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.INFINITE, 0, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// Loop entry = 1
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				secondArgumentValues);

		// create an operator
		weakUntilOperator = new CounterExampleWeakUntil(PathType.INFINITE, 1,
				firstArgument, secondArgument);

		// check result values
		values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = weakUntilOperator
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
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(firstHighlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3, 1 }));

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 1 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 2);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 1 }));

		// Loop entry = 2
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				secondArgumentValues);

		// create an operator
		weakUntilOperator = new CounterExampleWeakUntil(PathType.INFINITE, 2,
				firstArgument, secondArgument);

		// check result values
		values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = weakUntilOperator
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
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

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
		assertTrue(firstHighlightedPositions.get(3).size() == 2);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2 }));
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
		weakUntilOperator = new CounterExampleWeakUntil(PathType.INFINITE, 3,
				firstArgument, secondArgument);

		// check result values
		values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = weakUntilOperator
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
		assertTrue(secondHighlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				secondHighlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

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
	 * f-TTTU, g-UUUT, f U g-TTTT
	 */
	@Test
	public void testWeakUntilTrueDefinitionOnReducedPath() {
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
				"", PathType.FINITE, -1, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.FINITE, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
	public void testWeakUntilOnReducedPath() {
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
		CounterExampleBinaryOperator weakUntilOperator = new CounterExampleWeakUntil(
				PathType.REDUCED, -1, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = weakUntilOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = weakUntilOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = weakUntilOperator
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
