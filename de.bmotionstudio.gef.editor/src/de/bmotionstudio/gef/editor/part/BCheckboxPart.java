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
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.gef.editor.edit.TextEditManager;
import de.bmotionstudio.gef.editor.editpolicy.AppDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMotionNodeEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.gef.editor.figure.CheckboxFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BCheckboxPart extends AppAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		CheckboxFigure fig = new CheckboxFigure();
		return fig;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent pEvent) {

		Object value = pEvent.getNewValue();
		String aID = pEvent.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((CheckboxFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_CHECKED)) {
			Boolean bol = Boolean.valueOf(value.toString());
			if (bol) {
				((CheckboxFigure) figure).setImage(BMotionStudioImage
						.getImage(EditorImageRegistry.IMG_ICON_CHECKED));
			} else {
				((CheckboxFigure) figure).setImage(BMotionStudioImage
						.getImage(EditorImageRegistry.IMG_ICON_UNCHECKED));
			}

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT)) {
			int addWidth = ((CheckboxFigure) figure).setText(value.toString());
			((BControl) getModel()).setAttributeValue(
					AttributeConstants.ATTRIBUTE_WIDTH, (30 + addWidth));
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR)) {
			RGB rgbText = (RGB) value;
			((CheckboxFigure) figure)
					.setTextColor(new org.eclipse.swt.graphics.Color(Display
							.getDefault(), rgbText));
		}

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new RenamePolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMotionNodeEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	private void performDirectEdit() {
		new TextEditManager(this, new TextCellEditorLocator(
				(IFigure) getFigure())).show();
	}

	@Override
	public void performRequest(Request request) {
		super.performRequest(request);
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT
				&& !isRunning())
			performDirectEdit();
	}

}
