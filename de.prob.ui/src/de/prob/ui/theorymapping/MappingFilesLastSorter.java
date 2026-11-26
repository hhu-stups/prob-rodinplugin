/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Puts the theory mapping files (.ptm) of a project after its machines,
 * contexts and theories in the Event-B Explorer, sorted by name, and keeps
 * the order of the other children (see plugin.xml).
 */
@SuppressWarnings("deprecation") // the navigator's commonSorter is a ViewerSorter
public class MappingFilesLastSorter extends ViewerSorter {

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		final boolean file1 = e1 instanceof MappingFileNode;
		final boolean file2 = e2 instanceof MappingFileNode;
		if (file1 && file2)
			return ((MappingFileNode) e1).getFile().getName()
					.compareToIgnoreCase(
							((MappingFileNode) e2).getFile().getName());
		if (file1 == file2)
			return 0;
		return file1 ? 1 : -1;
	}
}
