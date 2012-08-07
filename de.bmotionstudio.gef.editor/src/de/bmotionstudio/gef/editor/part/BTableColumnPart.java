package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMotionNodeEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMotionStudioFlowEditPolicy;
import de.bmotionstudio.gef.editor.figure.TableColumnFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BTableColumnPart extends AppAbstractEditPart {

	@Override
	protected IFigure createEditFigure() {
		return new TableColumnFigure();
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new BMotionStudioFlowEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMotionNodeEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		// if (aID.equals(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR)) {
		// for (BControl cell : model.getChildrenArray())
		// cell.setAttributeValue(
		// AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR, value);
		// }
		//
		if (aID.equals(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR)) {
			((TableColumnFigure) figure).setForegroundColor((RGB) value);
			for (BControl cell : model.getChildrenArray())
				cell.setAttributeValue(
						AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR, value);
		}
		//
		// if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT_COLOR)) {
		// for (BControl cell : model.getChildrenArray())
		// cell.setAttributeValue(AttributeConstants.ATTRIBUTE_TEXT_COLOR,
		// value);
		// }
		//
		// if (aID.equals(AttributeConstants.ATTRIBUTE_FONT)) {
		// for (BControl cell : model.getChildrenArray())
		// cell.setAttributeValue(AttributeConstants.ATTRIBUTE_FONT, value);
		// }

	}

	@Override
	protected void refreshEditLayout(IFigure figure, BControl control) {
		if (getParent() instanceof AppAbstractEditPart) {
			AppAbstractEditPart tablePart = (AppAbstractEditPart) getParent();
			tablePart.refreshEditLayout(tablePart.getFigure(),
					control.getParent());
		}
		super.refreshEditLayout(figure, control);
	}

	@Override
	public List<BControl> getModelChildren() {
		return ((BControl) getModel()).getChildrenArray();
	}

}
