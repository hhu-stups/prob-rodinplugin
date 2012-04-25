package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;
import de.prob.core.domainobjects.ltl.CounterExampleYesterday;

/**
 * Unit test for a "yesterday" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleYesterdayUnitTest {

	/*
	 * f-FTFT, Y f-FFTF
	 */
	@Test
	public void testYesterdayOnFinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				PathType.FINITE, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator yesterdayOperator = new CounterExampleYesterday(
				PathType.FINITE, argument);

		// check result values
		final List<CounterExampleValueType> values = yesterdayOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = yesterdayOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));
	}

	/*
	 * f-FTFT, Y f-FFTF
	 */
	@Test
	public void testYesterdayOnInfinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// Loop entry = 0
		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate(
				PathType.INFINITE, 0, argumentValues);

		// create an operator
		CounterExampleUnaryOperator yesterdayOperator = new CounterExampleYesterday(
				PathType.INFINITE, 0, argument);

		// check result values
		List<CounterExampleValueType> values = yesterdayOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> highlightedPositions = yesterdayOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// Loop entry = 1
		// create an argument
		argument = new CounterExamplePredicate(PathType.INFINITE, 1,
				argumentValues);

		// create an operator
		yesterdayOperator = new CounterExampleYesterday(PathType.INFINITE, 1,
				argument);

		// check result values
		values = yesterdayOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = yesterdayOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// Loop entry = 2
		// create an argument
		argument = new CounterExamplePredicate(PathType.INFINITE, 2,
				argumentValues);

		// create an operator
		yesterdayOperator = new CounterExampleYesterday(PathType.INFINITE, 2,
				argument);

		// check result values
		values = yesterdayOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = yesterdayOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// Loop entry = 3
		// create an argument
		argument = new CounterExamplePredicate(PathType.INFINITE, 3,
				argumentValues);

		// create an operator
		yesterdayOperator = new CounterExampleYesterday(PathType.INFINITE, 3,
				argument);

		// check result values
		values = yesterdayOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.FALSE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = yesterdayOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));
	}

	/*
	 * f-TUFT, Y f-FTUF
	 */
	@Test
	public void testYesterdayOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				PathType.REDUCED, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator yesterdayOperator = new CounterExampleYesterday(
				PathType.REDUCED, argument);

		// check result values
		final List<CounterExampleValueType> values = yesterdayOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = yesterdayOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		assertTrue(highlightedPositions.get(0).size() == 0);

		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 1 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 2 }));

	}
}
