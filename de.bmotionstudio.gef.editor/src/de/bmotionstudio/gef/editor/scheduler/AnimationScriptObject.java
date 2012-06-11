/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import java.util.Vector;

import de.bmotionstudio.gef.editor.BindingObject;

public class AnimationScriptObject extends BindingObject implements Cloneable {

	private Vector<AnimationScriptStep> steps;

	private String predicate;

	public AnimationScriptObject() {
	}
	
	public AnimationScriptObject(String predicate) {
		this(predicate, new Vector<AnimationScriptStep>());
	}

	public AnimationScriptObject(String predicate,
			Vector<AnimationScriptStep> steps) {
		this.predicate = predicate;
		this.steps = steps;
	}

	public void setSteps(Vector<AnimationScriptStep> steps) {
		Object oldValue = this.steps;
		this.steps = steps;
		firePropertyChange("steps", oldValue, this.steps);
	}

	public Vector<AnimationScriptStep> getSteps() {
		if (this.steps == null)
			this.steps = new Vector<AnimationScriptStep>();
		return this.steps;
	}

	public void setPredicate(String predicate) {
		Object oldValue = this.predicate;
		this.predicate = predicate;
		firePropertyChange("predicate", oldValue, this.predicate);
	}

	public String getPredicate() {
		return predicate;
	}

	public AnimationScriptObject clone() throws CloneNotSupportedException {
		Vector<AnimationScriptStep> tmpVector = new Vector<AnimationScriptStep>();
		for (AnimationScriptStep p : getSteps()) {
			tmpVector.add(p.clone());
		}
		AnimationScriptObject tmpObj = (AnimationScriptObject) super.clone();
		tmpObj.setSteps(tmpVector);
		return tmpObj;
	}

}
