/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.eventb;

import org.eclipse.core.resources.IFile;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IMachineRoot;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;

import de.bmotionstudio.gef.editor.ILanguageService;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.command.LoadEventBModelCommand;
import de.prob.exceptions.ProBException;

/**
 * @author Lukas Ladenberger
 * 
 */
public class EventBLanguageService implements ILanguageService {

	private IEventBRoot getCorrespondingFile(IFile file, String machineFileName) {
		IRodinProject rProject = RodinCore.valueOf(file.getProject());
		IRodinFile rFile = rProject.getRodinFile(machineFileName);
		IEventBRoot eventbRoot = (IEventBRoot) rFile.getRoot();
		return eventbRoot;
	}

	@Override
	public void startProBAnimator(Visualization v) throws ProBException {
		IEventBRoot modelRoot = getCorrespondingFile(v.getProjectFile(),
				v.getMachineName());
		LoadEventBModelCommand.load(v.getAnimation().getAnimator(), modelRoot);
	}

	@Override
	public boolean isLanguageFile(IFile f) {
		IRodinProject rProject = RodinCore.valueOf(f.getProject());
		IRodinFile rFile = rProject.getRodinFile(f.getName());
		if (rFile != null
				&& ((rFile.getRoot() instanceof IMachineRoot) || (rFile
						.getRoot() instanceof IContextRoot)))
			return true;
		return false;
	}

}
