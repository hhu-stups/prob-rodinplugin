package de.prob.core.domainobjects.ltl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import de.prob.core.command.LtlCheckingCommand.PathType;

/**
 * Provides an abstract class for all types of propositions.
 * 
 * @author Andriy Tolstoy
 * 
 */
public abstract class CounterExampleProposition {
	protected final String name;
	protected final String fullName;
	protected final int loopEntry;
	protected final PathType pathType;
	protected CounterExampleProposition parent;
	private List<CounterExampleValueType> values;

	protected final PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);
	private boolean visible = false;
	protected int stateId = 0;

	public CounterExampleProposition(final String name, final String fullName,
			final PathType pathType, final int loopEntry) {
		this.name = name;
		this.fullName = fullName;
		this.loopEntry = loopEntry;
		this.pathType = pathType;
	}

	public CounterExampleProposition getParent() {
		return parent;
	}

	public void setParent(final CounterExampleProposition parent) {
		this.parent = parent;
	}

	public String getFullName() {
		return fullName;
	}

	public List<CounterExampleValueType> getValues() {
		if (values == null) {
			values = calculate();
		}

		return values;
	}

	public List<CounterExampleProposition> getChildren() {
		List<CounterExampleProposition> children = new ArrayList<CounterExampleProposition>();
		children.add(this);
		return children;
	}

	public abstract boolean hasChildren();

	public PathType getPathType() {
		return pathType;
	}

	public int getLoopEntry() {
		return loopEntry;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		boolean oldVisible = this.visible;
		this.visible = visible;
		listeners.firePropertyChange("visible", oldVisible, visible);
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		int oldStateId = this.stateId;
		this.stateId = stateId;
		listeners.firePropertyChange("stateId", oldStateId, stateId);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public List<Integer> fillPositions(int position, int index,
			int checkedSize, boolean isPastOperator) {
		List<Integer> positions = new ArrayList<Integer>();

		if (index != -1) {
			int pos = isPastOperator ? index : index + position;
			pos = calculatePosition(pos);
			positions.add(pos);
		} else {
			for (int i = 0; i < checkedSize; i++) {
				int pos = isPastOperator ? position - i : position + i;
				pos = calculatePosition(pos);
				positions.add(pos);
			}
		}

		return positions;
	}

	public boolean isTransition() {
		return false;
	}

	protected abstract List<CounterExampleValueType> calculate();

	protected abstract int calculatePosition(int pos);
}
