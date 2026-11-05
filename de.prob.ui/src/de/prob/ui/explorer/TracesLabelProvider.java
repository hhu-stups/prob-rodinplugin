package de.prob.ui.explorer;

import java.util.EnumMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecorationOverlayIcon;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

import de.prob.ui.ProbUiPlugin;
import de.prob.ui.explorer.TraceStatus.Status;

/**
 * Labels of the "Traces" nodes and trace files in the Event-B Explorer. The
 * icon of a trace shows the status of its last replay (see
 * {@link TraceStatus}): a green check for a perfect replay, a warning for an
 * imperfect one, a red cross if it failed, none if unknown (the check and
 * cross with @2x variants for high-resolution screens). The status line
 * shows the details of the selected trace.
 */
public class TracesLabelProvider extends LabelProvider implements
		IDescriptionProvider {

	private final Map<Status, Image> images = new EnumMap<Status, Image>(
			Status.class);

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
		if (element instanceof TracesNode)
			return shared().getImage(ISharedImages.IMG_OBJ_FOLDER);
		if (element instanceof TraceNode)
			return image(status((TraceNode) element));
		return null;
	}

	@Override
	public String getDescription(final Object element) {
		if (element instanceof TracesNode)
			return "ProB traces of "
					+ ((TracesNode) element).getRoot().getComponentName();
		if (!(element instanceof TraceNode))
			return null;
		final TraceNode trace = (TraceNode) element;
		final String name = trace.getFile().getName();
		final String detail = TraceStatus.getDetail(trace.getFile());
		switch (status(trace)) {
		case PERFECT:
			return name + ": replayed perfectly (" + detail + ")";
		case IMPERFECT:
			return name + ": replayed imperfectly (" + detail + ")";
		case FAILED:
			return name + ": replay failed (" + detail + ")";
		default:
			return name + ": not replayed for the current model";
		}
	}

	private static Status status(final TraceNode trace) {
		return TraceStatus.get(trace.getFile(), trace.getParent().getRoot());
	}

	private Image image(final Status status) {
		Image image = images.get(status);
		if (image == null) {
			final ImageDescriptor overlay = overlay(status);
			final Image file = shared().getImage(ISharedImages.IMG_OBJ_FILE);
			image = overlay == null ? file : new DecorationOverlayIcon(file,
					overlay, IDecoration.BOTTOM_LEFT).createImage();
			images.put(status, image);
		}
		return image;
	}

	private static ImageDescriptor overlay(final Status status) {
		switch (status) {
		case PERFECT:
			return ProbUiPlugin.imageDescriptorFromPlugin(
					ProbUiPlugin.PLUGIN_ID, "icons/ovr_success.png");
		case IMPERFECT:
			return shared().getImageDescriptor(
					ISharedImages.IMG_DEC_FIELD_WARNING);
		case FAILED:
			return ProbUiPlugin.imageDescriptorFromPlugin(
					ProbUiPlugin.PLUGIN_ID, "icons/ovr_error.png");
		default:
			return null;
		}
	}

	private static ISharedImages shared() {
		return PlatformUI.getWorkbench().getSharedImages();
	}

	@Override
	public void dispose() {
		// the shared file icon (unknown status) must not be disposed
		final Image file = shared().getImage(ISharedImages.IMG_OBJ_FILE);
		for (Image image : images.values())
			if (image != file)
				image.dispose();
		images.clear();
		super.dispose();
	}
}
