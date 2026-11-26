/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

/**
 * Labels of the theory mapping files (.ptm) in the Event-B Explorer: the
 * file's name with a file icon; the status line says what the file is for.
 */
public class MappingFilesLabelProvider extends LabelProvider implements
		IDescriptionProvider {

	@Override
	public String getText(final Object element) {
		return element instanceof IFile ? ((IFile) element).getName() : null;
	}

	@Override
	public Image getImage(final Object element) {
		return element instanceof IFile ? PlatformUI.getWorkbench()
				.getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE) : null;
	}

	@Override
	public String getDescription(final Object element) {
		if (!(element instanceof IFile))
			return null;
		final IFile file = (IFile) element;
		return "ProB theory mapping file for the theory "
				+ file.getFullPath().removeFileExtension().lastSegment()
				+ ": maps its axiomatic operators to constructs of ProB";
	}
}
