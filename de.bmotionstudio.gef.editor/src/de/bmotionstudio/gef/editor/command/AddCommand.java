/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import de.bmotionstudio.gef.editor.model.BControl;

public class AddCommand extends org.eclipse.gef.commands.Command {

	private BControl child;
	private BControl parent;
	private int index = -1;

	public AddCommand() {
		super("Add Control");
	}

	public void execute() {
		if (index < 0)
			parent.addChild(child);
		else
			parent.addChild(child, index);
	}

	public BControl getParent() {
		return parent;
	}

	public void redo() {
		if (index < 0)
			parent.addChild(child);
		else
			parent.addChild(child, index);
	}

	public void setChild(BControl subpart) {
		child = subpart;
	}

	public void setIndex(int i) {
		index = i;
	}

	public void setParent(BControl newParent) {
		parent = newParent;
	}

	public void undo() {
		parent.removeChild(child);
	}

}
