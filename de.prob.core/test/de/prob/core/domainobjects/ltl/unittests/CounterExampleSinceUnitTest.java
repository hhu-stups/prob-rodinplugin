package de.prob.core.domainobjects.ltl.unittests;


/**
 * Unit test for a "since" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleSinceUnitTest {
	// /*
	// * f-FTTT, g-TFFF, f S g-TTTT
	// */
	// @Test
	// public void testSinceTrueDefinitionOnFinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// }
	//
	// /*
	// * f-FFTT, g-TFFF, f S g-TFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnFinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-FTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnFinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// "", PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnFinitePath3() {
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
	// "", PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.FINITE, -1, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f - TFFT g - FTFF f S g - FTFF
	// */
	// @Test
	// public void testSinceOnFinitePath() {
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
	// "", PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
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
	// }
	//
	// /*
	// * f-FTTT, g-TFFF, f S g-TTTT
	// */
	// @Test
	// public void testSinceTrueDefinitionOnInfinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
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
	// CounterExampleBinaryOperator sinceOperator = new CounterExampleSince(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// }
	//
	// /*
	// * f-FFTT, g-TFFF, f S g-TFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnInfinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
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
	// CounterExampleBinaryOperator sinceOperator = new CounterExampleSince(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-FTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnInfinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// CounterExampleBinaryOperator sinceOperator = new CounterExampleSince(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnInfinitePath3() {
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
	// CounterExampleBinaryOperator sinceOperator = new CounterExampleSince(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
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
	// sinceOperator = new CounterExampleSince(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-FTTT, g-TFFF, f S g-TTTT
	// */
	// @Test
	// public void testSinceTrueDefinitionOnReducedPath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// }
	//
	// /*
	// * f-FFTT, g-TFFF, f S g-TFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-FTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// "", PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceFalseDefinitionOnReducedPath3() {
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
	// "", PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// "", PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-UUTU, g-UTUF, f S g-UTTU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// new Integer[] { 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-UFUU, g-UUFF, f S g-UUUU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath2() {
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
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-UUTU, g-UUUF, f S g-UUUU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath3() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(firstHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1 }));
	// }
	//
	// /*
	// * f-TTTT, g-FFFF, f S g-FFFF
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath4() {
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
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
	// assertTrue(secondHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // State 1
	// assertTrue(firstHighlightedPositions.get(1).size() == 0);
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// /*
	// * f-TTTT, g-UUUU, f S g-UUUU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath5() {
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
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// // f-TUTU, g-UFUF, f S g-UUUU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath6() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(firstHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 0 }));
	//
	// // State 2
	// assertTrue(firstHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(2).size() == 3);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1, 0 }));
	//
	// // State 3
	// assertTrue(firstHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 4);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2, 1, 0 }));
	// }
	//
	// // f-UFTU, g-FFTF, f S g-UFTU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath7() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// }
	//
	// // f-UUUU, g-TUUU, f S g-TUUU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath8() {
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
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// // f-UUFU, g-TUFU, f S g- TUFU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath9() {
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
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 0);
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
	// // f-FFUU, g-FUUU, f S g-FUUU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath10() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
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
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// // f-UUUU, g-UTUU, f S g-UTUU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath11() {
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
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// // f-UFUU, g-UUUU, f S g-UUUU
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath12() {
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// * f-UFTU, g-FFTF, f S g-UFTU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath13() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// }
	//
	// /*
	// * f-UUUU, g-UUUU, f S g-UUUU
	// */
	// @Test
	// public void testSinceUnknownDefinitionOnReducedPath14() {
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
	// PathType.REDUCED, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.REDUCED, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator sinceOperator = new
	// CounterExampleSince(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = sinceOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = sinceOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = sinceOperator
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
