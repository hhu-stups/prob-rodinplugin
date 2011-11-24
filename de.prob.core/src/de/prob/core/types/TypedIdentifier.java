/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

/**
 * 
 */
package de.prob.core.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * An ProB identifier together with its type
 * 
 * @author plagge
 */
public class TypedIdentifier {
	private final String name;
	private final ProbDataType type;
	private final Collection<String> sections;

	/**
	 * Creates a typed identifier
	 * 
	 * @param name
	 *            its name, never <code>null</code>
	 * @param type
	 *            its type, never <code>null</code>
	 */
	public TypedIdentifier(final String name, final ProbDataType type,
			final String[] sections) {
		super();
		if (name == null || type == null)
			throw new IllegalArgumentException(
					"Name and type of a typed identifier must not be null");
		this.name = name;
		this.type = type;
		if (sections == null) {
			this.sections = Collections.emptyList();
		} else {
			this.sections = Collections.unmodifiableCollection(Arrays
					.asList(sections));
		}
	}

	/**
	 * @return Name of the identifier, never <code>null</code>
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return the type of the identifier, never <code>null</code>
	 */
	public ProbDataType getType() {
		return type;
	}

	/**
	 * @return the list of sections (contextes or models) where this identifier
	 *         occurs
	 */
	public Collection<String> getSections() {
		return sections;
	}

	@Override
	public boolean equals(final Object other) {
		boolean isEqual;
		if (this == other) {
			isEqual = true;
		} else if (other != null && other instanceof TypedIdentifier) {
			TypedIdentifier ti = (TypedIdentifier) other;
			return name.equals(ti.name) && type.equals(ti.type);
		} else {
			isEqual = false;
		}
		return isEqual;
	}

	@Override
	public int hashCode() {
		return (name.hashCode() * 23 + type.hashCode()) * 11 + 24;
	}
}
