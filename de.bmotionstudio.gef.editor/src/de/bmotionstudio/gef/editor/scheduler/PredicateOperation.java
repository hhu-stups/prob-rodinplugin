/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;

public class PredicateOperation extends ObserverEvalObject implements Cloneable {

	private String operationName;
	private String executePredicate;
	private String predicate;
	private boolean random;
	private int maxrandom = 1;

	public PredicateOperation() {
		this("", "");
	}

	public PredicateOperation(String operationName, String predicate) {
		this.operationName = operationName;
		this.predicate = predicate;
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

	public void setRandom(boolean random) {
		Object oldValue = this.random;
		this.random = random;
		firePropertyChange("random", oldValue, this.random);
	}

	public boolean isRandom() {
		return random;
	}

	public boolean getIsRandom() {
		return isRandom();
	}

	public void setMaxrandom(int maxrandom) {
		Object oldValue = this.maxrandom;
		this.maxrandom = maxrandom;
		firePropertyChange("maxrandom", oldValue, this.maxrandom);
	}

	public int getMaxrandom() {
		return maxrandom;
	}

	public PredicateOperation clone() throws CloneNotSupportedException {
		return (PredicateOperation) super.clone();
	}

	public void setExecutePredicate(String executePredicate) {
		Object oldValue = this.executePredicate;
		this.executePredicate = executePredicate;
		firePropertyChange("executePredicate", oldValue, this.executePredicate);
	}

	public String getExecutePredicate() {
		return executePredicate;
	}

}
