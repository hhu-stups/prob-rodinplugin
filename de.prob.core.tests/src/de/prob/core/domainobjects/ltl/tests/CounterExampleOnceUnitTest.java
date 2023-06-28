package de.prob.core.domainobjects.ltl.tests;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExampleOnce;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

/**
 * Unit test for an "once" operator.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleOnceUnitTest {
	/*
	 * f-FTFT, O f-FTTT
	 */
	@Test
	public void testOnceTrueDefinitionOnFinitePath() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		checkOnce(d);
	}

	/*
	 * f-FFFF, O f-FFFF
	 */
	@Test
	public void testOnceFalseDefinitionOnFinitePath() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		checkOnceCompleteFalse(d);
	}

	/*
	 * f-FTFT, O f-FTTT
	 */
	@Test
	public void testOnceTrueDefinitionOnInfinitePath() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			checkOnce(d);
		}
	}

	/*
	 * f-FFFF, O f-FFFF
	 */
	@Test
	public void testOnceFalseDefinitionOnInfinitePath() {
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			checkOnceCompleteFalse(d);
		}
	}

	/*
	 * f-FTFT, O f-FTTT
	 */
	@Test
	public void testOnceTrueDefinitionOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		checkOnce(d);
	}

	/*
	 * f-FFFF, O f-FFFF
	 */
	@Test
	public void testOnceFalseDefinitionOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		checkOnceCompleteFalse(d);
	}

	/*
	 * f-UFUF, O f-UUUU
	 */
	@Test
	public void testOnceUnknownDefinitionOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		final CounterExampleProposition arg = d.addArgument("arg", "ufuf");
		final CounterExampleUnaryOperator once = new CounterExampleOnce(
				d.getCounterExample(), arg);
		d.checkValues("once", once, "uuuu");
		d.expectedHighlight(0, "onceH", 0);
		d.expectedHighlight(1, "onceH", 0, 1);
		d.expectedHighlight(2, "onceH", 0, 1, 2);
		d.expectedHighlight(3, "onceH", 0, 1, 2, 3);
		d.checkHighlights("once", once, "onceH");
	}

	/*
	 * f-FUTU, O f-FUTT
	 */
	@Test
	public void testOnceOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		final CounterExampleProposition arg = d.addArgument("arg", "futu");
		final CounterExampleUnaryOperator once = new CounterExampleOnce(
				d.getCounterExample(), arg);
		d.checkValues("once", once, "futt");
		d.expectedHighlight(0, "onceH", 0);
		d.expectedHighlight(1, "onceH", 0, 1);
		d.expectedHighlight(2, "onceH", 2);
		d.expectedHighlight(3, "onceH", 2);
		d.checkHighlights("once", once, "onceH");
	}

	private void checkOnce(final LtlTestDescription d) {
		final CounterExampleProposition arg = d.addArgument("arg", "ftft");
		final CounterExampleUnaryOperator once = new CounterExampleOnce(
				d.getCounterExample(), arg);
		d.checkValues("once", once, "fttt");
		d.expectedHighlight(0, "onceH", 0);
		d.expectedHighlight(1, "onceH", 1);
		d.expectedHighlight(2, "onceH", 1);
		d.expectedHighlight(3, "onceH", 3);
		d.checkHighlights("once", once, "onceH");
	}

	private void checkOnceCompleteFalse(final LtlTestDescription d) {
		final CounterExampleProposition arg = d.addArgument("arg", "ffff");
		final CounterExampleUnaryOperator once = new CounterExampleOnce(
				d.getCounterExample(), arg);
		d.checkValues("once", once, "ffff");
		d.expectedHighlight(0, "onceH", 0);
		d.expectedHighlight(1, "onceH", 0, 1);
		d.expectedHighlight(2, "onceH", 0, 1, 2);
		d.expectedHighlight(3, "onceH", 0, 1, 2, 3);
		d.checkHighlights("once", once, "onceH");
	}
}
