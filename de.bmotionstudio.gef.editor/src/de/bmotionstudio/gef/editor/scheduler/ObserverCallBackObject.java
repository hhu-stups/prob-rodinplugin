/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import de.bmotionstudio.gef.editor.BindingObject;
import de.bmotionstudio.gef.editor.model.BControl;

public class ObserverCallBackObject extends BindingObject implements Cloneable {

	private BControl control;
	private String observerID;

	public ObserverCallBackObject(BControl control, String observerID) {
		this.control = control;
		this.observerID = observerID;
	}

	public ObserverCallBackObject() {
		this(null, "");
	}

	public BControl getControl() {
		return control;
	}

	public void setControl(BControl control) {
		Object oldValue = this.control;
		this.control = control;
		firePropertyChange("control", oldValue, this.control);
	}

	public String getObserverID() {
		return observerID;
	}

	public void setObserverID(String observerID) {
		Object oldValue = this.observerID;
		this.observerID = observerID;
		firePropertyChange("observerID", oldValue, this.observerID);
	}

	public ObserverCallBackObject clone() throws CloneNotSupportedException {
		ObserverCallBackObject tmpObj = (ObserverCallBackObject) super.clone();
		return tmpObj;
	}

}
