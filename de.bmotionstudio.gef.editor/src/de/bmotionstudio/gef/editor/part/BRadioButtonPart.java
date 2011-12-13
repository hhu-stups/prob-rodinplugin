/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.ButtonGroupHelper;
import de.bmotionstudio.gef.editor.ImageRegistry;
import de.bmotionstudio.gef.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.gef.editor.edit.TextEditManager;
import de.bmotionstudio.gef.editor.editpolicy.AppDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMotionNodeEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.gef.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.gef.editor.figure.RadioButtonFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BRadioButtonPart extends AppAbstractEditPart {

	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void handleStateChanged(ChangeEvent event) {

			if (event.getPropertyName().equals(ButtonModel.PRESSED_PROPERTY)) {

				BControl control = (BControl) getModel();

				// Recheck observer after click
				control.getVisualization().getAnimation().checkObserver();

				// Set correct image of Radiobutton
				String btgroupid = control.getAttributeValue(
						AttributeConstants.ATTRIBUTE_BUTTONGROUP).toString();
				if (!btgroupid.trim().equals("")) {
					Collection<BControl> btGroup = ButtonGroupHelper
							.getButtonGroup(btgroupid);
					for (BControl c : btGroup) {
						c.setAttributeValue(
								AttributeConstants.ATTRIBUTE_CHECKED, false);
					}
				}
				control.setAttributeValue(AttributeConstants.ATTRIBUTE_CHECKED,
						true);

			}

		}

	};

	@Override
	public void activate() {
		super.activate();
		if (isRunning()) {
			if (getFigure() instanceof AbstractBMotionFigure)
				((AbstractBMotionFigure) getFigure())
						.addChangeListener(changeListener);
		}
	}

	@Override
	public void deactivate() {
		if (isRunning()) {
			if (getFigure() instanceof AbstractBMotionFigure)
				((AbstractBMotionFigure) getFigure())
						.removeChangeListener(changeListener);
		}
		super.deactivate();
	}

	@Override
	protected IFigure createEditFigure() {
		RadioButtonFigure fig = new RadioButtonFigure();
		return fig;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent pEvent) {

		Object value = pEvent.getNewValue();
		String aID = pEvent.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((RadioButtonFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_CHECKED)) {
			Boolean bol = Boolean.valueOf(value.toString());
			if (bol) {
				((RadioButtonFigure) figure).setImage(BMotionStudioImage
						.getImage(ImageRegistry.IMG_RADIOBUTTON_CHECKED));
			} else {
				((RadioButtonFigure) figure).setImage(BMotionStudioImage
						.getImage(ImageRegistry.IMG_RADIOBUTTON_UNCHECKED));
			}

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT)) {
			int addWidth = ((RadioButtonFigure) figure).setText(value
					.toString());
			((BControl) getModel()).setAttributeValue(
					AttributeConstants.ATTRIBUTE_WIDTH, (30 + addWidth));
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR)) {
			RGB rgbText = (RGB) value;
			((RadioButtonFigure) figure).setTextColor(rgbText);
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_BUTTONGROUP)) {
			String btgroup = value.toString();
			if (!btgroup.trim().equals("")) {
				ButtonGroupHelper.addToButtonGroup(btgroup,
						(BControl) getModel());
			}
		}
		
		if (aID.equals(AttributeConstants.ATTRIBUTE_ENABLED))
			((RadioButtonFigure) figure).setBtEnabled(Boolean.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_ENABLED))
			((RadioButtonFigure) figure).setBtEnabled(Boolean.valueOf(value
					.toString()));

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
