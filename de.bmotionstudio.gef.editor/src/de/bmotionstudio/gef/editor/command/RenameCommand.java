/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;

public class RenameCommand extends Command {

	private BControl control;
	private String oldString;
	private String newString;

	public void setControl(BControl control) {
		this.control = control;
	}

	public void setNewString(String newString) {
		this.newString = newString;
	}

	@Override
	public boolean canExecute() {
		return checkControl();
	}

	@Override
	public boolean canUndo() {
		if (oldString == null)
			return false;
		return checkControl();
	}

	private boolean checkControl() {
		if (control == null || newString == null
				|| !control.hasAttribute(AttributeConstants.ATTRIBUTE_TEXT))
			return false;
		return true;
	}

	@Override
	public void execute() {
		this.oldString = control.getAttributeValue(
				AttributeConstants.ATTRIBUTE_TEXT).toString();
		this.control.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
				newString);
	}

	@Override
	public void undo() {
		this.control.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT,
				oldString);
	}

}
