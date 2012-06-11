/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;

public class CreateCommand extends Command {

	private BControl parent;
	private BControl child;
	private int index = -1;

	public CreateCommand(BControl child, BControl parent) {
		super();
		this.parent = parent;
		this.child = child;
	}

	public void setLayout(Rectangle r) {
		if (child == null)
			return;
		child.setLayout(r);
	}

	@Override
	public boolean canExecute() {
		if (parent == null || child == null)
			return false;
		return true;
	}

	@Override
	public void execute() {
		parent.addChild(child, index);
	}

	@Override
	public boolean canUndo() {
		if (parent == null || child == null)
			return false;
		return parent.contains(child);
	}

	@Override
	public void undo() {
		parent.removeChild(child);
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
