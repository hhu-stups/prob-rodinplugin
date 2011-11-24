/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.domainobjects;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.prob.logging.Logger;

public final class State {

	private static final String ERROR_MSG = "Enabled Operations was null, this is most likely an error in the core.";

	private final String id;
	private final boolean initialized;
	private final boolean invariantViolated;
	private final List<Operation> enabledOperations;
	private final boolean timeoutOccured;
	private final boolean maxOperationReached;
	private final Collection<StateError> stateErrors;
	private final Map<String, Variable> stateValues;
	private final Set<String> timeout;

	public State(final String stateId, final boolean initialised,
			final boolean invariantKo, final boolean timeoutOccured,
			final boolean maxOperationReached,
			final Collection<Variable> stateValues,
			final List<Operation> enabledOperations,
			final Collection<StateError> stateErrors,
			final Set<String> timeoutedOperations) {
		this.id = stateId;
		this.initialized = initialised;
		this.invariantViolated = invariantKo;
		this.timeoutOccured = timeoutOccured;
		this.maxOperationReached = maxOperationReached;
		this.stateErrors = stateErrors;
		this.timeout = timeoutedOperations;

		Map<String, Variable> vars = new HashMap<String, Variable>(
				stateValues.size());
		for (Variable v : stateValues) {
			final String identifier = v.getIdentifier();
			if (vars.containsKey(identifier)) {
				Logger.notifyUser("ProB returned a variable twice.");
			} else {
				vars.put(identifier, v);
			}
		}
		this.stateValues = Collections.unmodifiableMap(vars);

		Logger.assertProB(ERROR_MSG, enabledOperations != null);

		this.enabledOperations = Collections
				.unmodifiableList(enabledOperations);
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isInvariantViolated() {
		return invariantViolated;
	}

	public boolean isInvariantPreserved() {
		return !invariantViolated;
	}

	public boolean isMaxOperationReached() {
		return maxOperationReached;
	}

	public boolean isTimeoutOccured() {
		return timeoutOccured;
	}

	public String getId() {
		return id;
	}

	/**
	 * Returns the enabled operations of this state, but only if the operations
	 * of this state have already been examined. Otherwise <code>null</code> is
	 * returned.
	 * 
	 * @return {@link List} of operations or <code>null</code> if not examined
	 *         yet.
	 */
	public final List<Operation> getEnabledOperations() {
		return enabledOperations;
	}

	public Map<String, Variable> getValues() {
		return stateValues;
	}

	public boolean variableHasValue(final String variable) {
		return stateValues.containsKey(variable);
	}

	@Override
	public String toString() {
		final StringBuilder result = new StringBuilder();
		result.append(id);
		if (isInvariantViolated()) {
			result.append(" (Invariant violated)");
		}
		result.append(": Var[");
		result.append(stateValues.values());
		result.append("]");
		return result.toString();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof State)
			return this.id.equals(((State) obj).id);
		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	public Collection<StateError> getStateBasedErrors() {
		return stateErrors;
	}

	public boolean hasStateBasedErrors() {
		return !stateErrors.isEmpty();
	}

	public boolean isTimeoutOp(final String opName) {
		return timeout.contains(opName);
	}

}
