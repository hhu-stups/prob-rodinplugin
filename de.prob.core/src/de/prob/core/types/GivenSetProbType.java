/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.types;

/**
 * Represents a Prob given set (like in the SETS section of a machine)
 * 
 * @author plagge
 */
public class GivenSetProbType extends ProbDataType {
	private final String name;

	public GivenSetProbType(final String name) {
		super();
		if (name == null) {
			throw new IllegalArgumentException(
					"Name of given set must not be null");
		}
		this.name = name;
	}

	public int getOperatorPriority() {
		return 1000;
	}

	public void prettyprint(final StringBuilder builder) {
		builder.append(name);
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof GivenSetProbType) {
			isEqual = name.equals(((GivenSetProbType) other).name);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return name.hashCode() * 13 + 4;
	}
}
