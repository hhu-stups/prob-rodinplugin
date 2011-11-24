/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.types;

/**
 * Represents a data type like it is used inside ProB. This is the abstract base
 * class for the different type implementations.
 * 
 * @author plagge
 */
public abstract class ProbDataType implements PrettyPrintable {
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		prettyprint(sb);
		return sb.toString();
	}

	protected final static void printWithParenthesis(StringBuilder builder,
			PrettyPrintable toPrint, int minPrio) {
		final boolean useParenthesis = toPrint.getOperatorPriority() < minPrio;
		if (useParenthesis) {
			builder.append('(');
		}
		toPrint.prettyprint(builder);
		if (useParenthesis) {
			builder.append(')');
		}
	}
}
