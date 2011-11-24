/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.MachineDescription.Section;

/**
 * An instance of this class contains two states: A current state and an
 * (optional) last state.
 * 
 * TODO: This class should be replaced as soon as possible
 * 
 * @author plagge
 */
public class ShownState {
	public static final String OTHER_SECTION_ID = "__Other__";

	private State current, last;
	private MachineDescription description;

	private Collection<String> usedSections;
	private List<String> otherVariables;

	private void reset() {
		usedSections = null;
		otherVariables = null;
	}

	private void compute() {
		if (usedSections == null) {
			usedSections = new HashSet<String>();
			addUsedSections(current);
			addUsedSections(last);
		}
	}

	public void setMachineDescription(final MachineDescription description) {
		reset();
		this.description = description;
	}

	public void setCurrent(final State current) {
		reset();
		this.current = current;
	}

	public void setLast(final State last) {
		reset();
		this.last = last;
	}

	/**
	 * @return the machine description object of the loaded machine to which the
	 *         current and last state belongs to
	 */
	public MachineDescription getMachineDescription() {
		return description;
	}

	/**
	 * @return all sections names as list, from the most abstract one to the
	 *         most concrete, never <code>null</code>
	 */
	public List<String> getSections() {
		compute();
		final List<String> sectionNames;
		if (description == null) {
			sectionNames = Collections.emptyList();
		} else {
			final List<Section> neSecs = description.getNonEmptySections();
			sectionNames = new ArrayList<String>(neSecs.size());
			for (Section s : neSecs) {
				sectionNames.add(s.getName());
			}
		}
		final List<String> allSectionNames;
		if (isOtherSectionNeeded()) {
			allSectionNames = new ArrayList<String>(sectionNames);
			allSectionNames.add(OTHER_SECTION_ID);
		} else {
			allSectionNames = sectionNames;
		}
		return allSectionNames;
	}

	/**
	 * @param section
	 *            the name of a section (machine/context), never
	 *            <code>null</code>
	 * @return if there is a variable in the given section that has a value in
	 *         the current or last state
	 */
	public boolean sectionHasValue(final String section) {
		compute();
		return usedSections.contains(section);
	}

	/**
	 * @param section
	 *            the name of a section (machine/context), never
	 *            <code>null</code>
	 * @return a list of variable names that are present in the given section,
	 *         never <code>null</code>, even if the section is unknown
	 */
	public List<String> getSectionVariables(final String section) {
		compute();
		final List<String> result;
		if (OTHER_SECTION_ID.equals(section) && isOtherSectionNeeded()) {
			result = otherVariables;
		} else {
			final List<String> vars;
			if (description == null) {
				vars = Collections.emptyList();
			} else {
				vars = description.getIdentifiersOfSection(section);
			}
			if (vars == null) {
				result = Collections.emptyList();
			} else {
				result = vars;
			}
		}
		return result;
	}

	private boolean isOtherSectionNeeded() {
		return sectionHasValue(OTHER_SECTION_ID);
	}

	private void addUsedSections(final State state) {
		if (state != null) {
			for (String id : state.getValues().keySet()) {
				final String section = description == null ? null : description
						.getSectionOfIdentifier(id);
				if (section != null) {
					usedSections.add(section);
					usedSections.addAll(description
							.getAllSectionsOfIdentifier(id));
				} else {
					usedSections.add(OTHER_SECTION_ID);
					if (otherVariables == null) {
						otherVariables = new ArrayList<String>();
					}
					if (!otherVariables.contains(id)) {
						otherVariables.add(id);
					}
				}
				usedSections.add(section == null ? OTHER_SECTION_ID : section);
			}
		}
	}

}
