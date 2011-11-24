/**
 * 
 */
package de.prob.ui.stateview.statetree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.core.domainobjects.MachineDescription;
import de.prob.core.domainobjects.State;

/**
 * @author plagge
 * 
 */
public class StateTreeSection extends AbstractStateTreeElement {
	private final String section;
	private final List<StateTreeElement> children;
	private final boolean isMainSectionOfVariable;

	public StateTreeSection(final String section, final MachineDescription md) {
		super(null);
		this.section = section;
		Collection<String> varnames = md.getIdentifiersOfSection(section);
		List<StateTreeElement> children = new ArrayList<StateTreeElement>();
		boolean isMainSectionOfVariable = false;
		for (final String varname : varnames) {
			final String mainSection = md.getSectionOfIdentifier(varname);
			final boolean isInMainSection = section.equals(mainSection);
			children.add(new StateTreeVariable(this, varname, isInMainSection));
			isMainSectionOfVariable |= isInMainSection;
		}
		this.children = children;
		this.isMainSectionOfVariable = isMainSectionOfVariable;
	}

	public StaticStateElement[] getChildren() {
		return children.toArray(StateTreeVariable.EMPTY_ARRAY);
	}

	public String getLabel() {
		return section;
	}

	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public boolean isMainSectionOfVariable() {
		return isMainSectionOfVariable;
	}

	public boolean hasChanged(final State current, final State last) {
		for (final StateTreeElement child : children) {
			if (child.hasChanged(current, last))
				return true;
		}
		return false;
	}

	public StateDependendElement getValue(final State state) {
		return new StateDependendElement(state, null,
				hasChildren() ? EStateTreeElementProperty.NONBOOLEAN
						: EStateTreeElementProperty.INACTIVE);
	}

}
