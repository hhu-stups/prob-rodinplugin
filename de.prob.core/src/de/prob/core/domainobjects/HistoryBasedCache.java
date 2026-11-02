/**
 * 
 */
package de.prob.core.domainobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a container class that stores information belonging to a certain
 * state, but removes that information as soon as the state leaves the history.
 * 
 * @author plagge
 * 
 */
public class HistoryBasedCache<T> implements HistoryListener {
	// all methods are synchronized: the history notifies this cache while
	// holding its own lock, users access it from other threads
	private final Map<State, Integer> stateCounter = new HashMap<State, Integer>();
	private final Map<State, T> store = new HashMap<State, T>();
	private final History history;

	public HistoryBasedCache(final History history) {
		this.history = history;
		HistoryItem[] items = history.getAllItems();
		for (final HistoryItem item : items) {
			stateEntersHistory(item.getState(), 0);
		}
	}

	/**
	 * @return true if this cache follows <code>history</code> (the animator
	 *         gets a new history when probcli is restarted)
	 */
	public boolean belongsTo(final History history) {
		return this.history == history;
	}

	public History getHistory() {
		return history;
	}

	@Override
	public synchronized void stateEntersHistory(final State state, final int position) {
		final Integer counter = stateCounter.get(state);
		stateCounter.put(state, counter == null ? 1 : counter + 1);
	}

	@Override
	public synchronized void stateLeavesHistory(final State state, final int position) {
		final Integer counter = stateCounter.get(state);
		if (counter != null) {
			if (counter == 1) {
				stateCounter.remove(state);
				store.remove(state);
			} else {
				stateCounter.put(state, counter - 1);
			}
		}
	}

	public synchronized void put(final State state, final T info) {
		if (stateCounter.containsKey(state)) {
			store.put(state, info);
		}
	}

	public synchronized T get(final State state) {
		return store.get(state);
	}

	public synchronized void clear() {
		store.clear();
	}
}
