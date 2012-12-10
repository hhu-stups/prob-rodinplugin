/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.BindingObject;

public class ObserverEvalObject extends BindingObject implements Cloneable {

	private String eval;

	private String attribute;

	private Object value;

	private Boolean isExpressionMode;

	private transient Boolean hasError;

	public ObserverEvalObject() {
		this.isExpressionMode = false;
	}

	public ObserverEvalObject(String eval, Boolean isExpressionMode) {
		this.eval = eval;
		this.isExpressionMode = isExpressionMode;
	}

	public ObserverEvalObject(String eval) {
		this(eval, false);
	}

	public void setEval(String eval) {
		Object oldValue = this.eval;
		this.eval = eval;
		firePropertyChange("eval", oldValue, this.eval);
	}

	public String getEval() {
		return eval;
	}

	public ObserverEvalObject clone() throws CloneNotSupportedException {
		return (ObserverEvalObject) super.clone();
	}

	public void setHasError(Boolean hasError) {
		this.hasError = hasError;
	}

	public Boolean hasError() {
		if (hasError == null)
			hasError = false;
		return hasError;
	}

	public void setValue(Object value) {
		Object oldValue = this.value;
		this.value = value;
		firePropertyChange("value", oldValue, this.value);
	}

	public Object getValue() {
		return this.value;
	}

	public void setIsExpressionMode(Boolean isExpressionMode) {
		Object oldValue = this.isExpressionMode;
		this.isExpressionMode = isExpressionMode;
		firePropertyChange("isExpressionMode", oldValue, this.isExpressionMode);
	}

	public Boolean getIsExpressionMode() {
		return isExpressionMode();
	}

	public Boolean isExpressionMode() {
		if (isExpressionMode == null)
			isExpressionMode = false;
		return isExpressionMode;
	}

	public void setAttribute(String attribute) {
		Object oldValue = this.attribute;
		this.attribute = attribute;
		firePropertyChange("attribute", oldValue, this.attribute);
	}

	public String getAttribute() {
		return attribute;
	}

}
