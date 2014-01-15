package de.prob.eventb.disprover.core.internal;

import java.util.*;

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
	private String reason = "";

	CounterExample(final boolean counterExampleFound,
			final boolean timeoutOccured) {
		this.counterExampleFound = counterExampleFound;
		this.timeoutOccured = timeoutOccured;
	}

	CounterExample(final boolean counterExampleFound,
			final boolean timeoutOccured, String reason) {
		this(counterExampleFound, timeoutOccured);
		this.reason = reason;
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
			return "Counter-Example found: " + state.toString();
		} else {
			if (isProof()) {
				return "No Counter-Example exists: Proof.";
			} else {
				if (timeoutOccured()) {
					return "No Counter-Example found due to Time-Out.";
				} else {
					return "No Counter-Example found.";
				}
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

	@Override
	public String getReason() {
		return reason;
	}
}
