/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

import de.bmotionstudio.gef.editor.command.RenameCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class RenamePolicy extends AbstractEditPolicy {

	@Override
	public Command getCommand(Request request) {
		if (request.getType().equals("rename"))
			return createRenameCommand(request);
		return null;
	}

	protected Command createRenameCommand(Request renameRequest) {
		RenameCommand command = new RenameCommand();
		command.setControl(((BControl) getHost().getModel()));
		command.setNewString(((String) renameRequest.getExtendedData().get(
				"newName")));
		return command;
	}

}
