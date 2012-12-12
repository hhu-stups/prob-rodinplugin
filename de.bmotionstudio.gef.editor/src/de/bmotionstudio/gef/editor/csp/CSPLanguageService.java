package de.bmotionstudio.gef.editor.csp;

import java.io.File;

import org.eclipse.core.resources.IFile;

import de.bmotionstudio.gef.editor.ILanguageService;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.prob.core.command.LoadCspModelCommand;
import de.prob.exceptions.ProBException;

public class CSPLanguageService implements ILanguageService {

	@Override
	public void startProBAnimator(Visualization v) throws ProBException {
		IFile f = v.getProjectFile().getProject().getFile(v.getMachineName());
		String n = f.getName();
		String name = n.substring(0, n.length() - 4);
		File file = new File(f.getLocationURI());
		LoadCspModelCommand.load(v.getAnimation().getAnimator(), file, name);
	}

	@Override
	public boolean isLanguageFile(IFile f) {
		if (f.getFileExtension().equals("csp"))
			return true;
		return false;
	}

}
