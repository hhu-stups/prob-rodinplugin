/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Sash;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.prob.core.Animator;
import de.prob.core.command.GetOperationNamesCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.OperationInfo;
import de.prob.exceptions.ProBException;
import de.prob.ui.ProbUiPlugin;

/**
 * Creates a <code>Dialog</code> that lists all of the available
 * <code>Operations</code> in the current <code>ActiveState</code> of the
 * <code>Animator</code>. For a better comparison of the available Operations,
 * the user can switch between a normal and simplified view.
 * 
 * @author Lukas Diekmann
 * 
 */
public class OperationSelectionDialog extends TrayDialog {

	private static final String IMG_CHECKED = "icons/checked.gif";
	private static final String IMG_UNCHECKED = "icons/unchecked.gif";
	private static final int MIN_ARGUMENT_LENGTH = 20;

	private String shellTitle = "";
	private final Animator animator;
	private final List<String> arguments = new ArrayList<String>();
	private List<Operation> enabledOperations = null;

	private final List<Boolean> hiddenColumns = new ArrayList<Boolean>();
	private final List<Object> hiddenArguments = new ArrayList<Object>();

	private Button btSwitchOperation = null;

	private TableViewer tbvOps;
	private TableViewer argRepTable;

	private Operation lastSelectedOperation = null;
	private boolean opsHidden = false;

	// String[] alphabet = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
	// "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W",
	// "X", "Y", "Z" };

	public OperationSelectionDialog(final Shell parentShell) {
		super(parentShell);
		animator = Animator.getAnimator();
	}

	@Override
	protected void okPressed() {
		IStructuredSelection sel = (IStructuredSelection) tbvOps.getSelection();
		OperationEntry entry = (OperationEntry) sel.getFirstElement();
		lastSelectedOperation = entry.getOperation();
		super.okPressed();
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setSize(400, 500);
		// newShell.setMinimumSize(250, 200);
		newShell.setText("Select Operation: " + shellTitle);
	}

	public void setTitle(final String title) {
		shellTitle = title;
	}

	/* ------------------- CREATE METHODS FOR THE DIALOG -------------------- */
	/* ---------------------------------------------------------------------- */

	@Override
	protected Control createContents(final Composite parent) {
		Composite contents = new Composite(parent, SWT.NONE);
		contents.setLayoutData(new GridData(GridData.FILL_BOTH));
		contents.setLayout(new GridLayout());
		dialogArea = createDialogArea(contents);
		buttonBar = createButtonBar(contents);
		return contents;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		Composite content = new Composite(parent, SWT.NONE);

		content.setLayoutData(new GridData(GridData.FILL_BOTH));
		content.setLayout(new FormLayout());

		Sash sash = new Sash(content, SWT.HORIZONTAL);

		// Check if there are long-enough arguments that may be filtered
		boolean argumentsAreShortened = argumentsAreShortened();

		FormData data = new FormData();
		if (argumentsAreShortened) {
			data.top = new FormAttachment(50, 0); // Attach to top
		} else {
			data.top = new FormAttachment(100, 0); // Attach to top
		}
		data.left = new FormAttachment(0, 0); // Attach halfway across
		data.right = new FormAttachment(100, 0); // Attach halfway across
		sash.setLayoutData(data);

		Group gpOperations = new Group(content, SWT.SHADOW_ETCHED_IN);
		gpOperations.setText("Operations");
		data = new FormData();
		data.top = new FormAttachment(0, 0);
		data.bottom = new FormAttachment(sash, 0);
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(100, 0);
		gpOperations.setLayoutData(data);
		gpOperations.setLayout(new GridLayout(1, false));

		Composite tableComp = new Composite(gpOperations, SWT.NONE);
		tableComp.setLayoutData(new GridData(GridData.FILL_BOTH));
		tableComp.setLayout(new FillLayout());
		createOperationsTable(tableComp);

		if (argumentsAreShortened) {
			Group gpArgRep = new Group(content, SWT.SHADOW_ETCHED_IN);
			gpArgRep.setText("Argument replacements");
			data = new FormData();
			data.top = new FormAttachment(sash, 0);
			data.bottom = new FormAttachment(100, 0);
			data.left = new FormAttachment(0, 0);
			data.right = new FormAttachment(100, 0);
			gpArgRep.setLayoutData(data);
			gpArgRep.setLayout(new GridLayout(1, false));

			Composite tableComp2 = new Composite(gpArgRep, SWT.NONE);
			tableComp2.setLayoutData(new GridData(GridData.FILL_BOTH));
			tableComp2.setLayout(new FillLayout());

			createArgReplacementTable(tableComp2);
		}
		btSwitchOperation = new Button(gpOperations, SWT.PUSH);
		btSwitchOperation.setText("Show/Hide equal columns");
		btSwitchOperation.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(final SelectionEvent e) {
			}

			public void widgetSelected(final SelectionEvent e) {
				hideSameArguments();
			}

		});

		return content;
	}

	/* ------------- ADDITIONAL METHODS (GETTERS / SETTERS/ ETC) ------------ */
	/* ---------------------------------------------------------------------- */

	/**
	 * Get the <code>Operation</code> that was last selected in OperationsTable
	 * 
	 * @return <code>Operation</code>
	 */
	private Operation getSelectedOperation() {
		return lastSelectedOperation;
	}

	/**
	 * Creates and Instance of <code>OperationSelectionDialog</code> and returns
	 * the selected <code>Operation</code>.
	 * 
	 * @return <code>Operation</code>
	 */
	public static Operation getOperation(final List<Operation> list) {
		String shellTitle = "";
		if (list.get(0) != null) {
			shellTitle = list.get(0).getName();
		}
		OperationSelectionDialog osd = new OperationSelectionDialog(new Shell());
		osd.setTitle(shellTitle);
		osd.setEnabledOperations(list);
		if (osd.open() == TitleAreaDialog.OK)
			return osd.getSelectedOperation();
		else
			return null;
	}

	/**
	 * Check if there are argument going to be filtered.
	 * 
	 * @return
	 */
	private boolean argumentsAreShortened() {
		for (Operation op : getEnabledOperations()) {
			for (String arg : op.getArguments()) {
				if (arg.length() > MIN_ARGUMENT_LENGTH)
					return true;
			}
		}
		return false;
	}

	/**
	 * Hides those columns where all <code>Operations</code> have the same
	 * <code>Argument</code>
	 */
	private void hideSameArguments() {
		if (!opsHidden) {
			// hide column (if all operations have the same argument)
			List<Operation> opList = getEnabledOperations();
			final Operation firstOp = opList.get(0);
			for (int i = 0; i < firstOp.getArguments().size(); i++) {
				boolean valuesAreEqual = allValuesAreEqual(opList, i);
				hiddenColumns.set(i, valuesAreEqual);
			}
		} else {
			// show columns
			ListIterator<Boolean> l = hiddenColumns.listIterator();
			while (l.hasNext()) {
				l.next();
				l.set(false);
			}
		}

		// switch Button
		opsHidden = !opsHidden;

		// refresh table
		tbvOps.refresh();
		packTableColumns(tbvOps);
	}

	private boolean allValuesAreEqual(final Collection<Operation> operations,
			final int argNum) {
		if (operations.isEmpty())
			return true;
		else {
			String firstArg = null;
			for (final Operation op : operations) {
				final String curArg = op.getArguments().get(argNum);
				if (firstArg == null) {
					firstArg = curArg;
				} else {
					if (!firstArg.equals(curArg))
						return false;
				}
			}
			return true;
		}
	}

	/**
	 * Sets a predefined list of operations. Operations from Animator won't be
	 * used then.
	 * 
	 * @param ArrayList
	 *            of Operations
	 */
	private void setEnabledOperations(final List<Operation> al) {
		enabledOperations = al;
	}

	/**
	 * Gets all possible Operations from the current ActiveState of the Animator
	 * 
	 * @return
	 */
	private List<Operation> getEnabledOperations() {
		if (enabledOperations == null) {
			enabledOperations = animator.getCurrentState()
					.getEnabledOperations();
		}
		return enabledOperations;
	}

	private List<String> getOperationParams(final Operation op) {
		Collection<OperationInfo> infos = null;
		try {
			infos = GetOperationNamesCommand.getNames(animator);
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
		final OperationInfo params = infos == null ? null : OperationInfo
				.getParams(op.getName(), infos);
		final List<String> result;
		if (params != null) {
			result = params.getParameters();
		} else {
			// If we cannot see the parameter names, we just use the operation's
			// number of arguments and use empty titles
			final int numArgs = op.getArguments().size();
			result = new ArrayList<String>(numArgs);
			for (int i = 0; i < numArgs; i++) {
				result.add("");
			}
		}
		return result;
	}

	/*
	 * --------- CREATE METHODS FOR THE ARGUMENT REPLACEMENT TABLE ---------
	 */

	private void createArgReplacementTable(final Composite parent) {
		argRepTable = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		createArgReplacmentColumns(argRepTable);
		argRepTable.setContentProvider(new ArgRepContentProvider());
		argRepTable.setLabelProvider(new ArgRepLabelProvider());
		argRepTable.setInput(getEnabledOperations());
		packTableColumns(argRepTable);
	}

	private void createArgReplacmentColumns(final TableViewer tbViewer) {
		TableViewerColumn hide = new TableViewerColumn(tbViewer, SWT.NONE);
		hide.getColumn().setText("!");
		hide.getColumn().setResizable(true);
		hide.setEditingSupport(new EditingSupport(tbViewer) {

			@Override
			protected boolean canEdit(final Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(final Object element) {
				return new CheckboxCellEditor(tbViewer.getTable());
			}

			/*
			 * There are no SelectionListeners for fields, so we use the
			 * getValue method from EditingSupport, which is called when an
			 * element is selected
			 */
			@Override
			protected Object getValue(final Object element) {
				if (hiddenArguments.contains(element)) {
					hiddenArguments.remove(element);
				} else {
					hiddenArguments.add(element);
				}
				tbViewer.refresh(element);
				tbvOps.refresh();
				packTableColumns(tbvOps);
				return null;
			}

			@Override
			protected void setValue(final Object element, final Object value) {
			}

		});

		TableViewerColumn repl = new TableViewerColumn(tbViewer, SWT.NONE);
		repl.getColumn().setText("Repl.");
		repl.getColumn().setResizable(true);

		TableViewerColumn org = new TableViewerColumn(tbViewer, SWT.NONE);
		org.getColumn().setText("Original");
		org.getColumn().setResizable(true);

		Table table = tbViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

	}

	/* ------------- CREATE METHODS FOR THE OPERATIONS TABLE ---------------- */
	/* ---------------------------------------------------------------------- */

	private void createOperationsTable(final Composite parent) {
		tbvOps = new TableViewer(parent, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.SINGLE);
		List<Operation> opList = getEnabledOperations();

		List<String> params = getOperationParams(opList.get(0));

		// fix for nondeterministic operations that have no parameters
		if (params.size() == 0) {
			params = new ArrayList<String>();
			params.add("");
		}

		createOperationsTableColumns(tbvOps, params);
		tbvOps.setContentProvider(new OperationsContentProvider());
		tbvOps.setLabelProvider(new OperationsLabelProvider());
		tbvOps.setInput(opList);
		tbvOps.addDoubleClickListener(new IDoubleClickListener() {

			public void doubleClick(final DoubleClickEvent event) {
				okPressed();
			}
		});
		packTableColumns(tbvOps);
	}

	private void createOperationsTableColumns(final TableViewer tbViewer,
			final List<String> params) {

		int count = 0;

		for (String string : params) {
			final int index = count;
			hiddenColumns.add(false);
			TableViewerColumn column = new TableViewerColumn(tbViewer, SWT.NONE);
			column.getColumn().setText(string);
			column.getColumn().setResizable(true);
			column.getColumn().pack();
			column.getColumn().addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(final SelectionEvent e) {
					hiddenColumns.set(index, !hiddenColumns.get(index));
					tbViewer.refresh();
					packTableColumns(tbViewer);
				}

				public void widgetSelected(final SelectionEvent e) {
				}
			});
			count++;
		}

		Table table = tbViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void packTableColumns(final TableViewer tbViewer) {
		for (TableColumn col : tbViewer.getTable().getColumns()) {
			col.pack();
		}
	}

	/**
	 * Defines the look of the strings for argument replacing
	 * 
	 * @param index
	 * @return String
	 */
	private String getReplacementString(final int index) {
		return "[" + index + "]";
	}

	/* ---------------- PROVIDERS FOR THE OPERATIONS TABLE ------------------ */
	/* ---------------------------------------------------------------------- */

	private class OperationsLabelProvider extends LabelProvider implements
			ITableLabelProvider {
		public Image getColumnImage(final Object element, final int columnIndex) {
			return null;
		}

		public String getColumnText(final Object element, final int columnIndex) {
			if (hiddenColumns.get(columnIndex))
				return "-";
			else {
				OperationEntry entry = (OperationEntry) element;
				if (!entry.isInternalNondeterminism()) {
					final Operation op = entry.getOperation();
					final String argument = op.getArguments().get(columnIndex);
					if (hiddenArguments.contains(argument))
						return getReplacementString(arguments.indexOf(argument));
					else
						return argument;
				} else
				    // TODO: obtain non-deterministically assigned variables and display them
					return "Non-deterministic choice #" + entry.getPosition();
			}
		}

	}

	private static class OperationEntry {
		private final Operation operation;
		private final int position;

		public OperationEntry(final Operation operation, final int position) {
			this.operation = operation;
			this.position = position;
		}

		public Operation getOperation() {
			return operation;
		}

		public int getPosition() {
			return position;
		}

		public boolean isInternalNondeterminism() {
			return operation.getArguments().isEmpty();
		}
	}

	private static class OperationsContentProvider implements
			IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(final Object inputElement) {
			final Collection<Operation> operations = (List<Operation>) inputElement;
			OperationEntry[] elements = new OperationEntry[operations.size()];
			int pos = 0;
			for (final Operation op : operations) {
				elements[pos] = new OperationEntry(op, pos);
				pos++;
			}
			return elements;
		}

	}

	/* ------------------ PROVIDERS FOR THE HISTORY TABLE ------------------- */
	/* ---------------------------------------------------------------------- */

	private class ArgRepLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == 0) {
				if (hiddenArguments.contains(element))
					return AbstractUIPlugin.imageDescriptorFromPlugin(
							ProbUiPlugin.PLUGIN_ID, IMG_CHECKED).createImage();
				else
					return AbstractUIPlugin.imageDescriptorFromPlugin(
							ProbUiPlugin.PLUGIN_ID, IMG_UNCHECKED)
							.createImage();
			} else
				return null;

		}

		public String getColumnText(final Object element, final int columnIndex) {
			if (columnIndex == 1) {
				int index = arguments.indexOf(element);
				return getReplacementString(index);
			} else if (columnIndex == 2)
				return (String) element;
			else
				return "";
		}
	}

	/**
	 * Fills the HistoryTable with arguments and their replacements. The
	 * arguments that will be replaced, can be filtered in the getElements
	 * method.
	 */
	private class ArgRepContentProvider implements IStructuredContentProvider {

		public void dispose() {
		}

		public void inputChanged(final Viewer viewer, final Object oldInput,
				final Object newInput) {
		}

		@SuppressWarnings("unchecked")
		public Object[] getElements(final Object inputElement) {
			List<Operation> operations = (List<Operation>) inputElement;

			// create Array with all possible Arguments (from all Operations)
			for (Operation op : operations) {
				for (String arg : op.getArguments()) {
					if (arg.length() > MIN_ARGUMENT_LENGTH
							&& !arguments.contains(arg)) {
						arguments.add(arg);
					}
				}
			}

			return arguments.toArray();
		}
	}

}
