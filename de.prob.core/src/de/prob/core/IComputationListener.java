/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import de.prob.core.domainobjects.State;

public interface IComputationListener {
	/**
	 * A new state has been computed. i.e. the sucessor states and the status of
	 * the state's invariant are accessible.
	 * 
	 * @param ressource
	 * @param state
	 */
	public void computedState(State state);
}
