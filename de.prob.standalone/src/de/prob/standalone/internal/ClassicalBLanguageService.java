package de.prob.standalone.internal;

import java.io.File;

import org.eclipse.core.resources.IFile;

import de.bmotionstudio.gef.editor.ILanguageService;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.Animator;
import de.prob.core.command.LoadClassicalBModelCommand;
import de.prob.exceptions.ProBException;

/**
 * @author Lukas Ladenberger
 * 
 */
public class ClassicalBLanguageService implements ILanguageService {

	public void startProBAnimator(Visualization v) throws ProBException {
		IFile f = v.getProjectFile().getProject().getFile(v.getMachineName());
		String n = f.getName();
		String name = n.substring(0, n.length() - 4);
		File file = new File(f.getLocationURI());
		LoadClassicalBModelCommand.load(Animator.getAnimator(), file, name);
	}

	public boolean isLanguageFile(IFile f) {
		if ("mch".equals(f.getFileExtension()))
			return true;
		return false;
	}

}
