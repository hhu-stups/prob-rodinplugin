package de.prob.core.domainobjects.ltl.unittests;


/**
 * Unit test for a "trigger" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleTriggerUnitTest {
	// /*
	// * f-TFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnFinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnFinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-TFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnFinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnFinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FTFT, g-TFFT, f T g-TFFT
	// */
	// @Test
	// public void testTriggerOnFinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FTTF, g-TTFT, f T g-TTFF
	// */
	// @Test
	// public void testTriggerOnFinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create first argument
	// final CounterExampleProposition firstArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, firstArgumentValues);
	//
	// // create second argument
	// final CounterExampleProposition secondArgument = new
	// CounterExamplePredicate(
	// PathType.FINITE, secondArgumentValues);
	//
	// // create an operator
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.FINITE, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 2 }));
	// }
	//
	// /*
	// * f-TFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnInfinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnInfinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// * f-TFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnInfinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // Loop entry = 1
	// // create first argument
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // Loop entry = 2
	// // create first argument
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// }
	//
	// /*
	// * f-FFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnInfinitePath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // Loop entry = 1
	// // create first argument
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	//
	// // Loop entry = 2
	// // create first argument
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 2,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// new Integer[] { 3, 2, 1, }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 0 }));
	// }
	//
	// /*
	// * f-FTFT, g-TFFT, f T g-TFFT
	// */
	// @Test
	// public void testTriggerOnInfinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // Loop entry = 0
	// // create first argument
	// CounterExampleProposition firstArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, firstArgumentValues);
	//
	// // create second argument
	// CounterExampleProposition secondArgument = new CounterExamplePredicate(
	// PathType.INFINITE, 0, secondArgumentValues);
	//
	// // create an operator
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	//
	// // Loop entry = 1
	// // create first argument
	// firstArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// firstArgumentValues);
	//
	// // create second argument
	// secondArgument = new CounterExamplePredicate(PathType.INFINITE, 1,
	// secondArgumentValues);
	//
	// // create an operator
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// triggerOperator = new CounterExampleTrigger(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = triggerOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = triggerOperator
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
	// * f-TFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerTrueDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-TFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-FTTT, f T g-FFFF
	// */
	// @Test
	// public void testTriggerFalseDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-UTUF, g-UUTU, f T g-UTTU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE });
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
	// * f-UUUF, g-UFTU, f T g-UFFU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath2() {
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
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
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
	// * f-UUUF, g-UUTU, f T g-UUUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath3() {
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
	// * f-FFFF, g-TTTT, f T g-TTTT
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath4() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-FFFF, g-UUUU, f T g-UUUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath5() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
	// /*
	// * f-UUUU, g-UUUU, f T g-UUUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath6() {
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
	// * f-FTUU, g-TTTF, f T g-TTTF
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath7() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// assertTrue(firstHighlightedPositions.get(3).size() == 0);
	// assertTrue(secondHighlightedPositions.get(3).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3 }));
	// }
	//
	// /*
	// * f-UTUF, g-UTUT, f T g-UTUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath8() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// assertTrue(firstHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// }
	//
	// /*
	// * f-UTFF, g-UTUT, f T g-UTUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath9() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// * f-UUUF, g-UFTU, f T g-UFUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath10() {
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
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// assertTrue(secondHighlightedPositions.get(2).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(2).toArray(new Integer[0]),
	// new Integer[] { 2, 1 }));
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
	// * f-UUUF, g-UFUU, f T g-UFUU
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath11() {
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
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator triggerOperator = new
	// CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
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
	// assertTrue(firstHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// firstHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 2 }));
	// }
	//
	// /*
	// * f-FUTF, g-UUTF, f T g-UUTF
	// */
	// @Test
	// public void testTriggerUnknownDefinitionOnReducedPath12() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
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
	// CounterExampleBinaryOperator triggerOperator = new CounterExampleTrigger(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = triggerOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = triggerOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = triggerOperator
	// .getSecondHighlightedPositions();
	// assertTrue(firstHighlightedPositions.size() == firstArgumentValues
	// .size());
	// assertTrue(secondHighlightedPositions.size() == secondArgumentValues
	// .size());
	//
	// // State 0
	// assertTrue(firstHighlightedPositions.get(0).size() == 1);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
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
}
