/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class BindingObject implements Cloneable {

	private transient PropertyChangeSupport propertyChangeSupport;

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName,
				listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(propertyName,
				listener);
	}

	protected void firePropertyChange(String propertyName, Object oldValue,
			Object newValue) {
		getPropertyChangeSupport().firePropertyChange(propertyName, oldValue,
				newValue);
	}

	public void setPropertyChangeSupport(
			PropertyChangeSupport propertyChangeSupport) {
		this.propertyChangeSupport = propertyChangeSupport;
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		if (propertyChangeSupport == null)
			propertyChangeSupport = new PropertyChangeSupport(this);
		return propertyChangeSupport;
	}

	public BindingObject clone() throws CloneNotSupportedException {
		BindingObject clone = (BindingObject) super.clone();
		clone.setPropertyChangeSupport(null);
		return clone;
	}

}
