/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.domainobjects;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class ProBPreference {

	public final String name;
	public final PrologTerm type;
	public final String description;
	public final String category;
	public final String defaultValue;

	// information about probcli itself; probcli rejects setting them
	private static final Set<String> READ_ONLY = Collections
			.unmodifiableSet(new HashSet<String>(Arrays.asList(
					"prob_version_info", "prob_revision_info",
					"prob_lastchangedate_info")));

	private final static int NAME = 1;
	private final static int TYPE = 2;
	private final static int DESC = 3;
	private final static int CAT = 4;
	private final static int DEFAULT = 5;

	public ProBPreference(final CompoundPrologTerm term) {
		name = PrologTerm.atomicString(term.getArgument(NAME));
		type = term.getArgument(TYPE);
		description = PrologTerm.atomicString(term.getArgument(DESC));
		category = PrologTerm.atomicString(term.getArgument(CAT));
		final PrologTerm defaultTerm = term.getArgument(DEFAULT);
		defaultValue = defaultTerm.isAtom() ? PrologTerm
				.atomicString(defaultTerm) : defaultTerm.toString();
	}

	/**
	 * @return true if probcli does not allow to set the preference; it must
	 *         not be stored or sent to probcli
	 */
	public static boolean isReadOnly(final String name) {
		return READ_ONLY.contains(name);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append("(cat. ").append(category);
		sb.append(", type ").append(type.toString());
		sb.append(", default ").append(defaultValue);
		sb.append(") ").append(description);
		return sb.toString();
	}

}
