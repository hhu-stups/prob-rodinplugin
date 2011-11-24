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
 * Representation of a ProB data type free type
 * 
 * @author plagge
 */
public class FreetypeProbType extends ProbDataType {
	private final String identifier;

	public FreetypeProbType(final String identifier) {
		super();
		if (identifier == null) {
			throw new IllegalArgumentException(
					"Freetype identifier must not be null");
		}
		this.identifier = identifier;
	}

	public int getOperatorPriority() {
		return 500;
	}

	public void prettyprint(final StringBuilder builder) {
		//		builder.append("freetype(");
		builder.append(identifier);
		//		builder.append(':');
		//		boolean first = true;
		//		for (Map.Entry<String, ProbDataType> entry : constructors.entrySet()) {
		//			if (!first) {
		//				builder.append(',');
		//			}
		//			builder.append(entry.getKey());
		//			if (entry.getValue() != null) {
		//				builder.append('(');
		//				entry.getValue().prettyprint(builder);
		//				builder.append(')');
		//			}
		//			first = false;
		//		}
		//		builder.append(')');
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof FreetypeProbType) {
			FreetypeProbType fother = (FreetypeProbType) other;
			isEqual = identifier.equals(fother.identifier);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return identifier.hashCode() * 11 + 14;
		//		return (identifier.hashCode() * 11 + constructors.hashCode()) * 7 + 4;
	}
}
