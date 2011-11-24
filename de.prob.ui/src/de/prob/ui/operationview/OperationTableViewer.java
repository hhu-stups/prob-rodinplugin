package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.GetOperationNamesCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.OperationInfo;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class OperationTableViewer {

	public enum DoubleClickBehaviour {
		NORMAL, RANDOM, DIALOG
	};

	private static DoubleClickBehaviour doubleClickBehaviour = DoubleClickBehaviour.NORMAL;
	private static OperationTableViewer instance;
	private final Collection<String> operationNames = getAllOperations();
	private final TableViewer viewer;

	private static final ViewerFilter DISABLED_OPS_FILTER = new ViewerFilter() {
		@Override
		public boolean select(final Viewer viewer, final Object parentElement,
				final Object element) {
			if (!(element instanceof ArrayList<?>))
				return false;
			return true;
		}
	};

	private OperationTableViewer(final Composite parent, final int style) {
		viewer = new TableViewer(parent, style);
		createColumns();
		viewer.setContentProvider(new OperationsContentProvider(operationNames));
		viewer.setLabelProvider(new OperationsLabelProvider(this));
		viewer.addDoubleClickListener(new OTVDoubleClickListener());

		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private Collection<String> getAllOperations() {
		Collection<String> list = new ArrayList<String>();
		try {
			list = OperationInfo.extractNames(GetOperationNamesCommand
					.getNames(Animator.getAnimator()));
		} catch (ProBException e) {

		}
		return list;
	}

	public void refresh() {
		viewer.refresh();
		packTableColumns();
	}

	private void createColumns() {
		TableViewerColumn column1 = new TableViewerColumn(viewer, SWT.NONE);
		column1.getColumn().setText("Event");
		column1.getColumn().setResizable(true);
		column1.getColumn().pack();

		TableViewerColumn column2 = new TableViewerColumn(viewer, SWT.NONE);
		column2.getColumn().setText("Parameter(s)");
		column2.getColumn().setResizable(true);
		column2.getColumn().pack();
	}

	/**
	 * Recalculate size of all columns
	 */
	private void packTableColumns() {
		for (TableColumn column : viewer.getTable().getColumns()) {
			column.pack();
		}
	}

	/**
	 * Gets the ArrayList of Operations from the selected Item in the Table
	 * 
	 * @return ArrayList (Operation)
	 */
	@SuppressWarnings("unchecked")
	public List<Operation> getSelectedOperations() {
		if (viewer.getSelection() != null
				&& viewer.getSelection() instanceof IStructuredSelection) {
			final IStructuredSelection ssel = (IStructuredSelection) viewer
					.getSelection();
			if (ssel.getFirstElement() instanceof ArrayList)
				return (List<Operation>) ssel.getFirstElement();
		}
		return Collections.emptyList();
	}

	/* ---------------- LISTENERS FOR THE OPERATIONS TABLE ------------------ */
	/* ---------------------------------------------------------------------- */

	/**
	 * Listener that selects and executes an operation, when a double-click in
	 * the table occurs
	 */
	private class OTVDoubleClickListener implements IDoubleClickListener {

		public void doubleClick(final DoubleClickEvent event) {
			if (!getSelectedOperations().isEmpty()) {
				LimitedLogger.getLogger().log("user chooses event", null, null);
				switch (doubleClickBehaviour) {
				case NORMAL:
					executeSingleOperation(0);
					break;
				case RANDOM:
					executeRandomOperation();
					break;
				case DIALOG:
					IHandlerService service = (IHandlerService) PlatformUI
							.getWorkbench().getService(IHandlerService.class);
					try {
						service.executeCommand(
								"de.prob.ui.show_parameter_dialog", null);
					} catch (Exception e) {
						Logger.assertFail("Exception when calling Handler Service");
					}
					break;
				}
			}
		}

	}

	/**
	 * Executes a random operation.
	 */
	private void executeRandomOperation() {
		List<Operation> selectedOperations = getSelectedOperations();
		int use = new Random().nextInt(selectedOperations.size());
		executeSingleOperation(use);
		// ExecuteRandomStepsCommand.executeOperation(Animator.getAnimator(),
		// 1);
	}

	/**
	 * Executes a specific operation given by the index.
	 * 
	 * @param index
	 */
	private void executeSingleOperation(final int index) {
		try {
			ExecuteOperationCommand.executeOperation(Animator.getAnimator(),
					getSelectedOperations().get(index));
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
	}

	/**
	 * Opens the OperationSelectionDialog if there are many operations
	 * available. Otherwise executes the operation that is there.
	 */
	// private void openSelectionDialog() {
	// if (getSelectedOperations().size() > 1) {
	// final Operation op = OperationSelectionDialog
	// .getOperation(getSelectedOperations());
	// if (op != null) {
	// try {
	// ExecuteOperationCommand.executeOperation(
	// Animator.getAnimator(), op);
	// } catch (ProBException e) {
	// e.notifyUserOnce();
	// }
	// }
	// } else {
	// executeSingleOperation(0);
	// }
	// }

	/**
	 * Converts a list of Arguments to a readable (short) String.
	 * 
	 * @param argLength
	 *            Maximum length an argument can have
	 * @param totalLength
	 *            Maximum length of the final String
	 * @param params
	 *            List containing the Arguments
	 * @return
	 */
	public static String convertParamsToString(final int argLength,
			final int totalLength, final List<String> params) {
		StringBuffer sb = new StringBuffer();
		boolean first = true;
		for (final String parameter : params) {
			if (!first) {
				sb.append(", ");
			}
			int length = parameter.length();
			if (length > argLength) {
				if (argLength > 6) {
					sb.append(parameter.substring(0, argLength - 3));
				}
				sb.append("...");
			} else {
				sb.append(parameter);
			}
			first = false;
		}
		String result = sb.toString();
		if (result.length() >= totalLength) {
			result = result.substring(0, totalLength) + " [...]";
		}
		return result;
	}

	/**
	 * Sets the doubleclick behavior for the TableItems
	 */
	public static void setDoubleClickBehavior(final String behavior) {
		doubleClickBehaviour = DoubleClickBehaviour.valueOf(behavior);
	}

	public TableViewer getViewer() {
		return viewer;
	}

	public static OperationTableViewer create(final Composite pageComposite,
			final int i) {
		if (instance == null) {
			instance = new OperationTableViewer(pageComposite, i);

			ICommandService service = (ICommandService) PlatformUI
					.getWorkbench().getService(ICommandService.class);
			Command command = service
					.getCommand("de.prob.ui.filter_enabled_only");
			State state = command
					.getState("org.eclipse.ui.commands.toggleState");
			if (state == null)
				instance.unapplyFilter();
			else {
				if ((Boolean) state.getValue())
					instance.applyFilter();
				else
					instance.unapplyFilter();
			}
		}
		return instance;
	}

	public static OperationTableViewer getInstance() {
		return instance;
	}

	public static void destroy() {
		instance = null;
	}

	public void applyFilter() {
		getViewer().setFilters(new ViewerFilter[] { DISABLED_OPS_FILTER });
	}

	public void unapplyFilter() {
		getViewer().resetFilters();
	}

}
