/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.figure.ShapeFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BShapePart extends BMSAbstractEditPart {

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR))
			((ShapeFigure) figure).setBackgroundColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR))
			((ShapeFigure) figure).setForegroundColor((RGB) value);

		if (aID.equals(AttributeConstants.ATTRIBUTE_ALPHA))
			((ShapeFigure) figure).setAlpha(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_OUTLINEALPHA))
			((ShapeFigure) figure)
					.setOutlineAlpha(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((ShapeFigure) figure).setVisible(Boolean.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_SHAPE))
			((ShapeFigure) figure).setShape(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_ORIENTATION))
			((ShapeFigure) figure).setOrientation(Integer.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_FILLTYPE))
			((ShapeFigure) figure).setFillType(Integer.valueOf(value.toString()));

		// /** North */
		// int NORTH = 1;
		// /** South */
		// int SOUTH = 4;
		// /** West */
		// int WEST = 8;
		// /** East */
		// int EAST = 16;

		if (aID.equals(AttributeConstants.ATTRIBUTE_DIRECTION)) {

			int direction = Integer.valueOf(value.toString());
			int fval = 1;

			switch (direction) {
			case 0:
				fval = 1;
				break;
			case 1:
				fval = 4;
				break;
			case 2:
				fval = 8;
				break;
			case 3:
				fval = 16;
				break;
			default:
				break;
			}

			((ShapeFigure) figure).setDirection(fval);

		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_IMAGE)) {
			Image img = null;
			if (value != null) {
				String imgPath = value.toString();
				if (imgPath.length() > 0) {
					IFile pFile = model.getVisualization().getProjectFile();
					String myPath = (pFile.getProject().getLocation()
							+ "/images/" + imgPath).replace("file:", "");
					if (new File(myPath).exists()) {
						img = new Image(Display.getDefault(), myPath);
					}
				}
			}
			((ShapeFigure) figure).setImage(img);
		}

	}

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new ShapeFigure();
		return figure;
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
