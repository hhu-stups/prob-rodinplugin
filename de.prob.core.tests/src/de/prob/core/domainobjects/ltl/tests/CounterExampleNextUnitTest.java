package de.prob.core.domainobjects.ltl.tests;

import org.junit.Test;

import de.prob.core.domainobjects.ltl.CounterExampleNext;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

/**
 * Unit test for a "next" operator.
 * 
 * @author Andriy Tolstoy
 */
public final class CounterExampleNextUnitTest {
	@Test
	public void testNextOnFinitePath() {
		final LtlTestDescription d = LtlTestDescription.finite(4);
		final CounterExampleProposition arg = d.addArgument("arg", "ftft");
		final CounterExampleUnaryOperator next = new CounterExampleNext(
				d.getCounterExample(), arg);

		d.checkValues("next", next, "tftf");

		d.expectedHighlight(0, "nextH", 1);
		d.expectedHighlight(1, "nextH", 2);
		d.expectedHighlight(2, "nextH", 3);
		d.expectedHighlight(3, "nextH");
		d.checkHighlights("next", next, "nextH");
	}

	@Test
	public void testNextOnInfinitePath() {
		final String[] values = new String[] { "tftf", "tftt", "tftf", "tftt" };
		for (int entry = 0; entry < 4; entry++) {
			final LtlTestDescription d = LtlTestDescription.loop(4, entry);
			final CounterExampleProposition arg = d.addArgument("arg", "ftft");
			final CounterExampleUnaryOperator next = new CounterExampleNext(
					d.getCounterExample(), arg);

			d.checkValues("next", next, values[entry]);
			d.expectedHighlight(0, "nextH", 1);
			d.expectedHighlight(1, "nextH", 2);
			d.expectedHighlight(2, "nextH", 3);
			d.expectedHighlight(3, "nextH", entry);
			d.checkHighlights("next", next, "nextH");
		}
	}

	@Test
	public void testNextOnReducedPath() {
		final LtlTestDescription d = LtlTestDescription.reduced(4);
		final CounterExampleProposition arg = d.addArgument("arg", "tuft");
		final CounterExampleUnaryOperator next = new CounterExampleNext(
				d.getCounterExample(), arg);
		d.checkValues("next", next, "uftu");

		d.expectedHighlight(0, "nextH", 1);
		d.expectedHighlight(1, "nextH", 2);
		d.expectedHighlight(2, "nextH", 3);
		d.expectedHighlight(3, "nextH");
		d.checkHighlights("next", next, "nextH");
	}
}
