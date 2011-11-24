/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;

import de.bmotionstudio.gef.editor.internal.StartVisualizationFileHandler;

/**
 * @author Lukas Ladenberger
 * 
 */
public class StartEventBVisualizationHandler extends
		StartVisualizationFileHandler implements IHandler {

	@Override
	protected IFile getBmsFileFromSelection(IStructuredSelection ssel) {
		if (ssel.getFirstElement() instanceof IBMotionSurfaceRoot)
			return ((IBMotionSurfaceRoot) ssel.getFirstElement()).getResource();
		return null;
	}

}
