/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.command.CreateCommand;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.SignalLayoutEditPolicy;
import de.bmotionstudio.gef.editor.figure.SignalFigure;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Light;

public class SignalPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new SignalFigure();
	}

	@Override
	protected void refreshEditLayout(IFigure figure, BControl control) {

		int lights = Integer.valueOf(control.getAttributeValue(
				AttributeConstants.ATTRIBUTE_LIGHTS).toString());

		// Set the correct size of the table
		figure.getParent().setConstraint(
				figure,
				new Rectangle(control.getLocation().x, control.getLocation().y,
						control.getDimension().width, lights * 12 + 30));

	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		Object oldValue = evt.getOldValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_LIGHTS)) {

			if (oldValue == null || value.equals(oldValue))
				return;

			// Create lights
			Integer numberOfLights = Integer.valueOf(value.toString());
			Integer numberOfCurrentLights = Integer
					.valueOf(oldValue.toString());

			if (numberOfLights < numberOfCurrentLights) {
				for (int i = numberOfCurrentLights - 1; i >= numberOfLights; i--) {
					model.removeChild(i);
				}
			}

			for (int i = numberOfCurrentLights; i < numberOfLights; i++) {
				Light light = new Light(model.getVisualization());
				CreateCommand cmd = new CreateCommand(light, model);
				cmd.execute();
			}

			refreshEditLayout(figure, model);

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_ID)) {
			((SignalFigure) getFigure()).setLabel(value.toString());
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_TRACK_DIRECTION)) {
			int direction = Integer.valueOf(value.toString());
			if (direction == 1) {
				((SignalFigure) getFigure()).setTrackDirection(false);
			} else {
				((SignalFigure) getFigure()).setTrackDirection(true);
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((SignalFigure) figure)
					.setVisible(Boolean.valueOf(value.toString()));

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new SignalLayoutEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildrenArray();
	}

}
