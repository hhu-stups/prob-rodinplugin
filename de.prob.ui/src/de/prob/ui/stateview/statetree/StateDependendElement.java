/**
 * 
 */
package de.prob.ui.stateview.statetree;

import de.prob.core.domainobjects.State;

/**
 * This interface describes objects that can be displayed in a view that depends
 * somehow on states, namely the state view and the history view.
 * 
 * @author plagge
 */
public class StateDependendElement {
	private final State state;
	private final String value;
	private final EStateTreeElementProperty property;

	public StateDependendElement(final State state, final String value,
			final EStateTreeElementProperty property) {
		this.state = state;
		this.value = value;
		this.property = property;
	}

	public State getState() {
		return state;
	}

	public String getValue() {
		return value;
	}

	public EStateTreeElementProperty getProperty() {
		return property;
	}
}
