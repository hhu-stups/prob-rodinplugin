/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;

import de.bmotionstudio.gef.editor.command.RenameCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.part.AppAbstractEditPart;

public class CustomDirectEditPolicy extends DirectEditPolicy {

	/**
	 * @see CustomDirectEditPolicy#getDirectEditCommand(DirectEditRequest)
	 */
	protected Command getDirectEditCommand(DirectEditRequest edit) {
		String labelText = (String) edit.getCellEditor().getValue();
		AppAbstractEditPart label = (AppAbstractEditPart) getHost();
		RenameCommand command = new RenameCommand();
		command.setControl((BControl) label.getModel());
		command.setNewString(labelText);
		return command;
	}

	/**
	 * @see CustomDirectEditPolicy#showCurrentEditValue(DirectEditRequest)
	 */
	protected void showCurrentEditValue(DirectEditRequest request) {
		// String value = (String) request.getCellEditor().getValue();
		getHostFigure().getUpdateManager().performUpdate();
	}

}
