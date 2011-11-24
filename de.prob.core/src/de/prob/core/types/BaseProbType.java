/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.types;

/**
 * The basic data types like INTEGER, BOOL and STRING. These are a singleton
 * types. (well, actually they are multitons)
 * 
 * @author plagge
 */
public class BaseProbType extends ProbDataType {
	public final static BaseProbType INTEGER = new BaseProbType("INTEGER");
	public final static BaseProbType BOOL = new BaseProbType("BOOLEAN");
	public final static BaseProbType STRING = new BaseProbType("STRING");
	public final static BaseProbType ANY = new BaseProbType("?");
	public final static BaseProbType PREDICATE = new BaseProbType("predicate");
	public final static BaseProbType SUBSTITUTION = new BaseProbType(
			"substitution");

	private final String type;

	private BaseProbType(final String type) {
		super();
		this.type = type;
	}

	public int getOperatorPriority() {
		return 1000;
	}

	public void prettyprint(final StringBuilder builder) {
		builder.append(type);
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof BaseProbType) {
			isEqual = type.equals(((BaseProbType) other).type);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return type.hashCode() * 37 + 10;
	}

}
