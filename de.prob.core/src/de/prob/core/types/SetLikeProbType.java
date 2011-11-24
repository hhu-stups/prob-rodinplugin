/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.core.types;

/**
 * The base class for ProB data types like set(...) or seq(...).
 * 
 * @author plagge
 */
public abstract class SetLikeProbType extends ProbDataType {
	private final String variant;
	private final ProbDataType innerType;

	protected SetLikeProbType(final String variant, final ProbDataType innerType) {
		super();
		if (innerType == null) {
			throw new IllegalArgumentException("Inner type to " + variant
					+ " constructor must not be null");
		}
		this.variant = variant;
		this.innerType = innerType;
	}

	public int getOperatorPriority() {
		return 500;
	}

	public void prettyprint(final StringBuilder builder) {
		builder.append(variant);
		builder.append('(');
		innerType.prettyprint(builder);
		builder.append(')');
	}

	public ProbDataType getInnerType() {
		return innerType;
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof SetLikeProbType) {
			SetLikeProbType setlikeother = (SetLikeProbType) other;
			isEqual = variant == setlikeother.variant
					&& innerType.equals(setlikeother.innerType);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return innerType.hashCode() * 17 + variant.hashCode();
	}

}
