package de.prob.ui.explorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;

/**
 * A trace file below the "Traces" node of a machine or context in the
 * Event-B Explorer. It adapts to its file, e.g. for the Properties view.
 */
public final class TraceNode implements IAdaptable {

	private final TracesNode parent;
	private final IFile file;

	public TraceNode(final TracesNode parent, final IFile file) {
		this.parent = parent;
		this.file = file;
	}

	public TracesNode getParent() {
		return parent;
	}

	public IFile getFile() {
		return file;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		if (adapter.isInstance(file))
			return adapter.cast(file);
		return null;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof TraceNode && ((TraceNode) o).parent.equals(parent)
				&& ((TraceNode) o).file.equals(file);
	}

	@Override
	public int hashCode() {
		return 31 * parent.hashCode() + file.hashCode();
	}

	@Override
	public String toString() {
		return file.getName();
	}
}
