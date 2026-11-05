package de.prob.ui.explorer;

import org.eclipse.core.resources.IFile;

/**
 * A trace file below the "Traces" node of a machine or context in the
 * Event-B Explorer. It deliberately does not adapt to its file: Eclipse's
 * resource actions would then open the file in an editor on a double-click,
 * instead of replaying it (see {@link TraceActionProvider}).
 */
public final class TraceNode {

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
