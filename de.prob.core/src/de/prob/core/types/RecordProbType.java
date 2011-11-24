/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.core.types;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Representation of the ProB data type record(...) a.k.a. struct(...)
 * 
 * @author plagge
 */
public class RecordProbType extends ProbDataType {
	private final SortedMap<String, ProbDataType> fields;

	public RecordProbType(final Map<String, ProbDataType> fields) {
		super();
		if (fields == null) {
			throw new IllegalArgumentException("fields must not be null");
		}
		if (fields.containsValue(null)) {
			String invalidField = getNullField(fields);
			throw new IllegalArgumentException("field " + invalidField
					+ " has null type");
		}
		this.fields = new TreeMap<String, ProbDataType>(fields);
	}

	private static String getNullField(final Map<String, ProbDataType> fields) {
		for (Map.Entry<String, ProbDataType> entry : fields.entrySet()) {
			if (entry.getValue() == null) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("no null field found");
	}

	public int getOperatorPriority() {
		return 500;
	}

	public void prettyprint(final StringBuilder builder) {
		builder.append("struct(");
		boolean first = true;
		for (Map.Entry<String, ProbDataType> entry : fields.entrySet()) {
			if (!first) {
				builder.append(',');
			}
			builder.append(entry.getKey());
			builder.append(':');
			printWithParenthesis(builder, entry.getValue(), 81);
			first = false;
		}
		builder.append(')');
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (other == this) {
			isEqual = true;
		} else if (other != null && other instanceof RecordProbType) {
			isEqual = fields.equals(((RecordProbType) other).fields);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return fields.hashCode() * 17 + 2;
	}
}
