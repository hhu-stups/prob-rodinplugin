/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

public class LibraryImageObject extends LibraryObject {

	public LibraryImageObject(String name, String type, Image typeImage) {
		super(name, type, typeImage);
	}

	@Override
	public void delete(LibraryPage page) {

		try {
			IFolder imageFolder = page.getEditor().getVisualization()
					.getProjectFile().getProject().getFolder("images");
			if (imageFolder.exists()) {
				IFile file = imageFolder.getFile(getName());
				if (file.exists())
					file.delete(true, new NullProgressMonitor());
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public Image getPreview(LibraryPage page) {
		IFile pFile = page.getEditor().getVisualization().getProjectFile();
		if (pFile != null) {
			String myPath = (pFile.getProject().getLocation() + "/images/" + getName())
					.replace("file:", "");
			return new Image(Display.getDefault(), myPath);
		} else {
			return null;
		}
	}

}
