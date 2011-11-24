/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;

public class DeleteCommand extends Command {

	private BControl control;
	private BControl parent;

	public DeleteCommand(BControl control, BControl parent) {
		this.control = control;
		this.parent = parent;
	}

	@Override
	public boolean canExecute() {
		if (parent == null || control == null || !parent.contains(control))
			return false;
		return true;
	}

	@Override
	public boolean canUndo() {
		if (parent == null || control == null)
			return false;
		return !parent.contains(control);
	}

	@Override
	public void execute() {
		this.parent.removeChild(control);
	}

	@Override
	public void undo() {
		this.parent.addChild(control);
	}

}
