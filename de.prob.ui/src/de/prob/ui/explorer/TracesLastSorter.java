package de.prob.ui.explorer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Puts the "Traces" node of a machine or context after Rodin's nodes
 * (Invariants, Events, ..., Proof Obligations) in the Event-B Explorer, and
 * keeps their order otherwise. The navigator uses it when comparing the
 * "Traces" node with a node that is also a trigger point of the traces
 * content (see plugin.xml).
 */
@SuppressWarnings("deprecation") // the navigator's commonSorter is a ViewerSorter
public class TracesLastSorter extends ViewerSorter {

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		final boolean traces1 = e1 instanceof TracesNode;
		final boolean traces2 = e2 instanceof TracesNode;
		if (traces1 == traces2)
			return 0;
		return traces1 ? 1 : -1;
	}
}
