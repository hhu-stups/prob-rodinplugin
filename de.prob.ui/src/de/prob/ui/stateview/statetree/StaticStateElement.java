package de.prob.ui.stateview.statetree;

import de.prob.core.domainobjects.State;

/**
 * This interface is for objects that describe what information about a state
 * possibly exists. Note that the objects are independent of a concrete state.
 * 
 * @author plagge
 */
public interface StaticStateElement {
	/**
	 * Returns the user-readable label for the property
	 * 
	 * @return the label
	 */
	String getLabel();

	/**
	 * Computes the value for a specific state
	 * 
	 * @param state
	 *            the state, never <code>null</code>
	 * @return a description of the specific property of the state
	 */
	StateDependendElement getValue(State state);

	/**
	 * @param current
	 *            a state, may be <code>null</code>
	 * @param last
	 *            a state, may be <code>null</code>
	 * @return if the property has changed between last and current
	 */
	boolean hasChanged(State current, State last);
}