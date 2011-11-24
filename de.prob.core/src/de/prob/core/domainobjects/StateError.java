/**
 * 
 */
package de.prob.core.domainobjects;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * An instance of this class represents a state based error. Such errors
 * happened in an event starting in a state.
 * 
 * The current implementation is very limited, in future, the error should be
 * easy to examine by the user, including visualization of predicates or
 * expressions.
 * 
 * @author plagge
 */
public class StateError {
	private final String event;
	private final String shortDescription;
	private final String longDescription;

	public StateError(final String event, final String shortDescription,
			final String longDescription) {
		super();
		this.event = event;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
	}

	public StateError(final CompoundPrologTerm term) {
		this.event = PrologTerm.atomicString(term.getArgument(1));
		this.shortDescription = PrologTerm.atomicString(term.getArgument(2));
		this.longDescription = PrologTerm.atomicString(term.getArgument(3));
	}

	public String getEvent() {
		return event;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getLongDescription() {
		return longDescription;
	}

}
