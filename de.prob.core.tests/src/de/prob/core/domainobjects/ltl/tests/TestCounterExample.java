package de.prob.core.domainobjects.ltl.tests;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExample;

public class TestCounterExample extends CounterExample {
	public TestCounterExample(int loopEntry, PathType pathType, int ceSize) {
		super(null, loopEntry, null, pathType, ceSize);
	}

	public static CounterExample finite(int size) {
		return new TestCounterExample(-1, PathType.FINITE, size);
	}

	public static CounterExample reduced(int size) {
		return new TestCounterExample(-1, PathType.REDUCED, size);
	}

	public static CounterExample loop(int entry, int size) {
		return new TestCounterExample(entry, PathType.INFINITE, size);
	}
}