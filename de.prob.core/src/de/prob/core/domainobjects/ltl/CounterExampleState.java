package de.prob.core.domainobjects.ltl;

import de.prob.core.domainobjects.Operation;
import de.prob.prolog.term.PrologTerm;

/**
 * Provides a state of a counter-example.
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleState {
	private final int index;
	private final PrologTerm stateId;
	private final Operation operation;

	public CounterExampleState(final int index, final PrologTerm stateId,
			final Operation operation/* , final boolean inLoop */) {
		this.index = index;
		this.stateId = stateId;
		this.operation = operation;
	}

	public PrologTerm getState() {
		return stateId;
	}

	public Operation getOperation() {
		return operation;
	}

	public int getIndex() {
		return index;
	}

	@Override
	public String toString() {
		return "CounterExampleState [index=" + index + ", stateId=" + stateId
				+ ", operation=" + operation + "]";
	}

}
