package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleConjunction;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

public class CounterExampleAndUnitTest {
	/*
	 * f-FTTF, g-TTFF, f And g-FTFF
	 */
	@Test
	public void testAndOnFinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.FINITE, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.FINITE, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator andOperator = new CounterExampleConjunction(
				PathType.FINITE, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = andOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-FTTF, g-TTFF, f And g-FTFF
	 */
	@Test
	public void testAndOnInfinitePath() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
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
		CounterExampleBinaryOperator andOperator = new CounterExampleConjunction(
				PathType.INFINITE, 0, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = andOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 1
		// create first argument
		firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
				secondArgumentValues);

		// create an operator
		andOperator = new CounterExampleConjunction(PathType.INFINITE, 1,
				firstArgument, secondArgument);

		// check result values
		values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = andOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 2
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				secondArgumentValues);

		// create an operator
		andOperator = new CounterExampleConjunction(PathType.INFINITE, 2,
				firstArgument, secondArgument);

		// check result values
		values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = andOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 3
		// create first argument
		firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				secondArgumentValues);

		// create an operator
		andOperator = new CounterExampleConjunction(PathType.INFINITE, 3,
				firstArgument, secondArgument);

		// check result values
		values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		firstHighlightedPositions = andOperator.getFirstHighlightedPositions();
		secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-FTTF, g-TTFF, f And g-FTFF
	 */
	@Test
	public void testAndOnReducedPath1() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.REDUCED, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.REDUCED, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator andOperator = new CounterExampleConjunction(
				PathType.REDUCED, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = andOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-FUUT, g-UTUU, f And g-FUUU
	 */
	@Test
	public void testAndOnReducedPath2() {
		// create first argument values
		List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE });

		// create second argument values
		List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.UNDEFINED });

		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				PathType.REDUCED, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				PathType.REDUCED, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator andOperator = new CounterExampleConjunction(
				PathType.REDUCED, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = andOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = andOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = andOperator
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
		assertTrue(secondHighlightedPositions.get(0).size() == 0);

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
