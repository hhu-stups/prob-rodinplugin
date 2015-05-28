package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.ui.ProbUiPlugin;

/**
 * Returns the labels for the TableViewer. The first column gets the name of the
 * operation, the second column a preview of the first operation with a button,
 * opening the OperationSelectionDialog
 */
class OperationsLabelProvider extends LabelProvider implements
		ITableLabelProvider {
	private final OperationTableViewer operationTableViewer;

	private final Image imgDisabled = ProbUiPlugin.getDefault()
			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_DISABLED)
			.createImage();
	private final Image imgTimeout = ProbUiPlugin.getDefault()
			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_TIMEOUT)
			.createImage();
	private final Image imgEnabled = ProbUiPlugin.getDefault()
			.getImageRegistry().getDescriptor(ProbUiPlugin.IMG_ENABLED)
			.createImage();

	/**
	 * @param operationTableViewer
	 */
	public OperationsLabelProvider(
			final OperationTableViewer operationTableViewer) {
		this.operationTableViewer = operationTableViewer;
	}

	// character code for "times" (similar to the letter x)
	private static final char TIMES = 215;

	@Override
	public Image getColumnImage(final Object element, final int columnIndex) {
		if (columnIndex == 0) {
			if (element instanceof String) {

				boolean timeout = Animator.getAnimator().getCurrentState()
						.isTimeoutOp((String) element);

				if (timeout)
					return imgTimeout;
				else
					return imgDisabled;
			} else if (element instanceof ArrayList<?>)
				return imgEnabled;
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public String getColumnText(final Object element, final int columnIndex) {

		// return the operation name
		if (columnIndex == 0) {
			if (element instanceof String)
				return (String) element;
			else if (element instanceof List) {
				List<Operation> ops = ((List<Operation>) element);
				final Operation op = ops.get(0);
				final int size = ops.size();
				if (size > 1) {
					StringBuilder sb = new StringBuilder();
					sb.append(op.getName());
					sb.append(" (").append(TIMES);
					sb.append(size).append(')');
					return sb.toString();
				} else
					return op.getName();
			}
		}

		// return a preview of the first operation and add a button
		if (columnIndex == 1) {
			if (element instanceof ArrayList) {
				ArrayList<Operation> ops = (ArrayList<Operation>) element;

				if (ops.size() > 0) {
					Operation op = ops.get(0);
					List<String> args = op.getArguments();
					int columnWidth = this.operationTableViewer.getViewer()
							.getTable().getColumn(columnIndex).getWidth();
					return OperationTableViewer.convertParamsToString(
							columnWidth / 6 / args.size(), columnWidth / 6,
							args);

				}
			}
		}
		return "";
	}
}