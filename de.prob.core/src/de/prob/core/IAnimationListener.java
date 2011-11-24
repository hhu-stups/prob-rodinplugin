/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

public interface IAnimationListener {

	/**
	 * Event fired, when the current state of an animated machine changes.
	 * 
	 * @param currentState
	 *            The new current {@link State} of the machine.
	 * @param operation
	 *            The {@link Operation} which resulted in the new current state.
	 *            May be <code>{@link Operation#NULL_OPERATION}}</code> if no
	 *            existing {@link Operation} was executed (i.e. when loading a
	 *            machine setting the initial current state, history jumps,
	 *            ...).
	 */
	public void currentStateChanged(State currentState, Operation operation);
}
