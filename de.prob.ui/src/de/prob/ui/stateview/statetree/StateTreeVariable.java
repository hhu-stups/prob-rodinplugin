/**
 * 
 */
package de.prob.ui.stateview.statetree;

import org.apache.commons.lang.ObjectUtils;

import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.Variable;

/**
 * Represents the property of the value of a specific variable.
 * 
 * @author plagge
 */
public class StateTreeVariable extends AbstractStateTreeElement {
	public static final StateTreeVariable[] EMPTY_ARRAY = new StateTreeVariable[0];

	private final String name;
	private final boolean isInMainSection;

	public StateTreeVariable(final StaticStateElement parent,
			final String name, final boolean isInMainSection) {
		super(parent);
		this.name = name;
		this.isInMainSection = isInMainSection;
	}

	public StaticStateElement[] getChildren() {
		return StateTreeElement.EMPTY_ARRAY;
	}

	public String getLabel() {
		return name;
	}

	public boolean hasChildren() {
		return false;
	}

	public boolean isInMainSection() {
		return isInMainSection;
	}

	public boolean hasChanged(final State current, final State last) {
		final String curVal = getValueOfVar(current);
		final String lastVal = getValueOfVar(last);
		return !ObjectUtils.equals(curVal, lastVal);
	}

	private String getValueOfVar(final State state) {
		final String result;
		if (state != null) {
			final Variable variable = state.getValues().get(name);
			result = variable == null ? null : variable.getValue();
		} else {
			result = null;
		}
		return result;
	}

	public StateDependendElement getValue(final State state) {
		final Variable variable = state.getValues().get(name);
		final StateDependendElement element;
		if (variable != null) {
			element = new StateDependendElement(state, variable.getValue(),
					EStateTreeElementProperty.NONBOOLEAN);
		} else {
			element = null;
		}
		return element;
	}

}
