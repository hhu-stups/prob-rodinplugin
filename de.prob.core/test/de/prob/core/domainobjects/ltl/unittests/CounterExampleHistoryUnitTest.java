package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleHistory;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for a "history" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleHistoryUnitTest {
	/*
	 * f-TTTT, O f-TTTT
	 */
	@Test
	public void testHistoryTrueDefinitionOnFinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.FINITE, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));
	}

	/*
	 * f-TTFF, O f-TTFF
	 */
	@Test
	public void testHistoryFalseDefinitionOnFinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.FINITE, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.FINITE, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TTTT, O f-TTTT
	 */
	@Test
	public void testHistoryTrueDefinitionOnInfinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// Loop entry = 0
		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.INFINITE, 0, argumentValues);

		// create an operator
		CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.INFINITE, 0, argument);

		// check result values
		List<CounterExampleValueType> values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));

		// Loop entry = 1
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 1,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));

		// Loop entry = 2
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 2,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));

		// Loop entry = 3
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 3,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));
	}

	/*
	 * f-TTFF, O f-TTFF
	 */
	@Test
	public void testHistoryFalseDefinitionOnInFinitePath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// Loop entry = 0
		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.INFINITE, 0, argumentValues);

		// create an operator
		CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.INFINITE, 0, argument);

		// check result values
		List<CounterExampleValueType> values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 1
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 1,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 2
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 2,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 3
		// create an argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				argumentValues);

		// create an operator
		historyOperator = new CounterExampleHistory(PathType.INFINITE, 3,
				argument);

		// check result values
		values = historyOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = historyOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TTTT, O f-TTTT
	 */
	@Test
	public void testHistoryTrueDefinitionOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.REDUCED, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.TRUE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));
	}

	/*
	 * f-TTFF, O f-TTFF
	 */
	@Test
	public void testHistoryFalseDefinitionOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.REDUCED, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.FALSE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-UTUT, O f-UUUU
	 */
	@Test
	public void testHistoryUnknownDefinitionOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.REDUCED, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 4);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3, 2, 1, 0 }));
	}

	/*
	 * f-TUTF, O f-TUUF
	 */
	@Test
	public void testHistoryOnReducedPath() {
		// create argument values
		final List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNKNOWN,
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE });

		// create an argument
		final CounterExampleProposition argument = new CounterExamplePredicate(
				"", PathType.REDUCED, -1, argumentValues);

		// create an operator
		final CounterExampleUnaryOperator historyOperator = new CounterExampleHistory(
				PathType.REDUCED, -1, argument);

		// check result values
		final List<CounterExampleValueType> values = historyOperator
				.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.TRUE);
		assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		final List<List<Integer>> highlightedPositions = historyOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		assertTrue(highlightedPositions.get(1).size() == 2);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1, 0 }));

		assertTrue(highlightedPositions.get(2).size() == 3);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2, 1, 0 }));

		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}
}
