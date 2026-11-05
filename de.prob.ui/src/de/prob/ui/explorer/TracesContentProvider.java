package de.prob.ui.explorer;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eventb.core.IEventBRoot;

import de.prob.ui.historyview.SaveTraceHandler;

/**
 * Adds a "Traces" node with the saved traces (see {@link TraceFiles}) to each
 * machine and context with traces in the Event-B Explorer. The explorer is
 * refreshed when a trace file is added, changed or removed.
 */
public class TracesContentProvider implements ITreeContentProvider,
		IResourceChangeListener {

	private static final Object[] NONE = new Object[0];

	private StructuredViewer viewer;

	public TracesContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this,
				IResourceChangeEvent.POST_CHANGE);
	}

	@Override
	public Object[] getChildren(final Object element) {
		if (element instanceof IEventBRoot) {
			final IEventBRoot root = (IEventBRoot) element;
			return TraceFiles.of(root).isEmpty() ? NONE
					: new Object[] { new TracesNode(root) };
		}
		if (element instanceof TracesNode) {
			final TracesNode node = (TracesNode) element;
			final List<IFile> files = TraceFiles.of(node.getRoot());
			final Object[] children = new Object[files.size()];
			for (int i = 0; i < children.length; i++)
				children[i] = new TraceNode(node, files.get(i));
			return children;
		}
		return NONE;
	}

	@Override
	public Object getParent(final Object element) {
		if (element instanceof TracesNode)
			return ((TracesNode) element).getRoot();
		if (element instanceof TraceNode)
			return ((TraceNode) element).getParent();
		return null;
	}

	@Override
	public boolean hasChildren(final Object element) {
		if (element instanceof IEventBRoot)
			return !TraceFiles.of((IEventBRoot) element).isEmpty();
		return element instanceof TracesNode;
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
		if (delta == null || viewer == null || !touchesTraces(delta))
			return;
		final Control control = viewer.getControl();
		if (control == null || control.isDisposed())
			return;
		control.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (!control.isDisposed())
					viewer.refresh();
			}
		});
	}

	/** whether a trace file in a project's traces folder was changed */
	private static boolean touchesTraces(final IResourceDelta delta) {
		final boolean[] found = { false };
		try {
			delta.accept(new IResourceDeltaVisitor() {
				@Override
				public boolean visit(final IResourceDelta d) {
					if (found[0])
						return false;
					final String name = d.getResource().getName();
					if (name.endsWith(SaveTraceHandler.EXTENSION)
							&& d.getResource().getParent() != null
							&& SaveTraceHandler.FOLDER.equals(d.getResource()
									.getParent().getName())) {
						found[0] = true;
						return false;
					}
					// only the workspace, projects and their traces folders
					return d.getResource().getParent() == null
							|| d.getResource().getParent().getParent() == null
							|| SaveTraceHandler.FOLDER.equals(name);
				}
			});
		} catch (CoreException e) {
			return false;
		}
		return found[0];
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		viewer = null;
	}
}
