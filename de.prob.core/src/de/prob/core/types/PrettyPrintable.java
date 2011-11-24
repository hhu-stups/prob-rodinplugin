/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.types;

/**
 * An object of a syntax tree (or similiar) that can be pretty-printed. It
 * should be able to return the binding priority of the operation that has been
 * used inside.
 * 
 * @author plagge
 */
public interface PrettyPrintable {
	/**
	 * Pretty-prints the object
	 * 
	 * @param builder
	 *            a {@link StringBuilder} that must not be <code>null</code>
	 */
	void prettyprint(StringBuilder builder);

	/**
	 * @return the Priority of the binding in the expression, the higher the
	 *         priority, the stronger the binding
	 */
	int getOperatorPriority();
}
