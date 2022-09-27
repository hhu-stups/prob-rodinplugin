/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.library;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;

/**
 * @author Lukas Ladenberger
 * 
 */
public abstract class AbstractLibraryCommand extends Command {

	protected EditPart editPart;
	protected AttributeTransferObject transferObject;
	protected String attributeName;
	protected Object attributeValue;
	protected Object oldAttributeValue;
	protected Point dropLocation;

	public boolean canExecute() {
		return true;
	}

	public void setEditPart(EditPart editPart) {
		this.editPart = editPart;
	}

	public EditPart getEditPart() {
		return this.editPart;
	}

	public void undo() {
		if (attributeName != null)
			getCastedModel()
					.setAttributeValue(attributeName, oldAttributeValue);
	}

	public void setAttributeTransferObject(
			AttributeTransferObject transferObject) {
		this.transferObject = transferObject;
	}

	public AttributeTransferObject getAttributeTransferObject() {
		return this.transferObject;
	}

	public Point getDropLocation() {
		return dropLocation;
	}

	public void setDropLocation(Point dropLocation) {
		this.dropLocation = dropLocation;
	}

	protected BControl getCastedModel() {
		return (BControl) editPart.getModel();
	}

}
