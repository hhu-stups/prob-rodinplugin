package de.prob.core.domainobjects.ltl.tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleImplication;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for an "imply" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleImplyUnitTest {
	/*
	 * f-TTFF, g-TFTF, f Imply g-TFTT
	 */
	@Test
	public void testImplyOnFinitePath() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		final CounterExample ce = TestCounterExample.finite(4);
		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", ce, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", ce, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator implyOperator = new CounterExampleImplication(
				ce, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-TTFF, g-TFTF, f Imply g-TFTT
	 */
	@Test
	public void testImplyOnInfinitePath() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// Loop entry = 0
		final CounterExample ce0 = TestCounterExample.loop(0, 4);
		// create first argument
		CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", ce0, firstArgumentValues);

		// create second argument
		CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", ce0, secondArgumentValues);

		// create an operator
		CounterExampleBinaryOperator implyOperator = new CounterExampleImplication(
				ce0, firstArgument, secondArgument);

		// check result values
		List<CounterExampleValueType> values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		List<List<Integer>> secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 1
		final CounterExample ce1 = TestCounterExample.loop(1, 4);
		// create first argument
		firstArgument = new CounterExamplePredicate("", ce1,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", ce1,
				secondArgumentValues);

		// create an operator
		implyOperator = new CounterExampleImplication(ce1, firstArgument,
				secondArgument);

		// check result values
		values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 2
		final CounterExample ce2 = TestCounterExample.loop(2, 4);
		// create first argument
		firstArgument = new CounterExamplePredicate("", ce2,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", ce2,
				secondArgumentValues);

		// create an operator
		implyOperator = new CounterExampleImplication(ce2, firstArgument,
				secondArgument);

		// check result values
		values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);

		// Loop entry = 3
		final CounterExample ce3 = TestCounterExample.loop(3, 4);
		// create first argument
		firstArgument = new CounterExamplePredicate("", ce3,
				firstArgumentValues);

		// create second argument
		secondArgument = new CounterExamplePredicate("", ce3,
				secondArgumentValues);

		// create an operator
		implyOperator = new CounterExampleImplication(ce3, firstArgument,
				secondArgument);

		// check result values
		values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-TTFF, g-TFTF, f Imply g-TFTT
	 */
	@Test
	public void testImplyOnReducedPath1() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		final CounterExample ce = TestCounterExample.reduced(4);
		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", ce, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", ce, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator implyOperator = new CounterExampleImplication(
				ce, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = implyOperator
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
				new Integer[] { 2 }));
		assertTrue(secondHighlightedPositions.get(2).size() == 0);

		// State 3
		assertTrue(firstHighlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				firstHighlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}

	/*
	 * f-UUTU, g-UTUF, f Imply g-UTUU
	 */
	@Test
	public void testImplyOnReducedPath2() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.FALSE });

		final CounterExample ce = TestCounterExample.reduced(4);

		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", ce, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", ce, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator implyOperator = new CounterExampleImplication(
				ce, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = implyOperator
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

	/*
	 * f-UUTF, g-UTUU, f Imply g-UTUT
	 */
	@Test
	public void testImplyOnReducedPath3() {
		// create first argument values
		final List<CounterExampleValueType> firstArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create second argument values
		final List<CounterExampleValueType> secondArgumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.UNKNOWN });

		final CounterExample ce = TestCounterExample.reduced(4);

		// create first argument
		final CounterExampleProposition firstArgument = new CounterExamplePredicate(
				"", ce, firstArgumentValues);

		// create second argument
		final CounterExampleProposition secondArgument = new CounterExamplePredicate(
				"", ce, secondArgumentValues);

		// create an operator
		final CounterExampleBinaryOperator implyOperator = new CounterExampleImplication(
				ce, firstArgument, secondArgument);

		// check result values
		final List<CounterExampleValueType> values = implyOperator.getValues();
		assertTrue(values.size() == firstArgumentValues.size());
		assertTrue(values.size() == secondArgumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		final List<List<Integer>> firstHighlightedPositions = implyOperator
				.getFirstHighlightedPositions();
		final List<List<Integer>> secondHighlightedPositions = implyOperator
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
		assertTrue(secondHighlightedPositions.get(3).size() == 0);
	}
}
