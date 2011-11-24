/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.eventb;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.IEventBProject;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;

public class FileInputValidator implements IInputValidator {
	private final IEventBProject prj;

	FileInputValidator(final IRodinProject prj) {
		this.prj = (IEventBProject) prj.getAdapter(IEventBProject.class);
	}

	public String isValid(final String newText) {
		if (newText.equals("")) {
			return "Name must not be empty.";
		}
		IRodinFile file = prj.getMachineFile(newText + "_mch");
		if (file != null && file.exists()) {
			return "File name " + newText + " already exists.";
		}
		file = prj.getContextFile(newText + "_ctx");
		if (file != null && file.exists()) {
			return "File name " + newText + " already exists.";
		}
		return null;
	}
}
