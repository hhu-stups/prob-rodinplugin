/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.jface.dialogs.IInputValidator;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;

public class BMotionFileInputValidator implements IInputValidator {

	private IRodinProject prj;

	public BMotionFileInputValidator(IRodinProject prj) {
		this.prj = prj;
	}

	public String isValid(String newText) {
		IRodinFile rodinFile = prj.getRodinFile(newText + "."
				+ BMotionEditorPlugin.FILEEXT_STUDIO);
		if (rodinFile != null && rodinFile.exists())
			return "The BMotion-Project filename must be unique in a project.";
		return null;
	}

}
