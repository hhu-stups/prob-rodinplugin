package de.prob.core.domainobjects.ltl.tests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleFinally;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for a "finally" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleFinallyUnitTest {
	@Test
	public void testFinallyOnFinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		final CounterExample ce = TestCounterExample.finite(4);
		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", ce, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator finallyOperator = new CounterExampleFinally(
				ce, argument);

		// check result values
		final List<CounterExampleValueType> values = finallyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = finallyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	@Test
	public void testFinallyOnInfinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// Loop entry = 0
		final CounterExample ce0 = TestCounterExample.loop(0, 4);
		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				ce0, argumentValues);

		// create an operator
		CounterExampleUnaryOperator finallyOperator = new CounterExampleFinally(
				ce0, argument);

		// check result values
		List<CounterExampleValueType> values = finallyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> highlightedPositions = finallyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// Loop entry = 1
		final CounterExample ce1 = TestCounterExample.loop(1, 4);
		// create an argument
		argument = new CounterExamplePredicate("", ce1, argumentValues);

		// create an operator
		finallyOperator = new CounterExampleFinally(ce1, argument);

		// check result values
		values = finallyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		highlightedPositions = finallyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// Loop entry = 2
		final CounterExample ce2 = TestCounterExample.loop(2, 4);
		// create an argument
		argument = new CounterExamplePredicate("", ce2, argumentValues);

		// create an operator
		finallyOperator = new CounterExampleFinally(ce2, argument);

		// check result values
		values = finallyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = finallyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

		assertTrue(highlightedPositions.get(3).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2 }));

		// Loop entry = 3
		final CounterExample ce3 = TestCounterExample.loop(3, 4);
		// create an argument
		argument = new CounterExamplePredicate("", ce3, argumentValues);

		// create an operator
		finallyOperator = new CounterExampleFinally(ce3, argument);

		// check result values
		values = finallyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = finallyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	@Test
	public void testFinallyOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		final CounterExample ce = TestCounterExample.reduced(4);
		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", ce, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator finallyOperator = new CounterExampleFinally(
				ce, argument);

		// check result values
		final List<CounterExampleValueType> values = finallyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = finallyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 2, 3 }));

		assertTrue(highlightedPositions.get(2).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 3 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}
}
