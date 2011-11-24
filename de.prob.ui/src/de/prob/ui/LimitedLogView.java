/**
 * 
 */
package de.prob.ui;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.part.ViewPart;

import de.prob.core.LimitedLogger;
import de.prob.core.LimitedLogger.LogEntry;

/**
 * This view shows the content of the {@link LimitedLogger} and allows the user
 * to see durations between events easily.
 * 
 * @author plagge
 * 
 */
public class LimitedLogView extends ViewPart implements
		LimitedLogger.LogListener {

	private LimitedLogger logger;
	private TableViewer viewer;
	private Long offset;

	public LimitedLogView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.logger = LimitedLogger.getLogger();
		this.logger.registerListener(this);
		createViewer(parent);
		updateLoggingStart();
		viewer.setInput(logger);
	}

	private void createViewer(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION);
		createColumns(viewer);
		viewer.setContentProvider(new LogContentProvider());
		viewer.setLabelProvider(new LogLabelProvider());
	}

	private void createColumns(TableViewer viewer) {
		createColumn(viewer, SWT.RIGHT, "Time", 50);
		createColumn(viewer, SWT.LEFT, "Category", 100);
		createColumn(viewer, SWT.LEFT, "Description", 300);

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		viewer.addDoubleClickListener(new LogViewerDoubleClick());
	}

	private void createColumn(TableViewer viewer, final int style,
			final String title, final int width) {
		final TableViewerColumn column = new TableViewerColumn(viewer, style);
		column.getColumn().setText(title);
		column.getColumn().setWidth(width);
		column.getColumn().setResizable(true);
		column.getColumn().setMoveable(true);
	}

	@Override
	public void setFocus() {
		viewer.getTable().getParent().setFocus();
	}

	@Override
	public void dispose() {
		super.dispose();
		logger.unregisterListener(this);
		logger = null;
	}

	public void newLoggingInfo() {
		if (viewer != null) {
			updateLoggingStart();
			asyncRefresh();
		}
	}

	private void updateLoggingStart() {
		if (offset == null) {
			offset = logger.getFirstLoggingTime();
		}
	}

	private void asyncRefresh() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				viewer.refresh();
			}
		});
	}

	private static class LogContentProvider implements
			IStructuredContentProvider {

		public Object[] getElements(Object object) {
			if (object != null && object instanceof LimitedLogger) {
				LimitedLogger logger = (LimitedLogger) object;
				return logger.getEntries();
			} else {
				return null;
			}
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object arg1, Object arg2) {
		}
	}

	private class LogViewerDoubleClick implements IDoubleClickListener {
		public void doubleClick(DoubleClickEvent event) {
			ISelection selection = event.getSelection();
			if (selection != null && !selection.isEmpty()) {
				IStructuredSelection ssel = (IStructuredSelection) selection;
				LogEntry entry = (LogEntry) ssel.getFirstElement();
				offset = entry.getTime();
				asyncRefresh();
			}
		}
	}

	private class LogLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(Object object, int column) {
			return null;
		}

		public String getColumnText(Object object, int column) {
			final String result;
			if (object != null && object instanceof LogEntry) {
				LogEntry entry = (LogEntry) object;
				switch (column) {
				case 0:
					final long time = entry.getTime()
							- (offset == null ? 0 : offset);
					result = String.format("%,d", time);
					break;
				case 1:
					result = entry.getCategory();
					break;
				case 2:
					result = entry.getShortDescription();
					break;
				default:
					result = null;
				}
			} else {
				result = null;
			}
			return result;
		}
	}

}
