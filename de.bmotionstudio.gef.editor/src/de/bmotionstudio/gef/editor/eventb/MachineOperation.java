/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.eventb;

import java.util.ArrayList;
import java.util.List;

public class MachineOperation extends MachineContentObject {

	private List<String> parameters;
	private List<String> guards;

	public MachineOperation(String label, List<String> parameters,
			List<String> guards) {
		super(label);
		this.parameters = parameters;
		this.guards = guards;
	}

	public MachineOperation(String label) {
		super(label);
	}

	public List<String> getParameters() {
		return this.parameters;
	}

	public List<String> getGuards() {
		return guards;
	}

	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}

	public void setGuards(ArrayList<String> guards) {
		this.guards = guards;
	}

}
