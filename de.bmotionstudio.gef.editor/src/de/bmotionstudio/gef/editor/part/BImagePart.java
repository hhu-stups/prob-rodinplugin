/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.editpolicy.AppDeletePolicy;
import de.bmotionstudio.gef.editor.editpolicy.BMotionNodeEditPolicy;
import de.bmotionstudio.gef.editor.editpolicy.ChangeAttributePolicy;
import de.bmotionstudio.gef.editor.figure.BMSImageFigure;
import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.library.LibraryImageCommand;
import de.bmotionstudio.gef.editor.model.BControl;

public class BImagePart extends AppAbstractEditPart {

	private HashMap<String, File> fileMap = new HashMap<String, File>();

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
					File file = null;
					if (fileMap.containsKey(myPath)) {
						file = fileMap.get(myPath);
					} else {
						file = new File(myPath);
						fileMap.put(myPath, file);
					}
					if (file.exists()) {
						((BMSImageFigure) figure).setImage(new Image(Display
								.getDefault(), myPath));
					}
				}
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_VISIBLE))
			((BMSImageFigure) figure).setVisible(Boolean.valueOf(value
					.toString()));

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
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AppDeletePolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new BMotionNodeEditPolicy());
		installEditPolicy(ChangeAttributePolicy.CHANGE_ATTRIBUTE_POLICY,
				new ChangeAttributePolicy());
	}

	@Override
	protected void prepareRunPolicies() {
	}

}
