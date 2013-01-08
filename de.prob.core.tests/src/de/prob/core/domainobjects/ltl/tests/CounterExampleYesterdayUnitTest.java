package de.prob.core.domainobjects.ltl.tests;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
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
		final LtlTestDescription d = LtlTestDescription.finite(4);
		checkYesterdayFTFT(d);
	}

	/*
	 * f-FTFT, Y f-FFTF
	 */
	@Test
	public void testYesterdayOnInfinitePath() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			checkYesterdayFTFT(d);
		}
	}

	/*
	 * f-TUFT, Y f-FTUF
	 */
	@Test
	public void testYesterdayOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		final CounterExampleProposition arg = d.addArgument("arg", "tuft");
		final CounterExampleUnaryOperator yesterday = new CounterExampleYesterday(
				d.getCounterExample(), arg);
		d.checkValues("yesterday", yesterday, "ftuf");
		d.expectedHighlight(0, "yestH");
		d.expectedHighlight(1, "yestH", 0);
		d.expectedHighlight(2, "yestH", 1);
		d.expectedHighlight(3, "yestH", 2);
		d.checkHighlights("yesterday", yesterday, "yestH");
	}

	private void checkYesterdayFTFT(final LtlTestDescription d) {
		final CounterExampleProposition arg = d.addArgument("arg", "ftft");
		final CounterExampleUnaryOperator yesterday = new CounterExampleYesterday(
				d.getCounterExample(), arg);
		d.checkValues("yesterday", yesterday, "fftf");
		d.expectedHighlight(0, "yestH");
		d.expectedHighlight(1, "yestH", 0);
		d.expectedHighlight(2, "yestH", 1);
		d.expectedHighlight(3, "yestH", 2);
		d.checkHighlights("yesterday", yesterday, "yestH");
	}

}
