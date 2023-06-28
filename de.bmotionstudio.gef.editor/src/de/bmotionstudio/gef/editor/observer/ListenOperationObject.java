/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.BindingObject;

public class ListenOperationObject extends BindingObject implements Cloneable {

	private String operationName;
	private String predicate;

	public ListenOperationObject() {
		super();
		this.operationName = "";
		this.predicate = "";
	}

	public ListenOperationObject clone() throws CloneNotSupportedException {
		return (ListenOperationObject) super.clone();
	}

	public void setOperationName(String operationName) {
		Object oldValue = this.operationName;
		this.operationName = operationName;
		firePropertyChange("operationName", oldValue, this.operationName);
	}

	public String getOperationName() {
		return operationName;
	}

	public void setPredicate(String predicate) {
		Object oldValue = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldValue, this.predicate);
	}

	public String getPredicate() {
		return predicate;
	}

}
