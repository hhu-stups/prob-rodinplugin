/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;


public class SetAttributeObject extends ObserverEvalObject implements Cloneable {

	public SetAttributeObject() {
		super();
	}

	public SetAttributeObject(String type, String eval) {
		super(type, eval);
	}

	public SetAttributeObject clone() throws CloneNotSupportedException {
		return (SetAttributeObject) super.clone();
	}

}
