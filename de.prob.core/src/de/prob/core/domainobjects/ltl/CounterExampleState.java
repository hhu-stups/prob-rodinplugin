package de.prob.core.domainobjects.ltl;

import de.prob.core.domainobjects.Operation;

/**
 * Provides a state of a counter-example.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleState {
	private final int index;
	private final int stateId;
	private final Operation operation;
	private final boolean inLoop;

	public CounterExampleState(final int index, final int stateId,
			final Operation operation, final boolean inLoop) {
		this.index = index;
		this.stateId = stateId;
		this.operation = operation;
		this.inLoop = inLoop;
	}

	public CounterExampleState(final int index, final int stateId,
			final boolean inLoop) {
		this(index, stateId, null, inLoop);
	}

	public int getState() {
		return stateId;
	}

	public Operation getOperation() {
		return operation;
	}

	public int getIndex() {
		return index;
	}

	public boolean isInLoop() {
		return inLoop;
	}
}
