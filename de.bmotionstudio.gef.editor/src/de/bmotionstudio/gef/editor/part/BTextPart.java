/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.gef.editor.edit.TextEditManager;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.gef.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.gef.editor.figure.TextFigure;
import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.library.LibraryVariableCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class BTextPart extends BMSAbstractEditPart {

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT))
			((TextFigure) figure).setText(value.toString());

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((TextFigure) figure)
					.setBackgroundColor(new org.eclipse.swt.graphics.Color(
							Display.getDefault(), (RGB) value));

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR))
			((TextFigure) figure).setTextColor(((RGB) value));

		if (aID.equals(AttributeConstants.ATTRIBUTE_FONT))
			((TextFigure) figure).setFont((value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_VISIBLE))
			((TextFigure) figure).setBackgroundVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((TextFigure) figure).setVisible(Boolean.valueOf(value.toString()));

	}

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new TextFigure();
		return figure;
	}

	private void performDirectEdit() {
		new TextEditManager(this, new TextCellEditorLocator(
				(IFigure) getFigure())).show();
	}

	@Override
	public void performRequest(Request request) {
		super.performRequest(request);
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

	// @Override
	// public String getValueOfData() {
	// return ((BControl) getModel()).getAttributeValue(
	// AttributeConstants.ATTRIBUTE_CUSTOM).toString();
	// }

	@Override
	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		AbstractLibraryCommand command = null;
		if (request.getAttributeTransferObject().getLibraryObject().getType()
				.equals("variable")) {
			command = new LibraryVariableCommand();
		}
		return command;
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new RenamePolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
