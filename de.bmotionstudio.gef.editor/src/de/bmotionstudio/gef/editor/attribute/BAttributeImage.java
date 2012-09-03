/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.ImagePropertyDescriptor;

public class BAttributeImage extends AbstractAttribute {

	public PropertyDescriptor preparePropertyDescriptor() {
		return new ImagePropertyDescriptor(getID(), getName());
	}

	public String validateValue(Object value, BControl control) {
		if (value != null) {
			String fImage = value.toString();
			IFile pFile = control.getVisualization().getProjectFile();
			String myPath = (pFile.getProject().getLocation() + "/images/" + fImage)
					.replace("file:", "");
			if (!new File(myPath).exists()) {
				return "No such image in your library: " + fImage;
			}
		}
		return null;
	}

	@Override
	public String getName() {
		return "Image";
	}

}
