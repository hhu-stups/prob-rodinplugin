/**
 * 
 */
package de.prob.core.domainobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.prob.core.command.GetMachineObjectsCommand.MachineObjectsResult;
import de.prob.core.types.TypedIdentifier;

/**
 * @author plagge
 * 
 */
public class MachineDescription {
	public enum SectionType {
		CONTEXT, MODEL
	};

	public static class Section {
		private final SectionType type;
		private final String name;

		public Section(SectionType type, String name) {
			this.type = type;
			this.name = name;
		}

		public SectionType getType() {
			return type;
		}

		public String getName() {
			return name;
		}
	}

	// private final TypedIdentifier[] typedIdentifiers;
	private final List<Section> sections, nonEmptySections;
	private final Collection<String> identifiers;
	private final Map<String, List<String>> sectionIdentifiers;
	private final Map<String, String> sectionOfIdentifier;
	private final Map<String, Collection<String>> allSectionsOfIdentifier;
	private final Map<String, TypedIdentifier> types;

	private final Collection<String> eventNames;

	public MachineDescription(MachineObjectsResult mor) {
		final int clen = mor.constants.length;
		final int vlen = mor.variables.length;
		final TypedIdentifier[] typedIdentifiers = new TypedIdentifier[clen
				+ vlen];
		System.arraycopy(mor.constants, 0, typedIdentifiers, 0, clen);
		System.arraycopy(mor.variables, 0, typedIdentifiers, clen, vlen);

		this.sections = Collections.unmodifiableList(Arrays
				.asList(mor.sections));

		Map<String, List<String>> secIds = new HashMap<String, List<String>>();
		ArrayList<String> ids = new ArrayList<String>(typedIdentifiers.length);
		Map<String, String> secOfId = new HashMap<String, String>();
		this.allSectionsOfIdentifier = new HashMap<String, Collection<String>>();
		classifyIdentifiers(typedIdentifiers, secIds, ids, secOfId,
				allSectionsOfIdentifier);

		this.identifiers = Collections.unmodifiableCollection(ids);
		this.sectionIdentifiers = Collections.unmodifiableMap(secIds);
		this.sectionOfIdentifier = Collections.unmodifiableMap(secOfId);

		this.eventNames = extractEvents(mor);
		this.nonEmptySections = filterNonEmpty(sections, sectionIdentifiers);
		this.types = createTypeMap(typedIdentifiers);
	}

	private Map<String, TypedIdentifier> createTypeMap(
			TypedIdentifier[] typedIdentifiers) {
		Map<String, TypedIdentifier> typeMap = new HashMap<String, TypedIdentifier>();
		for (final TypedIdentifier ti : typedIdentifiers) {
			typeMap.put(ti.getName(), ti);
		}
		return Collections.unmodifiableMap(typeMap);
	}

	private static void classifyIdentifiers(
			final TypedIdentifier[] typedIdentifiers,
			Map<String, List<String>> secIds, ArrayList<String> ids,
			Map<String, String> secOfId,
			Map<String, Collection<String>> allSecsOfId) {
		for (TypedIdentifier tid : typedIdentifiers) {
			final String name = tid.getName();
			ids.add(name);
			boolean first = true;
			final Collection<String> allSections = tid.getSections();
			allSecsOfId.put(name, allSections);
			for (String sec : allSections) {
				if (first) {
					secOfId.put(name, sec);
					first = false;
				}
				List<String> idsOfSection = secIds.get(sec);
				if (idsOfSection == null) {
					idsOfSection = new ArrayList<String>();
					secIds.put(sec, idsOfSection);
				}
				idsOfSection.add(name);
			}
		}
	}

	private static Collection<String> extractEvents(MachineObjectsResult mor) {
		Collection<String> events = new ArrayList<String>();
		for (TypedIdentifier tevent : mor.operations) {
			events.add(tevent.getName());
		}
		return Collections.unmodifiableCollection(events);
	}

	private static List<Section> filterNonEmpty(List<Section> sections,
			Map<String, List<String>> vars) {
		List<Section> nonEmpty = new ArrayList<Section>(sections);
		for (Iterator<Section> it = nonEmpty.iterator(); it.hasNext();) {
			Section sec = it.next();
			List<String> svars = vars.get(sec.getName());
			if (svars == null || svars.isEmpty()) {
				it.remove();
			}
		}
		return Collections.unmodifiableList(nonEmpty);
	}

	/**
	 * Gives the list of all sections, i.e. models or contexts. From the most
	 * abstract to the most concrete. The contextes come first.
	 * 
	 * @return a list of sections, never <code>null</code>
	 */
	public List<Section> getSections() {
		return sections;
	}

	/**
	 * Like {@link #getSections()}, but this returns only sections for which
	 * identifier are registered.
	 * 
	 * @return a list of sections, never <code>null</code>
	 */
	public List<Section> getNonEmptySections() {
		return nonEmptySections;
	}

	public Collection<String> getIdentifiers() {
		return identifiers;
	}

	/**
	 * Like {@link #getSections()}, but it returns only the names of the models.
	 * From the most abstract to the most concrete.
	 * 
	 * @return a list of model names, never <code>null</code>
	 */
	public List<String> getModelNames() {
		List<String> names = new ArrayList<String>();
		for (final Section section : sections) {
			if (section.type == SectionType.MODEL) {
				names.add(section.getName());
			}
		}
		return names;
	}

	public List<String> getIdentifiersOfSection(String section) {
		return sectionIdentifiers.get(section);
	}

	public String getSectionOfIdentifier(String identifier) {
		return sectionOfIdentifier.get(identifier);
	}

	public Collection<String> getEventNames() {
		return eventNames;
	}

	public Collection<String> getAllSectionsOfIdentifier(String identifier) {
		return allSectionsOfIdentifier.get(identifier);
	}

	public TypedIdentifier getTypeOfIdentifier(final String name) {
		return types.get(name);
	}
}
