/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.TrackEditPolicy;
import de.bmotionstudio.gef.editor.figure.TrackNodeFigure;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.TrackNode;

public class TrackNodePart extends BMSAbstractEditPart {

	protected Color foregroundColor;

	@Override
	protected IFigure createEditFigure() {
		return new TrackNodeFigure();
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_LINEWIDTH))
			((TrackNodeFigure) getFigure()).setLineWidth(Integer
					.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_LINESTYLE))
			((TrackNodeFigure) getFigure()).setLineStyle((Integer
					.valueOf(value.toString()) + 1));

		if (aID.equals(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR)) {
			if (foregroundColor != null)
				foregroundColor.dispose();
			foregroundColor = new Color(Display.getDefault(), (RGB) value);
			((TrackNodeFigure) getFigure())
					.setForegroundColor(foregroundColor);
		}

	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new TrackEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	@Override
	protected List<?> getModelSourceConnections() {
		List<BControl> all = new ArrayList<BControl>();
		all.addAll(((TrackNode) getModel()).getSourceTracks());
		all.addAll(((TrackNode) getModel()).getSourceConnections());
		return all;
	}

	@Override
	protected List<?> getModelTargetConnections() {
		List<BControl> all = new ArrayList<BControl>();
		all.addAll(((TrackNode) getModel()).getTargetTracks());
		all.addAll(((TrackNode) getModel()).getTargetConnections());
		return all;
	}

}
