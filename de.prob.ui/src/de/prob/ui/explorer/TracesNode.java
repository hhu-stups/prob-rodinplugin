package de.prob.ui.explorer;

import org.eventb.core.IEventBRoot;

/**
 * The "Traces" node of a machine or context in the Event-B Explorer
 */
public final class TracesNode {

	private final IEventBRoot root;

	public TracesNode(final IEventBRoot root) {
		this.root = root;
	}

	/** @return the machine or context */
	public IEventBRoot getRoot() {
		return root;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof TracesNode && ((TracesNode) o).root.equals(root);
	}

	@Override
	public int hashCode() {
		return root.hashCode();
	}

	@Override
	public String toString() {
		return "Traces";
	}
}
