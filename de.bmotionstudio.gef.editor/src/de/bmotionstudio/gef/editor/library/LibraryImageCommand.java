/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.command.CreateCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BImage;

public class LibraryImageCommand extends AbstractLibraryCommand {

	private BControl newImageControl;

	public void execute() {

		attributeName = AttributeConstants.ATTRIBUTE_IMAGE;
		attributeValue = transferObject.getLibraryObject().getName();
		oldAttributeValue = getCastedModel().getAttributeValue(attributeName);

		if (getCastedModel().canHaveChildren()) {

			newImageControl = new BImage(getCastedModel().getVisualization());
			newImageControl.setAttributeValue(attributeName, attributeValue);

			CreateCommand createCommand = new CreateCommand(
					newImageControl, getCastedModel());

			String imagePath = attributeValue.toString();

			org.eclipse.swt.graphics.Rectangle imageBounds = null;
			Image img = null;

			IFile pFile = getCastedModel().getVisualization().getProjectFile();

			Rectangle fRect = new Rectangle(getDropLocation().x
					- getCastedModel().getLocation().x, getDropLocation().y
					- getCastedModel().getLocation().y, 100, 100);

			if (pFile != null) {
				final String myPath = (pFile.getProject().getLocation()
						+ "/images/" + imagePath).replace("file:", "");
				if (new File(myPath).exists() && imagePath.length() > 0) {
					img = new Image(Display.getCurrent(), myPath);
					imageBounds = img.getBounds();
				}
			}

			if (imageBounds != null) {
				fRect.width = imageBounds.width;
				fRect.height = imageBounds.height;
			}

			createCommand.setLayout(fRect);
			createCommand.execute();

		} else {

			if (getCastedModel().hasAttribute(
					AttributeConstants.ATTRIBUTE_IMAGE))
				getCastedModel().setAttributeValue(attributeName,
						attributeValue);

		}

	}

	public void undo() {
		super.undo();
		getCastedModel().removeChild(newImageControl);
	}

}
