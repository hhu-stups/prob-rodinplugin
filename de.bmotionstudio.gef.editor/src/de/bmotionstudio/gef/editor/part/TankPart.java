/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.figure.TankFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class TankPart extends BMSAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new TankFigure();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {
		((TankFigure) figure).setAlpha(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_ALPHA)
				.toString()));
		((TankFigure) figure).setFillColor((RGB) model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_FILL_COLOR));
		((TankFigure) figure).setFillHeight(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_FILL_HEIGHT)
				.toString()));
		((TankFigure) figure).setMaxPos(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_MEASURE_MAXPOS)
				.toString()));
		((TankFigure) figure).setInterval(Integer.valueOf(model
				.getAttributeValue(
						AttributeConstants.ATTRIBUTE_MEASURE_INTERVAL)
				.toString()));
		((TankFigure) figure).setMeasure(Boolean.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_SHOWS_MEASURE)
				.toString()));
		((TankFigure) figure)
				.setBackgroundColor((RGB) model
						.getAttributeValue(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR));
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
