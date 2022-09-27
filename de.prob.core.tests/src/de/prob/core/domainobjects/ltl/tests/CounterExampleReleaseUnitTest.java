package de.prob.core.domainobjects.ltl.tests;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleRelease;

/**
 * Unit test for a "release" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleReleaseUnitTest {
	/*
	 * f-FFFT, g-TTTT, f R g-TTTT
	 */
	@Test
	public void testReleaseTrueDefinitionOnFinitePath1() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "ffft");
		final CounterExampleProposition snd = d.addArgument("snd", "tttt");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "tttt");

		d.expectedHighlight(0, "fstH", 3);
		d.expectedHighlight(0, "sndH", 0, 1, 2, 3);

		d.expectedHighlight(1, "fstH", 3);
		d.expectedHighlight(1, "sndH", 1, 2, 3);

		d.expectedHighlight(2, "fstH", 3);
		d.expectedHighlight(2, "sndH", 2, 3);

		d.expectedHighlight(3, "fstH", 3);
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-FFFF, g-TTTT, f R g-TTTT
	 */
	@Test
	public void testReleaseTrueDefinitionOnFinitePath2() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "ffff");
		final CounterExampleProposition snd = d.addArgument("snd", "tttt");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "tttt");

		d.expectedHighlight(0, "fstH");
		d.expectedHighlight(0, "sndH", 0, 1, 2, 3);

		d.expectedHighlight(1, "fstH");
		d.expectedHighlight(1, "sndH", 1, 2, 3);

		d.expectedHighlight(2, "fstH");
		d.expectedHighlight(2, "sndH", 2, 3);

		d.expectedHighlight(3, "fstH");
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-FFFT, g-TTTF, f R g-FFFF
	 */
	@Test
	public void testReleaseFalseDefinitionOnFinitePath1() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "ffft");
		final CounterExampleProposition snd = d.addArgument("snd", "tttf");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "ffff");

		d.expectedHighlight(0, "fstH", 0, 1, 2);
		d.expectedHighlight(0, "sndH", 3);

		d.expectedHighlight(1, "fstH", 1, 2);
		d.expectedHighlight(1, "sndH", 3);

		d.expectedHighlight(2, "fstH", 2);
		d.expectedHighlight(2, "sndH", 3);

		d.expectedHighlight(3, "fstH");
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-FFFF, g-TTTF, f R g-FFFF
	 */
	@Test
	public void testReleaseFalseDefinitionOnFinitePath2() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "ffff");
		final CounterExampleProposition snd = d.addArgument("snd", "tttf");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "ffff");

		d.expectedHighlight(0, "fstH", 0, 1, 2);
		d.expectedHighlight(0, "sndH", 3);

		d.expectedHighlight(1, "fstH", 1, 2);
		d.expectedHighlight(1, "sndH", 3);

		d.expectedHighlight(2, "fstH", 2);
		d.expectedHighlight(2, "sndH", 3);

		d.expectedHighlight(3, "fstH");
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-TFTF, g-TFFT, f R g-TFFT
	 */
	@Test
	public void testReleaseOnFinitePath1() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "tftf");
		final CounterExampleProposition snd = d.addArgument("snd", "tfft");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "tfft");

		d.expectedHighlight(0, "fstH", 0);
		d.expectedHighlight(0, "sndH", 0);

		d.expectedHighlight(1, "fstH");
		d.expectedHighlight(1, "sndH", 1);

		d.expectedHighlight(2, "fstH");
		d.expectedHighlight(2, "sndH", 2);

		d.expectedHighlight(3, "fstH");
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-FTTF, g-TFTT, f R g-FFTT
	 */
	@Test
	public void testReleaseOnFinitePath2() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition fst = d.addArgument("fst", "fttf");
		final CounterExampleProposition snd = d.addArgument("snd", "tftt");
		final CounterExampleBinaryOperator release = new CounterExampleRelease(
				d.getCounterExample(), fst, snd);
		d.checkValues("release", release, "fftt");

		d.expectedHighlight(0, "fstH", 0);
		d.expectedHighlight(0, "sndH", 1);

		d.expectedHighlight(1, "fstH");
		d.expectedHighlight(1, "sndH", 1);

		d.expectedHighlight(2, "fstH", 2);
		d.expectedHighlight(2, "sndH", 2);

		d.expectedHighlight(3, "fstH");
		d.expectedHighlight(3, "sndH", 3);

		d.checkHighlights("release", release, "fstH", "sndH");
	}

	/*
	 * f-FFFT, g-TTTT, f R g-TTTT
	 */
	@Test
	public void testReleaseTrueDefinitionOnInfinitePath1() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			final CounterExampleProposition fst = d.addArgument("fst", "ffft");
			final CounterExampleProposition snd = d.addArgument("snd", "tttt");
			final CounterExampleBinaryOperator release = new CounterExampleRelease(
					d.getCounterExample(), fst, snd);
			d.checkValues("release", release, "tttt");

			d.expectedHighlight(0, "fstH", 3);
			d.expectedHighlight(0, "sndH", 0, 1, 2, 3);

			d.expectedHighlight(1, "fstH", 3);
			d.expectedHighlight(1, "sndH", 1, 2, 3);

			d.expectedHighlight(2, "fstH", 3);
			d.expectedHighlight(2, "sndH", 2, 3);

			d.expectedHighlight(3, "fstH", 3);
			d.expectedHighlight(3, "sndH", 3);

			d.checkHighlights("release", release, "fstH", "sndH");
		}
	}

	/*
	 * f-FFFF, g-TTTT, f R g-TTTT
	 */
	@Test
	public void testReleaseTrueDefinitionOnInfinitePath2() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			final CounterExampleProposition fst = d.addArgument("fst", "ffff");
			final CounterExampleProposition snd = d.addArgument("snd", "tttt");
			final CounterExampleBinaryOperator release = new CounterExampleRelease(
					d.getCounterExample(), fst, snd);
			d.checkValues("release", release, "tttt");

			d.expectedHighlight(0, "fstH");
			d.expectedHighlight(0, "sndH", 0, 1, 2, 3);

			d.expectedHighlight(1, "fstH");
			d.expectedHighlight(1, "sndH", future(1, 4, entry));

			d.expectedHighlight(2, "fstH");
			d.expectedHighlight(2, "sndH", future(2, 4, entry));

			d.expectedHighlight(3, "fstH");
			d.expectedHighlight(3, "sndH", future(3, 4, entry));

			d.checkHighlights("release", release, "fstH", "sndH");
		}
	}

	// /*
	// * f-FFFT, g-TTTF, f R g-FFFF
	// */
	// @Test
	// public void testReleaseFalseDefinitionOnInfinitePath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
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
	// CounterExampleBinaryOperator releaseOperator = new CounterExampleRelease(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// * f-FFFF, g-TTTF, f R g-FFFF
	// */
	// @Test
	// public void testReleaseFalseDefinitionOnInfinitePath2() {
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
	// CounterExampleValueType.FALSE });
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
	// CounterExampleBinaryOperator releaseOperator = new CounterExampleRelease(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// * f-TFTF, g-TFFT, f R g-TFFT
	// */
	// @Test
	// public void testReleaseOnInfinitePath() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.FALSE });
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
	// CounterExampleBinaryOperator releaseOperator = new CounterExampleRelease(
	// PathType.INFINITE, 0, firstArgument, secondArgument);
	//
	// // check result values
	// List<CounterExampleValueType> values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// new Integer[] { 0 }));
	// assertTrue(secondHighlightedPositions.get(3).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(3).toArray(new Integer[0]),
	// new Integer[] { 3, 0 }));
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 1,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 2,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// releaseOperator = new CounterExampleRelease(PathType.INFINITE, 3,
	// firstArgument, secondArgument);
	//
	// // check result values
	// values = releaseOperator.getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// secondHighlightedPositions = releaseOperator
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
	// assertTrue(firstHighlightedPositions.get(2).size() == 0);
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
	// * f-UUUT, g-TTTT, f R g-TTTT
	// */
	// @Test
	// public void testReleaseTrueDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE });
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.TRUE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FFFF, g-TTTT, f R g-UUUU
	// */
	// @Test
	// public void testReleaseTrueDefinitionOnReducedPath2() {
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FFFT, g-TTTF, f R g-FFFF
	// */
	// @Test
	// public void testReleaseFalseDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE });
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.FALSE);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FFFF, g-FTTT, f R g-FUUU
	// */
	// @Test
	// public void testReleaseFalseDefinitionOnReducedPath2() {
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FUTU, g-UTUU, f R g-UTTU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath1() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FUUU, g-UTFU, f R g-UUFU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath2() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
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
	// * f-FUUU, g-UTUU, f R g-UUUU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath3() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FFFF, g-TTTT, f R g-UUUU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath4() {
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FFFF, g-UUUU, f R g-UUUU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath5() {
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-UUUU, g-UUUU, f R g-UUUU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath6() {
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-UUTF, g-FTTT, f R g-FTTU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath7() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FUTU, g-TUTU, f R g-UUTU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath8() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// assertTrue(secondHighlightedPositions.get(0).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1 }));
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
	// * f-FFTU, g-TUTU, f R g-UUTU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath9() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// * f-FUUU, g-UTFU, f R g-UUFU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath10() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.TRUE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// assertTrue(secondHighlightedPositions.get(1).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(1).toArray(new Integer[0]),
	// new Integer[] { 1, 2 }));
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
	// * f-FUUU, g-UUFU, f R g-UUFU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath11() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.UNKNOWN });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.UNKNOWN,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(1) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(2) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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
	// assertTrue(secondHighlightedPositions.get(0).size() == 2);
	// assertTrue(Arrays.equals(
	// secondHighlightedPositions.get(0).toArray(new Integer[0]),
	// new Integer[] { 0, 1 }));
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
	// /*
	// * f-FTUF, g-FTUU, f R g-FTUU
	// */
	// @Test
	// public void testReleaseUnknownDefinitionOnReducedPath12() {
	// // create first argument values
	// final List<CounterExampleValueType> firstArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
	// CounterExampleValueType.TRUE,
	// CounterExampleValueType.UNKNOWN,
	// CounterExampleValueType.FALSE });
	//
	// // create second argument values
	// final List<CounterExampleValueType> secondArgumentValues = Arrays
	// .asList(new CounterExampleValueType[] {
	// CounterExampleValueType.FALSE,
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
	// final CounterExampleBinaryOperator releaseOperator = new
	// CounterExampleRelease(
	// PathType.REDUCED, firstArgument, secondArgument);
	//
	// // check result values
	// final List<CounterExampleValueType> values = releaseOperator
	// .getValues();
	// assertTrue(values.size() == firstArgumentValues.size());
	// assertTrue(values.size() == secondArgumentValues.size());
	// assertTrue(values.get(0) == CounterExampleValueType.FALSE);
	// assertTrue(values.get(1) == CounterExampleValueType.TRUE);
	// assertTrue(values.get(2) == CounterExampleValueType.UNKNOWN);
	// assertTrue(values.get(3) == CounterExampleValueType.UNKNOWN);
	//
	// // check highlighted positions
	// final List<List<Integer>> firstHighlightedPositions = releaseOperator
	// .getFirstHighlightedPositions();
	// final List<List<Integer>> secondHighlightedPositions = releaseOperator
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

	private static int[] future(int current, int size, int entry) {
		final int first = current <= entry ? current : entry;
		final int resSize = size - first;
		int[] res = new int[resSize];
		for (int i = 0; i < resSize; i++) {
			res[i] = first + i;
		}
		return res;
	}

}
