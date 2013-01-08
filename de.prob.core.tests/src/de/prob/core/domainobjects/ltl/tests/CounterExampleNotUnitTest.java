package de.prob.core.domainobjects.ltl.tests;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExampleNegation;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

/**
 * Unit test for a "not" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleNotUnitTest {

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnFinitePath() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		checkNot(d);
	}

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnInFinitePath() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			checkNot(d);
		}
	}

	/*
	 * f-TFFT, Not f-FTTF
	 */
	@Test
	public void testNotOnReducedPath1() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		checkNot(d);
	}

	/*
	 * f-TUFU, Not f-FUTU
	 */
	@Test
	public void testNotOnReducedPath2() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		final CounterExampleProposition arg = d.addArgument("not", "tufu");
		final CounterExampleUnaryOperator not = new CounterExampleNegation(
				d.getCounterExample(), arg);
		d.checkValues("not", not, "futu");
		d.expectedHighlight(0, "notH", 0);
		d.expectedHighlight(1, "notH", 1);
		d.expectedHighlight(2, "notH", 2);
		d.expectedHighlight(3, "notH", 3);
		d.checkHighlights("not", not, "notH");
	}

	private void checkNot(final LtlTestDescription d) {
		final CounterExampleProposition arg = d.addArgument("not", "tfft");
		final CounterExampleUnaryOperator not = new CounterExampleNegation(
				d.getCounterExample(), arg);
		d.checkValues("not", not, "fttf");
		d.expectedHighlight(0, "notH", 0);
		d.expectedHighlight(1, "notH", 1);
		d.expectedHighlight(2, "notH", 2);
		d.expectedHighlight(3, "notH", 3);
		d.checkHighlights("not", not, "notH");
	}
}
