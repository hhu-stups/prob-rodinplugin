/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.bmotionstudio.gef.editor.handler.StartVisualizationFileHandler;

/**
 * @author Lukas Ladenberger
 * 
 */
public class StartEventBVisualizationHandler extends
		StartVisualizationFileHandler implements IHandler {

	@Override
	protected IFile getBmsFileFromSelection(IStructuredSelection ssel) {
		if (ssel.getFirstElement() instanceof BMotionStudioRodinFile) {
			IResource resource = ((BMotionStudioRodinFile) ssel
					.getFirstElement()).getResource();
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IPath location = Path.fromOSString(resource.getFullPath()
					.toOSString());
			return workspace.getRoot().getFileForLocation(location);
		}
		return null;
	}

}
