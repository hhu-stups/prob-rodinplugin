package de.prob.ui.explorer;

import org.eclipse.jface.window.DefaultToolTip;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * The hover of a trace in the Event-B Explorer: its name, number of steps
 * and description (from the trace file). Other elements of the Explorer get
 * no hover from it.
 */
final class TraceToolTip extends DefaultToolTip {

	private static final String KEY = TraceToolTip.class.getName();

	private final Tree tree;

	private TraceToolTip(final Tree tree) {
		super(tree, ToolTip.RECREATE, false);
		this.tree = tree;
		setHideOnMouseDown(true);
	}

	/** adds the hover to the tree, unless it already has it */
	static void install(final Tree tree) {
		if (tree.isDisposed() || tree.getData(KEY) != null)
			return;
		tree.setData(KEY, new TraceToolTip(tree));
	}

	private TraceNode trace(final Event event) {
		final TreeItem item = tree.getItem(new Point(event.x, event.y));
		return item != null && item.getData() instanceof TraceNode ? (TraceNode) item
				.getData() : null;
	}

	/** a new hover for each item */
	@Override
	protected Object getToolTipArea(final Event event) {
		final TreeItem item = tree.getItem(new Point(event.x, event.y));
		return item != null ? item : super.getToolTipArea(event);
	}

	@Override
	protected boolean shouldCreateToolTip(final Event event) {
		return trace(event) != null && super.shouldCreateToolTip(event);
	}

	@Override
	protected String getText(final Event event) {
		final TraceNode trace = trace(event);
		if (trace == null)
			return null;
		final TraceFiles.Summary summary = TraceFiles.summary(trace.getFile());
		final StringBuilder text = new StringBuilder(
				TraceFiles.displayName(trace.getFile()));
		if (summary.steps < 0)
			text.append("\nThe trace file could not be read: ").append(
					summary.error);
		else
			text.append('\n').append(summary.steps)
					.append(summary.steps == 1 ? " step" : " steps");
		if (summary.description != null)
			text.append('\n').append(summary.description);
		return text.toString();
	}
}
