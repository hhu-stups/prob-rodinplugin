/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.observer;

/**
 * @author Lukas Ladenberger
 * 
 */
public class ObserverCloneObject extends ObserverEvalObject implements
		Cloneable {

	private String controlId;

	private int counter;

	public ObserverCloneObject() {
		super();
	}

	public void setControlId(String controlId) {
		Object oldValue = this.controlId;
		this.controlId = controlId;
		firePropertyChange("controlId", oldValue, this.controlId);
	}

	public String getControlId() {
		return controlId;
	}

	public void setCounter(int counter) {
		Object oldValue = this.counter;
		this.counter = counter;
		firePropertyChange("counter", oldValue, this.counter);
	}

	public int getCounter() {
		return counter;
	}

	public ObserverCloneObject clone() throws CloneNotSupportedException {
		return (ObserverCloneObject) super.clone();
	}

}
