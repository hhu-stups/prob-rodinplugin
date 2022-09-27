/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.edit.TextCellEditorLocator;
import de.bmotionstudio.gef.editor.edit.TextEditManager;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.CustomDirectEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.RenamePolicy;
import de.bmotionstudio.gef.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.gef.editor.figure.TextfieldFigure;
import de.bmotionstudio.gef.editor.model.BControl;

public class BTextfieldPart extends BMSAbstractEditPart {

	private TextEditManager textEditManager;

	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void handleStateChanged(ChangeEvent event) {
			if (event.getPropertyName().equals(ButtonModel.PRESSED_PROPERTY)) {
				AbstractBMotionFigure f = (AbstractBMotionFigure) getFigure();
				if (f.getModel().isPressed()) {
					if (textEditManager == null)
						textEditManager = new TextEditManager(
								BTextfieldPart.this, new TextCellEditorLocator(
										(IFigure) getFigure())) {
							@Override
							protected void bringDown() {
								super.bringDown();
								((BControl) getModel()).getVisualization()
										.getAnimation().checkObserver();
							}

						};
					textEditManager.show();
				}
			}
		}
	};

	@Override
	protected IFigure createEditFigure() {
		TextfieldFigure figure = new TextfieldFigure();
		return figure;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_TEXT))
			((TextfieldFigure) figure).setText(value.toString());

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((TextfieldFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

	}

	private void performDirectEdit() {
		new TextEditManager(BTextfieldPart.this, new TextCellEditorLocator(
				(IFigure) getFigure())).show();
	}

	@Override
	public void performRequest(Request request) {
		super.performRequest(request);
		if (request.getType() == RequestConstants.REQ_DIRECT_EDIT)
			performDirectEdit();
	}

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
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.NODE_ROLE, new RenamePolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
	}

	@Override
	protected void prepareRunPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new CustomDirectEditPolicy());
	}

}
