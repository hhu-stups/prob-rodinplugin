/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.figure.CanisterFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BCanisterPart extends AppAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new CanisterFigure();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {
		((CanisterFigure) figure).setAlpha(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_ALPHA)
				.toString()));
		((CanisterFigure) figure).setFillColor((RGB) model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_FILL_COLOR));
		((CanisterFigure) figure).setFillHeight(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_FILL_HEIGHT)
				.toString()));
		((CanisterFigure) figure).setMaxPos(Integer.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_MEASURE_MAXPOS)
				.toString()));
		((CanisterFigure) figure).setInterval(Integer.valueOf(model
				.getAttributeValue(
						AttributeConstants.ATTRIBUTE_MEASURE_INTERVAL)
				.toString()));
		((CanisterFigure) figure).setMeasure(Boolean.valueOf(model
				.getAttributeValue(AttributeConstants.ATTRIBUTE_SHOWS_MEASURE)
				.toString()));
		((CanisterFigure) figure)
				.setBackgroundColor((RGB) model
						.getAttributeValue(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR));
	}

	@Override
	protected void prepareEditPolicies() {
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
