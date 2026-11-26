/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.rodinp.core.IRodinProject;

import de.prob.eventb.translator.TheoryMappingFiles;

/**
 * Shows the theory mapping files (.ptm) of a project in the Event-B Explorer
 * (as {@link MappingFileNode}s),
 * after its machines, contexts and theories: ProB reads <code>T.ptm</code> in
 * the project of a theory <code>T</code> (see {@link TheoryMappingFiles}). The
 * explorer is refreshed when such a file is added or removed.
 */
public class MappingFilesContentProvider implements ITreeContentProvider,
		IResourceChangeListener {

	private static final Object[] NONE = new Object[0];

	private StructuredViewer viewer;

	public MappingFilesContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	/** @return the .ptm files directly in the project, by name */
	static List<IFile> files(final IProject project) {
		final List<IFile> result = new ArrayList<IFile>();
		if (project == null || !project.isAccessible())
			return result;
		try {
			for (IResource member : project.members())
				if (member instanceof IFile && isMappingFile(member))
					result.add((IFile) member);
		} catch (CoreException e) {
			return result;
		}
		result.sort(Comparator.comparing(IFile::getName,
				String.CASE_INSENSITIVE_ORDER));
		return result;
	}

	static boolean isMappingFile(final IResource resource) {
		return TheoryMappingFiles.EXTENSION.equals(resource
				.getFileExtension());
	}

	private static IProject project(final Object element) {
		if (element instanceof IRodinProject)
			return ((IRodinProject) element).getProject();
		if (element instanceof IProject)
			return (IProject) element;
		return null;
	}

	@Override
	public Object[] getChildren(final Object element) {
		final IProject project = project(element);
		if (project == null)
			return NONE;
		final List<IFile> files = files(project);
		final Object[] nodes = new Object[files.size()];
		for (int i = 0; i < nodes.length; i++)
			nodes[i] = new MappingFileNode(files.get(i));
		return nodes;
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof MappingFileNode)
			return ((MappingFileNode) element).getFile().getProject();
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		final IProject project = project(element);
		return project != null && !files(project).isEmpty();
	}

	@Override
	public Object[] getElements(final Object input) {
		return getChildren(input);
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		this.viewer = viewer instanceof StructuredViewer ? (StructuredViewer) viewer
				: null;
	}

	@Override
	public void resourceChanged(final IResourceChangeEvent event) {
		final IResourceDelta delta = event.getDelta();
		if (delta != null && addsOrRemovesMappingFile(delta))
			refreshLater();
	}

	/** a .ptm file directly in a project was added or removed */
	private static boolean addsOrRemovesMappingFile(final IResourceDelta delta) {
		for (IResourceDelta project : delta.getAffectedChildren())
			for (IResourceDelta file : project.getAffectedChildren(
					IResourceDelta.ADDED | IResourceDelta.REMOVED))
				if (isMappingFile(file.getResource()))
					return true;
		return false;
	}

	private void refreshLater() {
		final StructuredViewer viewer = this.viewer;
		if (viewer == null)
			return;
		final Control control = viewer.getControl();
		if (control == null || control.isDisposed())
			return;
		control.getDisplay().asyncExec(() -> {
			if (!control.isDisposed())
				viewer.refresh();
		});
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		viewer = null;
	}
}
