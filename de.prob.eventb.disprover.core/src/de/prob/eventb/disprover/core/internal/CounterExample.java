package de.prob.eventb.disprover.core.internal;

import java.util.SortedMap;
import java.util.TreeMap;

import de.prob.eventb.disprover.core.ICounterExample;

/**
 * This class wraps the results from a Disprover run. It either indicates that
 * there is no counter-example, or it provides a way to show the counter
 * example.
 * 
 * @author jastram
 * 
 */
class CounterExample implements ICounterExample {

	private final boolean counterExampleFound;
	public final SortedMap<String, String> state = new TreeMap<String, String>();
	private final boolean timeoutOccured;
	private boolean proof = false;

	CounterExample(final boolean counterExampleFound,
			final boolean timeoutOccured) {
		this.counterExampleFound = counterExampleFound;
		this.timeoutOccured = timeoutOccured;
	}

	@Override
	public boolean isProof() {
		return proof;
	}

	public void setProof(boolean proof) {
		this.proof = proof;
	}

	@Override
	public boolean counterExampleFound() {
		return counterExampleFound;
	}

	@Override
	public String toString() {
		if (counterExampleFound) {
			return state.toString();
		} else {
			if (isProof()) {
				return "No Counter-Example exists.";
			} else {
				return "No Counter-Example found.";
			}
		}
	}

	void addVar(String name, String value) {
		state.put(name, value);
	}

	@Override
	public boolean timeoutOccured() {
		return timeoutOccured;
	}
}
