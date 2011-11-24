/**
 * 
 */
package de.prob.sap.commands;

import java.util.Collection;
import java.util.Collections;

/**
 * Instances of this class are created by the {@link GenerateTestcaseCommand}
 * and contains a few properties about the generated test cases.
 * 
 * @author plagge
 */
public class GlobalTestcaseResult {
	private final int numberOfTestcases;
	private final Collection<String> uncoveredEvents;

	public GlobalTestcaseResult(int numberOfTestcases,
			Collection<String> uncoveredEvents) {
		this.numberOfTestcases = numberOfTestcases;
		this.uncoveredEvents = Collections
				.unmodifiableCollection(uncoveredEvents);
	}

	/**
	 * @return the number of test cases that have been generated
	 */
	public int getNumberOfTestcases() {
		return numberOfTestcases;
	}

	/**
	 * @return the list of events that weren't covered by any test cases, never
	 *         <code>null</code>.
	 */
	public Collection<String> getUncoveredEvents() {
		return uncoveredEvents;
	}
}