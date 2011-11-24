/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.core.types;

import java.util.Arrays;

/**
 * The representation of a ProB operation type
 * 
 * @author plagge
 */
public class OperationProbType extends ProbDataType {
	private final ProbDataType[] results, parameters;

	public OperationProbType(final ProbDataType[] results,
			final ProbDataType[] parameters) {
		super();
		checkValidTypeList(results, "result");
		checkValidTypeList(parameters, "parameter");
		this.results = new ProbDataType[results.length];
		this.parameters = new ProbDataType[parameters.length];
		System.arraycopy(results, 0, this.results, 0, results.length);
		System.arraycopy(parameters, 0, this.parameters, 0, parameters.length);
	}

	private static void checkValidTypeList(final ProbDataType[] list,
			final String name) {
		if (list == null) {
			throw new IllegalArgumentException(name
					+ " list of operation type must not be null");
		}
		for (ProbDataType elem : list) {
			if (elem == null) {
				throw new IllegalArgumentException(name
						+ " list of operation type contains null type");
			}
		}
	}

	public int getOperatorPriority() {
		return results.length == 0 ? 500 : 150;
	}

	public void prettyprint(final StringBuilder builder) {
		if (results.length > 0) {
			printTypeList(builder, results);
			builder.append("<--");
		}
		builder.append("op(");
		printTypeList(builder, parameters);
		builder.append(")");
	}

	private static void printTypeList(final StringBuilder builder,
			final ProbDataType[] list) {
		if (list.length > 0) {
			if (list.length == 1) {
				list[0].prettyprint(builder);
			} else {
				boolean first = true;
				for (ProbDataType elem : list) {
					if (!first) {
						builder.append(',');
					}
					printWithParenthesis(builder, elem, 116);
					first = false;
				}
			}
		}
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof OperationProbType) {
			OperationProbType opother = (OperationProbType) other;
			isEqual = Arrays.equals(results, opother.results)
					&& Arrays.equals(parameters, opother.parameters);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return (Arrays.hashCode(results) * 13 + Arrays.hashCode(parameters)) * 17 + 8;
	}

}
