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
 * The ProB data type couple(A,B)
 * 
 * @author plagge
 */
public class CoupleProbType extends ProbDataType {
	private final ProbDataType left, right;

	public CoupleProbType(final ProbDataType left, final ProbDataType right) {
		super();
		if (left == null || right == null) {
			throw new IllegalArgumentException(
					"Subtypes of couple data type must not be null");
		}
		this.left = left;
		this.right = right;
	}

	public int getOperatorPriority() {
		return 190;
	}

	public void prettyprint(final StringBuilder builder) {
		printWithParenthesis(builder, left, getOperatorPriority());
		builder.append('*');
		printWithParenthesis(builder, right, getOperatorPriority() + 1);
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof CoupleProbType) {
			CoupleProbType ocouple = (CoupleProbType) other;
			isEqual = left.equals(ocouple.left) && right.equals(ocouple.right);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return ((left.hashCode() * 13 + right.hashCode()) * 17 + 8);
	}

}
