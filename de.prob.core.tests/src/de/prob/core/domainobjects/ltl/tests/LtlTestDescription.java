package de.prob.core.domainobjects.ltl.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

import org.junit.Assert;

public class LtlTestDescription {
	private final CounterExample counterExample;
	private final Map<String, ArrayList<int[]>> expectedHighlights = new HashMap<String, ArrayList<int[]>>();

	private LtlTestDescription(int size, PathType pathType, int loopEntry) {
		super();
		this.counterExample = new TestCounterExample(loopEntry, pathType, size);
	}

	public static LtlTestDescription loop(int size, int entry) {
		if (entry < 0 || entry >= size) {
			throw new IllegalArgumentException("unexpected entry point");
		}
		return new LtlTestDescription(size, PathType.INFINITE, entry);
	}

	public static LtlTestDescription finite(int size) {
		return new LtlTestDescription(size, PathType.FINITE, -1);
	}

	public static LtlTestDescription reduced(int size) {
		return new LtlTestDescription(size, PathType.REDUCED, -1);
	}

	public CounterExampleProposition addArgument(final String name,
			final String desc) {
		List<CounterExampleValueType> values = createValues(desc);
		CounterExamplePredicate prop = new CounterExamplePredicate(name,
				counterExample, values);
		return prop;
	}

	private List<CounterExampleValueType> createValues(final String desc) {
		final int length = desc.length();
		final int size = counterExample.getCounterExampleSize();
		if (length != size) {
			throw new IllegalArgumentException("Expected length " + size
					+ " for argument '" + desc + "'");
		}
		List<CounterExampleValueType> values = new ArrayList<CounterExampleValueType>();
		for (int i = 0; i < length; i++) {
			final char c = desc.charAt(i);
			final CounterExampleValueType value;
			switch (c) {
			case 't':
				value = CounterExampleValueType.TRUE;
				break;
			case 'f':
				value = CounterExampleValueType.FALSE;
				break;
			case 'u':
				value = CounterExampleValueType.UNKNOWN;
				break;
			default:
				throw new IllegalArgumentException("unexpected char " + c
						+ "in '" + desc + "'");
			}
			values.add(value);
		}
		return values;
	}

	public CounterExample getCounterExample() {
		return counterExample;
	}

	public void expectedHighlight(final int pos, final String name,
			final int... highlights) {
		if (expectedHighlights.containsKey(name)) {

		} else {
			expectedHighlights.put(name, new ArrayList<int[]>());
		}
		Collection<int[]> highlightList = expectedHighlights.get(name);
		final int expectedPos = highlightList.size();
		if (expectedPos != pos) {
			throw new IllegalArgumentException("Unexpected position (was "
					+ pos + " instead of " + expectedPos
					+ ")for Highlight list '" + name + "'");
		}
		highlightList.add(highlights);
	}

	public void checkValues(String name, CounterExampleProposition prop,
			String expected) {
		final List<CounterExampleValueType> values = prop.getValues();
		List<CounterExampleValueType> expValues = createValues(expected);
		Assert.assertEquals(name + ": values", expValues, values);
	}

	public void checkHighlights(String name, CounterExampleUnaryOperator op,
			String highlights) {
		checkHighlights(name, op.getHighlightedPositions(), highlights);
	}

	public void checkHighlights(String name, CounterExampleBinaryOperator op,
			String highlights1, String highlights2) {
		checkHighlights(name, op.getFirstHighlightedPositions(), highlights1);
		checkHighlights(name, op.getSecondHighlightedPositions(), highlights2);
	}

	private void checkHighlights(String name, List<List<Integer>> actual,
			String highlights) {
		ArrayList<int[]> hl = expectedHighlights.get(highlights);
		if (hl == null) {
			throw new IllegalArgumentException(name + ": highlights '"
					+ highlights + "' not registered");
		}
		int size = counterExample.getCounterExampleSize();
		if (hl.size() != size) {
			throw new IllegalArgumentException(name + ": expected " + size
					+ " highlights but only " + hl.size() + " registered in '"
					+ highlights + "'");
		}
		Assert.assertEquals(name + ": number of highlights", size,
				actual.size());
		for (int i = 0; i < size; i++) {
			Set<Integer> expectedH = new TreeSet<Integer>();
			for (int e : hl.get(i)) {
				expectedH.add(e);
			}
			Set<Integer> actualH = new TreeSet<Integer>(actual.get(i));
			Assert.assertEquals(name + ": highlight pos " + i, expectedH,
					actualH);
		}
	}
}
