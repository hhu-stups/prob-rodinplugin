/**
 * 
 */
package de.prob.ui.historyview;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import de.prob.ui.historyview.HistoryView.HistViewItem;

/**
 * Base class for label provider for the columns of the history view.
 * 
 * @author plagge
 */
public abstract class HistoryLabelProvider extends CellLabelProvider {
	private final Color currentPositionColor = Display.getDefault()
			.getSystemColor(SWT.COLOR_INFO_BACKGROUND);
	final Font bold = JFaceResources.getFontRegistry().getBold(
			JFaceResources.BANNER_FONT);

	protected boolean showParameters = true;

	@Override
	public void update(final ViewerCell cell) {
		final Object element = cell.getElement();
		if (element != null && element instanceof HistViewItem) {
			final HistViewItem item = (HistViewItem) element;
			cell.setText(getText(item));
			cell.setForeground(getForeground(item));
			// cell.setBackground(getBackground(item));
			// cell.setFont(getFont(item));
		}
	}

	protected Font getFont(final HistViewItem item) {
		return item.followingStateIsSameAsCurrent() ? bold : null;
	}

	protected Color followingStateIsActive(final HistViewItem item) {
		return item.followingStateIsActive() ? currentPositionColor : null;
	}

	protected String getText(final HistViewItem item) {
		return null;
	}

	protected Color getForeground(final HistViewItem item) {
		return null;
	}

	public void setShowParameters(final boolean show) {
		showParameters = show;
	}
}
