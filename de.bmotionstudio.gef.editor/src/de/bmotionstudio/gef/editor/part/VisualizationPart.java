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
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.editpolicies.RootComponentEditPolicy;
import org.eclipse.gef.editpolicies.SnapFeedbackPolicy;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.AppDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.AppEditLayoutPolicy;
import de.bmotionstudio.gef.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.gef.editor.figure.VisualizationFigure;
import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.library.LibraryImageCommand;
import de.bmotionstudio.gef.editor.library.LibraryVariableCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class VisualizationPart extends AppAbstractEditPart {

	public static String ID = "de.bmotionstudio.gef.editor.visualization";

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildrenArray();
	}

	@Override
	protected IFigure createEditFigure() {
		return new VisualizationFigure();
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new AppEditLayoutPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.CONTAINER_ROLE, new SnapFeedbackPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new RootComponentEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	@Override
	public void refreshEditFigure(final IFigure figure, final BControl model,
			final PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR)) {
			RGB rgbBG = (RGB) value;
			((VisualizationFigure) figure)
					.setBackgroundColor(new org.eclipse.swt.graphics.Color(
							Display.getDefault(), rgbBG.red, rgbBG.green,
							rgbBG.blue));
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object getAdapter(final Class adapter) {
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
			Boolean val = (Boolean) getViewer().getProperty(
					RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGuides(this));
			}
			val = (Boolean) getViewer().getProperty(
					SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGeometry(this));
			}
			val = (Boolean) getViewer().getProperty(
					SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue()) {
				snapStrategies.add(new SnapToGrid(this));
			}

			if (snapStrategies.size() == 0) {
				return null;
			}
			if (snapStrategies.size() == 1) {
				return snapStrategies.get(0);
			}

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++) {
				ss[i] = (SnapToHelper) snapStrategies.get(i);
			}
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}

	@Override
	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		AbstractLibraryCommand command = null;
		if (request.getAttributeTransferObject().getLibraryObject().getType()
				.equals("variable")) {
			command = new LibraryVariableCommand();
		} else if (request.getAttributeTransferObject().getLibraryObject()
				.getType().equals("image")) {
			command = new LibraryImageCommand();
		}
		return command;
	}

}
