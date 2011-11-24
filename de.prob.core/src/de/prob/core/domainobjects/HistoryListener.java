/**
 * 
 */
package de.prob.core.domainobjects;

/**
 * This listener interface is used to inform clients about states that have been
 * added to the history or have left the history.
 * 
 * @author plagge
 */
public interface HistoryListener {
	void stateEntersHistory(State state, int position);

	void stateLeavesHistory(State state, int position);
}
