/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;

/**
 * A theory mapping file (.ptm) in the Event-B Explorer. Not the file itself:
 * the explorer's filter "hide resources", which is on by default, hides all
 * files. It adapts to the file, e.g. for the Properties view and the
 * commands on the selection.
 */
public final class MappingFileNode implements IAdaptable {

	private final IFile file;

	public MappingFileNode(final IFile file) {
		this.file = file;
	}

	public IFile getFile() {
		return file;
	}

	@Override
	public <T> T getAdapter(final Class<T> adapter) {
		if (adapter.isInstance(file)
				&& (adapter == IFile.class || adapter == IResource.class))
			return adapter.cast(file);
		return null;
	}

	@Override
	public boolean equals(final Object o) {
		return o instanceof MappingFileNode
				&& ((MappingFileNode) o).file.equals(file);
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return file.getFullPath().toString();
	}
}
