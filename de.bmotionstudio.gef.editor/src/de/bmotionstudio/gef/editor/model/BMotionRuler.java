/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.rulers.RulerProvider;


public class BMotionRuler implements Serializable {

	public transient static final String PROPERTY_CHILDREN = "children changed"; //$NON-NLS-1$
	public transient static final String PROPERTY_UNIT = "units changed"; //$NON-NLS-1$

	static transient final long serialVersionUID = 1;

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);
	private int unit;
	private boolean horizontal;
	private List<BMotionGuide> guides = new ArrayList<BMotionGuide>();

	public BMotionRuler(boolean isHorizontal) {
		this(isHorizontal, RulerProvider.UNIT_PIXELS);
	}

	public BMotionRuler(boolean isHorizontal, int unit) {
		horizontal = isHorizontal;
		setUnit(unit);
	}

	protected Object readResolve() {
		this.listeners = new PropertyChangeSupport(this);
		return this;
	}

	public void addGuide(BMotionGuide guide) {
		if (!guides.contains(guide)) {
			guide.setHorizontal(!isHorizontal());
			guides.add(guide);
			listeners.firePropertyChange(PROPERTY_CHILDREN, null, guide);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	// the returned list should not be modified
	public List<BMotionGuide> getGuides() {
		return guides;
	}

	public int getUnit() {
		return unit;
	}

	public boolean isHidden() {
		return false;
	}

	public boolean isHorizontal() {
		return horizontal;
	}

	public void removeGuide(BMotionGuide guide) {
		if (guides.remove(guide)) {
			listeners.firePropertyChange(PROPERTY_CHILDREN, null, guide);
		}
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public void setHidden(boolean isHidden) {
	}

	public void setUnit(int newUnit) {
		if (unit != newUnit) {
			int oldUnit = unit;
			unit = newUnit;
			listeners.firePropertyChange(PROPERTY_UNIT, oldUnit, newUnit);
		}
	}

}
