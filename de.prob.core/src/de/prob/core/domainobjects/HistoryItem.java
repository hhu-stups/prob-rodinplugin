/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.domainobjects;

import org.eclipse.core.runtime.Assert;

/**
 * <p>
 * Contains one entry in the animator history. Immutable representation of an
 * history entry. When an entry has to be changed, it is replaced. This is
 * happening for example when an {@link Operation} is executed in the current
 * {@link State}. Then the last entry of the history is replaced. Thus don't
 * rely on the identity of instances.
 * </p>
 * <p>
 * An entry can consist of a {@link State} and an {@link Operation} if this
 * {@link Operation} was executed while the {@link State} was currently active.
 * </p>
 * <p>
 * Otherwise, if no {@link Operation} has yet been executed from this
 * {@link State}, the {@link Operation} field is <code>null</code>. Be aware to
 * check for <code>!= null</code> before accessing the field
 * <code>operation</code>.
 * </p>
 * 
 */

public final class HistoryItem {
	private final State state;
	private final Operation operation;

	//@requires state != null;
	public HistoryItem(final State state, final Operation operation) {
		//@StartAssert
		Assert.isNotNull(state);
		//@EndAssert

		this.state = state;
		this.operation = operation;
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();

		result.append("(");
		result.append(state.getId());
		result.append(")");

		if (operation != null) {
			result.append(" -");
			result.append(operation.getName());
			result.append("-> (");
			result.append(operation.getDestination());
			result.append(")");
		}

		return result.toString();
	}

	public Operation getOperation() {
		return operation;
	}

	public State getState() {
		return state;
	}

	public String getStateId() {
		return state.getId();
	}

}
