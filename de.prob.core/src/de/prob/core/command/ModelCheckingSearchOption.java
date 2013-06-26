/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.core.command;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ModelCheckingSearchOption {
	breadth_first_search(0, "Breadth First Search", false), find_deadlocks(1,
			"Find Deadlocks", true), find_invariant_violations(2,
			"Find Invariant Violations", true), find_assertion_violations(3,
			"Find Theorem Violations", false), inspect_existing_nodes(4,
			"Recheck Existing States", false), stop_at_full_coverage(5,
			"Stop when all Events are Covered", false);

	private final String text;
	private final int pos;
	private final boolean enabledByDefault;

	private ModelCheckingSearchOption(final int pos, final String text,
			final boolean enabledByDefault) {
		this.pos = pos;
		this.text = text;
		this.enabledByDefault = enabledByDefault;
	}

	public final String getDescription() {
		return text;
	}

	private static final Map<Integer, ModelCheckingSearchOption> lookup = new HashMap<Integer, ModelCheckingSearchOption>();

	static {
		for (ModelCheckingSearchOption s : EnumSet
				.allOf(ModelCheckingSearchOption.class)) {
			lookup.put(s.getPos(), s);
		}
	}

	public final int getPos() {
		return pos;
	}

	public final boolean isEnabledByDefault() {
		return enabledByDefault;
	}

	public final static ModelCheckingSearchOption get(final int code) {
		return lookup.get(code);
	}

}