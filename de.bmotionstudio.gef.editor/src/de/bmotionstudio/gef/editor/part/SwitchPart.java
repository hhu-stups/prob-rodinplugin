/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AttributeSwitchDirection;
import de.bmotionstudio.gef.editor.attribute.AttributeSwitchPosition;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.figure.SwitchFigure;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Switch;
import de.bmotionstudio.gef.editor.model.Track;

public class SwitchPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new SwitchFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();
		Switch sw = (Switch) model;
		
		Track track1 = sw.getTrack1();
		Track track2 = sw.getTrack2();

		if (aID.equals(AttributeConstants.ATTRIBUTE_SWITCH_POSITION)) {
			if (track1 != null && track2 != null) {
				track1.setAttributeValue(AttributeConstants.ATTRIBUTE_VISIBLE,
						true);
				track2.setAttributeValue(AttributeConstants.ATTRIBUTE_VISIBLE,
						true);
				if (value.equals(AttributeSwitchPosition.LEFT)) {
					track1.setAttributeValue(
							AttributeConstants.ATTRIBUTE_VISIBLE, false);
				} else if (value.equals(AttributeSwitchPosition.RIGHT)) {
					track2.setAttributeValue(
							AttributeConstants.ATTRIBUTE_VISIBLE, false);
				}
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_SWITCH_DIRECTION))
			refreshEditLayout(figure, model);

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE)) {
			if (track1 != null && track2 != null) {
				Boolean visible = Boolean.valueOf(value.toString());
				((SwitchFigure) figure).setVisible(visible);
				track1.setAttributeValue(
						AttributeConstants.ATTRIBUTE_VISIBLE, visible);
				track2.setAttributeValue(
						AttributeConstants.ATTRIBUTE_VISIBLE, visible);
			}
		}

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildrenArray();
	}

	@Override
	protected void refreshEditLayout(IFigure figure, BControl control) {

		int width = control.getDimension().width;
		int height = control.getDimension().height;

		List<BControl> trackNodes = control.getChildrenArray();

		int dir = Integer.valueOf(((BControl) getModel()).getAttributeValue(
				AttributeConstants.ATTRIBUTE_SWITCH_DIRECTION).toString());

		Point pt = new Point();

		for (BControl trackNode : trackNodes) {

			switch (Integer.valueOf(trackNode.getAttributeValue(
					AttributeConstants.ATTRIBUTE_CUSTOM).toString())) {
			case 1: // top left (1)
				if (dir == AttributeSwitchDirection.RIGHT_SOUTH) {
					pt.y = 0;
					pt.x = 5;
				} else if (dir == AttributeSwitchDirection.LEFT_SOUTH) {
					pt.y = 0;
					pt.x = width - 25;
				} else if (dir == AttributeSwitchDirection.RIGHT_NORTH) {
					pt.y = height - 20;
					pt.x = 5;
				} else if (dir == AttributeSwitchDirection.LEFT_NORTH) {
					pt.y = height - 20;
					pt.x = width - 25;
				}
				break;
			case 2: // top right (2)
				if (dir == AttributeSwitchDirection.RIGHT_SOUTH) {
					pt.y = 0;
					pt.x = width - 25;
				} else if (dir == AttributeSwitchDirection.LEFT_SOUTH) {
					pt.y = 0;
					pt.x = 5;
				} else if (dir == AttributeSwitchDirection.RIGHT_NORTH) {
					pt.y = 0;
					pt.x = width - 25;
				} else if (dir == AttributeSwitchDirection.LEFT_NORTH) {
					pt.y = 0;
					pt.x = 5;
				}
				break;
			case 3: // left right (3)
				if (dir == AttributeSwitchDirection.RIGHT_SOUTH) {
					pt.y = height - 20;
					pt.x = width - 25;
				} else if (dir == AttributeSwitchDirection.LEFT_SOUTH) {
					pt.y = height - 20;
					pt.x = 5;
				} else if (dir == AttributeSwitchDirection.RIGHT_NORTH) {
					pt.y = height - 20;
					pt.x = width - 25;
				} else if (dir == AttributeSwitchDirection.LEFT_NORTH) {
					pt.y = height - 20;
					pt.x = 5;
				}
				break;
			default:
				break;
			}

			trackNode.setAttributeValue(AttributeConstants.ATTRIBUTE_X, pt.x);
			trackNode.setAttributeValue(AttributeConstants.ATTRIBUTE_Y, pt.y);

		}

		super.refreshEditLayout(figure, control);

	}

}
