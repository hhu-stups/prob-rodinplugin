package de.prob.ui.explorer;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Labels of the "Traces" nodes and trace files in the Event-B Explorer
 */
public class TracesLabelProvider extends LabelProvider {

	@Override
	public String getText(final Object element) {
		if (element instanceof TracesNode)
			return "Traces";
		if (element instanceof TraceNode)
			return ((TraceNode) element).getFile().getName();
		return null;
	}

	@Override
	public Image getImage(final Object element) {
		final ISharedImages images = PlatformUI.getWorkbench()
				.getSharedImages();
		if (element instanceof TracesNode)
			return images.getImage(ISharedImages.IMG_OBJ_FOLDER);
		if (element instanceof TraceNode)
			return images.getImage(ISharedImages.IMG_OBJ_FILE);
		return null;
	}
}
