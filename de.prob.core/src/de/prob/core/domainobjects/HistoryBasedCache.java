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
	private final Map<State, Integer> stateCounter = new HashMap<State, Integer>();
	private final Map<State, T> store = new HashMap<State, T>();

	public HistoryBasedCache(final History history) {
		HistoryItem[] items = history.getAllItems();
		for (final HistoryItem item : items) {
			stateEntersHistory(item.getState(), 0);
		}
	}

	public void stateEntersHistory(final State state, final int position) {
		final Integer counter = stateCounter.get(state);
		stateCounter.put(state, counter == null ? 1 : counter + 1);
	}

	public void stateLeavesHistory(final State state, final int position) {
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

	public void put(final State state, final T info) {
		if (stateCounter.containsKey(state)) {
			store.put(state, info);
		}
	}

	public T get(final State state) {
		return store.get(state);
	}

	public void clear() {
		store.clear();
	}
}
