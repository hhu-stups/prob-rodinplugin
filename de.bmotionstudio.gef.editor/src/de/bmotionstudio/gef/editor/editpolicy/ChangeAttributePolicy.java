/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.AbstractEditPolicy;

import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;

public class ChangeAttributePolicy extends AbstractEditPolicy {

	public static final String CHANGE_ATTRIBUTE_POLICY = "ChangeAttributePolicy";

	@Override
	public Command getCommand(Request request) {
		if ("change attribute" == request.getType()
				&& request instanceof AttributeRequest) {
			AttributeRequest changeAttributeReq = (AttributeRequest) request;
			if (changeAttributeReq.getAttributeTransferObject() != null) {
				AbstractLibraryCommand command = ((BMSAbstractEditPart) getHost())
						.getLibraryCommand(changeAttributeReq);
				if (command != null) {
					command.setEditPart(getHost());
					command.setAttributeTransferObject(changeAttributeReq
							.getAttributeTransferObject());
					command.setDropLocation(changeAttributeReq
							.getDropLocation());
				}
				return command;
			}
		}
		return null;
	}

	public EditPart getTargetEditPart(Request request) {
		if ("change attribute" == request.getType())
			return getHost();
		return null;
	}

}
