/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.figure.SignalFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class SignalPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new SignalFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();
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

		if (aID.equals(AttributeConstants.ATTRIBUTE_SIGNAL_COLOR)) {
			int color = Integer.valueOf(value.toString());
			((SignalFigure) getFigure()).setSignalColor(color);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bmotionstudio.gef.editor.part.AppAbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
		super.createEditPolicies();
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
