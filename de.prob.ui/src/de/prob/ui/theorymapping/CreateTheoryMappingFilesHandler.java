/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;
import org.rodinp.core.IRodinElement;

/**
 * "Create ProB Theory Mapping Files" in the Event-B Explorer, for a project or
 * an element of it (e.g. a machine, context or theory): see
 * {@link CreateTheoryMappingFiles}.
 */
public class CreateTheoryMappingFilesHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection))
			return null;
		final IProject project = project(((IStructuredSelection) selection)
				.getFirstElement());
		if (project != null)
			CreateTheoryMappingFiles.run(project);
		return null;
	}

	private static IProject project(final Object element) {
		if (element instanceof IProject)
			return (IProject) element;
		if (element instanceof MappingFileNode)
			return ((MappingFileNode) element).getFile().getProject();
		if (element instanceof IRodinElement)
			return ((IRodinElement) element).getRodinProject().getProject();
		final IResource resource = Adapters.adapt(element, IResource.class);
		return resource == null ? null : resource.getProject();
	}
}
