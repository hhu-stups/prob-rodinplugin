package de.prob.core.domainobjects.ltl.unittests;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleNegation;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * Unit test for a "Not" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public class CounterExampleNotUnitTest {

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnFinitePath() {
		// create argument values
		List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.FINITE, argumentValues);

		// create an operator
		CounterExampleUnaryOperator notOperator = new CounterExampleNegation(
				PathType.FINITE, argument);

		// check result values
		List<CounterExampleValueType> values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		// State 0
		List<List<Integer>> highlightedPositions = notOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnInFinitePath() {
		// create argument values
		List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// Loop entry = 0
		// create argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.INFINITE, 0, argumentValues);

		// create an operator
		CounterExampleUnaryOperator notOperator = new CounterExampleNegation(
				PathType.INFINITE, 0, argument);

		// check result values
		List<CounterExampleValueType> values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		List<List<Integer>> highlightedPositions = notOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		// State 0
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 1
		// create argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 1,
				argumentValues);

		// create an operator
		notOperator = new CounterExampleNegation(PathType.INFINITE, 1, argument);

		// check result values
		values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = notOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		// State 0
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 2
		// create argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 2,
				argumentValues);

		// create an operator
		notOperator = new CounterExampleNegation(PathType.INFINITE, 2, argument);

		// check result values
		values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = notOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		// State 0
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));

		// Loop entry = 3
		// create argument
		argument = new CounterExamplePredicate("", PathType.INFINITE, 3,
				argumentValues);

		// create an operator
		notOperator = new CounterExampleNegation(PathType.INFINITE, 3, argument);

		// check result values
		values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		highlightedPositions = notOperator.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());

		// State 0
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnReducedPath1() {
		// create argument values
		List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.TRUE });

		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.REDUCED, argumentValues);

		// create an operator
		CounterExampleUnaryOperator notOperator = new CounterExampleNegation(
				PathType.REDUCED, argument);

		// check result values
		List<CounterExampleValueType> values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.TRUE);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.FALSE);

		// check highlighted positions
		// State 0
		List<List<Integer>> highlightedPositions = notOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}

	/*
	 * f-TUFU, Not f-FUTU
	 */
	@Test
	public void testNotOnReducedPath2() {
		// create argument values
		List<CounterExampleValueType> argumentValues = Arrays
				.asList(new CounterExampleValueType[] {
						CounterExampleValueType.TRUE,
						CounterExampleValueType.UNDEFINED,
						CounterExampleValueType.FALSE,
						CounterExampleValueType.UNDEFINED });

		// create an argument
		CounterExampleProposition argument = new CounterExamplePredicate("",
				PathType.REDUCED, argumentValues);

		// create an operator
		CounterExampleUnaryOperator notOperator = new CounterExampleNegation(
				PathType.REDUCED, argument);

		// check result values
		List<CounterExampleValueType> values = notOperator.getValues();
		assertTrue(values.size() == argumentValues.size());
		assertTrue(values.get(0) == CounterExampleValueType.FALSE);
		assertTrue(values.get(1) == CounterExampleValueType.UNDEFINED);
		assertTrue(values.get(2) == CounterExampleValueType.TRUE);
		assertTrue(values.get(3) == CounterExampleValueType.UNDEFINED);

		// check highlighted positions
		// State 0
		List<List<Integer>> highlightedPositions = notOperator
				.getHighlightedPositions();
		assertTrue(highlightedPositions.size() == argumentValues.size());
		assertTrue(highlightedPositions.get(0).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(0).toArray(new Integer[0]),
				new Integer[] { 0 }));

		// State 1
		assertTrue(highlightedPositions.get(1).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(1).toArray(new Integer[0]),
				new Integer[] { 1 }));

		// State 2
		assertTrue(highlightedPositions.get(2).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(2).toArray(new Integer[0]),
				new Integer[] { 2 }));

		// State 3
		assertTrue(highlightedPositions.get(3).size() == 1);
		assertTrue(Arrays.equals(
				highlightedPositions.get(3).toArray(new Integer[0]),
				new Integer[] { 3 }));
	}
}
