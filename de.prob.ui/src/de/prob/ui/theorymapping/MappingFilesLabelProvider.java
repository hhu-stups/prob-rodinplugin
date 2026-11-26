/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import de.prob.ui.ProbUiPlugin;

/**
 * Labels of the theory mapping files (.ptm) in the Event-B Explorer: the
 * file's name, with the file icon and the small ProB logo at its bottom right;
 * the status line says what the file is for.
 */
public class MappingFilesLabelProvider extends LabelProvider implements
		IDescriptionProvider {

	@Override
	public String getText(final Object element) {
		return element instanceof MappingFileNode ? ((MappingFileNode) element)
				.getFile().getName() : null;
	}

	/** the file icon with the small ProB logo; created when first needed */
	private Image image;

	@Override
	public Image getImage(final Object element) {
		if (!(element instanceof MappingFileNode))
			return null;
		if (image == null) {
			final Image file = PlatformUI.getWorkbench().getSharedImages()
					.getImage(ISharedImages.IMG_OBJ_FILE);
			// 8x8, and 16x16 (@2x) for high-resolution screens
			final ImageDescriptor logo = ProbUiPlugin
					.imageDescriptorFromPlugin(ProbUiPlugin.PLUGIN_ID,
							"icons/ovr_prob.png");
			image = logo == null ? file : new DecorationOverlayIcon(file,
					logo, IDecoration.BOTTOM_RIGHT).createImage();
		}
		return image;
	}

	@Override
	public void dispose() {
		final Image shared = PlatformUI.getWorkbench().getSharedImages()
				.getImage(ISharedImages.IMG_OBJ_FILE);
		if (image != null && image != shared)
			image.dispose();
		image = null;
		super.dispose();
	}

	@Override
	public String getDescription(final Object element) {
		if (!(element instanceof MappingFileNode))
			return null;
		final IFile file = ((MappingFileNode) element).getFile();
		return "ProB theory mapping file for the theory "
				+ file.getFullPath().removeFileExtension().lastSegment()
				+ ": maps its axiomatic operators to constructs of ProB";
	}
}
