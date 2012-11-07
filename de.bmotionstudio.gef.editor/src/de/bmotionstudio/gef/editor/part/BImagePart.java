/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.BMSConnectionEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMSDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.gef.editor.figure.BMSImageFigure;
import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.library.LibraryImageCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class BImagePart extends BMSAbstractEditPart {

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_IMAGE)) {
			if (value != null) {
				String imgPath = value.toString();
				if (imgPath.length() > 0) {
					IFile pFile = model.getVisualization().getProjectFile();
					String myPath = (pFile.getProject().getLocation()
							+ "/images/" + imgPath).replace("file:", "");
					((BMSImageFigure) figure).setImage(myPath);
				}
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((BMSImageFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_ALPHA))
			((BMSImageFigure) figure)
					.setAlpha(Integer.valueOf(value.toString()));

	}

	@Override
	protected IFigure createEditFigure() {
		IFigure figure = new BMSImageFigure();
		return figure;
	}

	@Override
	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		AbstractLibraryCommand command = null;
		if (request.getAttributeTransferObject().getLibraryObject().getType()
				.equals("image")) {
			command = new LibraryImageCommand();
		}
		return command;
	}

	@Override
	protected void prepareEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new BMSDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMSConnectionEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
