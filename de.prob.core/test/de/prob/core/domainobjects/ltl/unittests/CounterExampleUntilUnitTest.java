package de.prob.core.domainobjects.ltl.unittests;


/**
 * Unit test for an "until" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleUntilUnitTest {
	// /*
	// * f-TTTF, g-FFFT, f U g-TTTT
	// */
	// @Test
	// public void testUntilTrueDefinitionOnFinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTTF, g-FFFF, f U g-FFFF
	// */
	// @Test
	// public void testUntilFalseDefinitionOnFinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTFF, g-FFFT, f U g-FFFT
	// */
	// @Test
	// public void testUntilFalseDefinitionOnFinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f U g-FFFF
	// */
	// @Test
	// public void testUntilFalseDefinitionOnFinitePath3() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f - TFFT g - FTFF f U g - TTFF
	// */
	// @Test
	// public void testUntilOnFinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f - TFFT g - FTFF
	// */
	// @Test
	// public void testUntilOnInfinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// "", PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// "", PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator untilOperator = new CounterExampleUntil(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // Loop entry = 1
	// // create first argument
	// firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// untilOperator = new CounterExampleUntil(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // Loop entry = 2
	// // create first argument
	// firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 2,
	// secondArgumentValues);
	//
	// // create an operator
	// untilOperator = new CounterExampleUntil(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	//
	// // Loop entry = 3
	// // create first argument
	// firstArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate("", PathType.INFINITE, 3,
	// secondArgumentValues);
	//
	// // create an operator
	// untilOperator = new CounterExampleUntil(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTTU, g-UUUT, f U g-TTTT
	// */
	// @Test
	// public void testUntilTrueDefinitionOnReducedPath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UUFU, g-FFFT, f U g-FFFT
	// */
	// @Test
	// public void testUntilFalseDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UUUF, g-FFFF, f U g-FFFF
	// */
	// @Test
	// public void testUntilFalseDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UTUU, g-FUTU, f U g-UTTU
	// */
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UUFU, g-FFUU, f U g-UUUU
	// */
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UTUU, g-FUUU, f U g-UUUU
	// */
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath3() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f U g-UUUU
	// */
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath4() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-TTTT, g-UUUU, f U g-UUUU
	// */
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath5() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UTUT, g-FUFU, f U g-UUUU
	// */
	// @Test
	// public void testUntilOnReducedPathUnknownDefinition6() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1, 2, 3 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2, 3 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 3 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UTFU, g-FTFF, f U g-UTFU
	// */
	// @Test
	// public void testUntilOnReducedPathUnknownDefinition7() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// // f-UUUU, g-UUUT, f U g-UUUT
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath8() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// // f-UFUU, g-UFUT, f U g-UFUT
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath9() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// // f-UUFF, g-UUUF, f U g-UUUF
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath10() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// // f-UUUU, g-UUTU, f U g-UUTU
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath11() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// // f-UUFU, g-UUUU, f U g-UUUU
	// @Test
	// public void testUntilUnknownDefinitionOnReducedPath12() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f - UTFU g - FTFF f U g - UTFU
	// */
	// @Test
	// public void testUntilOnReducedPathUnknownDefinition13() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f - UUUU g - UUUU f U g - UUUU
	// */
	// @Test
	// public void testUntilOnReducedPathUnknownDefinition14() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, -1, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator untilOperator = new
	// CounterExampleUntil(
	// PathType.REDUCED, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = untilOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = untilOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = untilOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
}
