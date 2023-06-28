/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.eventb;

import org.eventb.core.ast.Type;

import de.bmotionstudio.gef.editor.BindingObject;


public class MachineContentObject extends BindingObject {

	private String label;
	private Type type;

	public MachineContentObject(String label) {
		this.setLabel(label);
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public String toString() {
		return this.label;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

}
