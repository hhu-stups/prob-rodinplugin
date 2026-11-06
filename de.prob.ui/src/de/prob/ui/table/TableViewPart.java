package de.prob.ui.table;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.PrologException;
import de.prob.core.command.CallTableCommand;
import de.prob.core.command.CommandFormulas;
import de.prob.core.command.GetCommandsCommand;
import de.prob.core.command.GetCommandsCommand.Category;
import de.prob.core.command.GetCommandsCommand.CommandInfo;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.CommandMenus;
import de.prob.ui.StaticListenerRegistry;
import de.prob.ui.console.ConsoleViewPart;
import de.prob.ui.graph.GraphViewPart;

/**
 * Shows the tables that ProB can compute in the current state, e.g. machine
 * statistics or coverage information. The tables are offered in one toolbar
 * drop-down menu per group. Tables that need an expression take it from the
 * field above the table.
 */
public class TableViewPart extends ViewPart implements ILifecycleListener,
		IAnimationListener {

	public static final String VIEW_ID = "de.prob.ui.TableView";

	// ProB's command for the value of an expression as a table
	private static final String EXPRESSION_TABLE = "expr_as_table";

	// names of ProB's groups for the menus; others are derived from the name
	private static final Map<String, String> GROUP_LABELS = new LinkedHashMap<String, String>();
	static {
		GROUP_LABELS.put("machine_info", "Machine");
		GROUP_LABELS.put("coverage_info", "Coverage");
		GROUP_LABELS.put("formula_visualisation", "Formulas");
		GROUP_LABELS.put("wd_info", "Well-Definedness");
		GROUP_LABELS.put("trace_visualisation", "Trace");
		GROUP_LABELS.put("visb_info", "VisB");
		GROUP_LABELS.put("debug_info", "Debug");
	}

	// groups of ProB that are not offered (yet); edit to show them
	private static final Set<String> HIDDEN_GROUPS = new HashSet<String>(
			Arrays.asList("visb_info"));
	// commands of ProB that are not offered (yet); edit to show them
	private static final Set<String> HIDDEN_COMMANDS = new HashSet<String>(
			Arrays.asList("mcdc_coverage", "mcdc_inv_coverage"));

	private static final String COMPLETION_HINT = "Tab or Ctrl+Space in the expression field completes identifiers.";

	private Text expressionField;
	private Label status;
	private Table table;
	private Job runningJob;
	private Action stop;
	private Action save;
	/** the title of the table shown, for Save; null if there is none */
	private String savableTitle;
	private Clipboard clipboard;

	// the menus with the tables from ProB
	private CommandMenus menus;
	private CommandInfo shown;

	// the table's contents
	private List<String> headers = Collections.emptyList();
	private List<List<String>> rows = Collections.emptyList();
	private int sortColumn = -1;
	private boolean sortAscending = true;

	@Override
	public void createPartControl(final Composite parent) {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 2;
		parent.setLayout(layout);
		createExpressionField(parent);

		status = new Label(parent, SWT.WRAP);
		final GridData statusData = new GridData(SWT.FILL, SWT.TOP, true,
				false);
		statusData.horizontalIndent = 5;
		status.setLayoutData(statusData);

		table = new Table(parent, SWT.VIRTUAL | SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addListener(SWT.SetData, new Listener() {
			@Override
			public void handleEvent(final Event event) {
				final TableItem item = (TableItem) event.item;
				final int index = table.indexOf(item);
				if (index >= 0 && index < rows.size())
					item.setText(rows.get(index).toArray(new String[0]));
			}
		});
		clipboard = new Clipboard(parent.getDisplay());
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(final KeyEvent e) {
				if ((e.stateMask & SWT.MOD1) != 0
						&& (e.keyCode == 'c' || e.keyCode == 'C'))
					copySelection();
				else if ((e.stateMask & SWT.MOD1) != 0
						&& (e.keyCode == 'a' || e.keyCode == 'A'))
					table.selectAll();
			}
		});

		createActions();
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
		StaticListenerRegistry.registerListener((IAnimationListener) this);
		loadCommands(null);
	}

	/** the expression for tables that need one; Enter shows the table again */
	private void createExpressionField(final Composite parent) {
		final Composite row = new Composite(parent, SWT.NONE);
		row.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 2;
		row.setLayout(layout);
		final Label label = new Label(row, SWT.NONE);
		label.setText("Expression:");
		expressionField = new Text(row, SWT.SINGLE | SWT.BORDER);
		expressionField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		expressionField
				.setMessage("for the tables ending in \"...\", e.g. Expression Table...");
		expressionField
				.setToolTipText("The expression for the tables that need one (their names end in \"...\")."
						+ "\nEnter: compute the table again, Tab or Ctrl+Space: complete identifiers");
		expressionField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				if (shown != null && shown.getFormulaArguments() > 0)
					showTable(shown);
				else
					setStatus("Choose a table that needs an expression (its name ends in \"...\"). "
							+ COMPLETION_HINT, false);
			}
		});
		ConsoleViewPart.installMathSymbols(expressionField);
		ConsoleViewPart.installCompletion(expressionField);
	}

	private void createActions() {
		final IToolBarManager toolBar = getViewSite().getActionBars()
				.getToolBarManager();
		menus = new CommandMenus(getViewSite(), "de.prob.ui.table", "tables",
				GROUP_LABELS, HIDDEN_GROUPS, HIDDEN_COMMANDS,
				new CommandMenus.Handler() {
					@Override
					public void run(final CommandInfo command) {
						showTable(command);
					}
				});
		menus.addTo(toolBar);
		toolBar.add(new Separator());
		final Action refresh = new Action("Refresh") {
			@Override
			public void run() {
				refresh();
			}
		};
		refresh.setToolTipText("Compute the table again (e.g. after animating or model checking)");
		toolBar.add(refresh);
		stop = new Action("Stop") {
			@Override
			public void run() {
				if (runningJob == null)
					return;
				runningJob.cancel();
				setRunningJob(null);
				setStatus("Stopped. Choose a table or press Refresh to compute it again.", false);
			}
		};
		stop.setToolTipText("Stop computing the table: interrupts ProB");
		final ISharedImages images = PlatformUI.getWorkbench()
				.getSharedImages();
		stop.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
		stop.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP_DISABLED));
		stop.setEnabled(false);
		toolBar.add(stop);
		toolBar.add(new Separator());
		save = new Action("Save Table As...") {
			@Override
			public void run() {
				saveTable();
			}
		};
		save.setToolTipText("Save the table as comma-separated values (.csv) or as text with aligned columns (.txt)");
		save.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
		save.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT_DISABLED));
		save.setEnabled(false);
		toolBar.add(save);
	}

	private void setSavableTitle(final String title) {
		savableTitle = title;
		if (save != null)
			save.setEnabled(title != null);
	}

	/**
	 * saves the shown table, in its current order, as .csv or .txt, chosen by
	 * the file's extension
	 */
	private void saveTable() {
		if (savableTitle == null)
			return;
		final FileDialog dialog = new FileDialog(getSite().getShell(),
				SWT.SAVE);
		dialog.setText("Save Table As");
		dialog.setFilterExtensions(new String[] { "*.csv", "*.txt" });
		dialog.setFilterNames(new String[] {
				"Comma-separated values (*.csv)", "Text (*.txt)" });
		dialog.setFileName(GraphViewPart.fileName(savableTitle) + ".csv");
		dialog.setOverwrite(true);
		String path = dialog.open();
		if (path == null)
			return;
		final String lower = path.toLowerCase(Locale.ROOT);
		final boolean csv;
		if (lower.endsWith(".csv"))
			csv = true;
		else if (lower.endsWith(".txt"))
			csv = false;
		else {
			// no known extension: the chosen file type's
			csv = dialog.getFilterIndex() != 1;
			path += csv ? ".csv" : ".txt";
		}
		final String text = csv ? toCsv(headers, rows) : toText(headers, rows);
		try {
			Files.write(new File(path).toPath(),
					text.getBytes(StandardCharsets.UTF_8));
			getViewSite().getActionBars().getStatusLineManager()
					.setMessage("Saved the table to " + path);
		} catch (IOException e) {
			MessageDialog.openError(getSite().getShell(), "Save Table As",
					"Could not save the table to " + path + ": "
							+ e.getMessage());
		}
	}

	/** @return the table as CSV (RFC 4180), with the headers first */
	static String toCsv(final List<String> headers,
			final List<List<String>> rows) {
		final StringBuilder text = new StringBuilder();
		csvLine(text, headers);
		for (List<String> row : rows)
			csvLine(text, row);
		return text.toString();
	}

	private static void csvLine(final StringBuilder text,
			final List<String> cells) {
		for (int i = 0; i < cells.size(); i++) {
			if (i > 0)
				text.append(',');
			final String cell = cells.get(i);
			if (cell.indexOf(',') >= 0 || cell.indexOf('"') >= 0
					|| cell.indexOf('\n') >= 0 || cell.indexOf('\r') >= 0)
				text.append('"').append(cell.replace("\"", "\"\"")).append('"');
			else
				text.append(cell);
		}
		text.append("\r\n");
	}

	/**
	 * @return the table as text, with the columns aligned and the headers
	 *         underlined
	 */
	static String toText(final List<String> headers,
			final List<List<String>> rows) {
		final List<Integer> widths = new ArrayList<Integer>();
		widen(widths, headers);
		for (List<String> row : rows)
			widen(widths, row);
		final StringBuilder text = new StringBuilder();
		textLine(text, headers, widths);
		final List<String> rule = new ArrayList<String>();
		for (int width : widths)
			rule.add("-".repeat(width));
		textLine(text, rule, widths);
		for (List<String> row : rows)
			textLine(text, row, widths);
		return text.toString();
	}

	private static void widen(final List<Integer> widths,
			final List<String> cells) {
		for (int i = 0; i < cells.size(); i++) {
			final int width = cells.get(i).codePointCount(0,
					cells.get(i).length());
			if (i < widths.size())
				widths.set(i, Math.max(widths.get(i), width));
			else
				widths.add(width);
		}
	}

	private static void textLine(final StringBuilder text,
			final List<String> cells, final List<Integer> widths) {
		final StringBuilder line = new StringBuilder();
		for (int i = 0; i < cells.size(); i++) {
			if (i > 0)
				line.append("  ");
			final String cell = cells.get(i).replace('\n', ' ');
			line.append(cell);
			if (i < cells.size() - 1)
				for (int n = cell.codePointCount(0, cell.length()); n < widths
						.get(i); n++)
					line.append(' ');
		}
		text.append(line.toString().replaceAll("\\s+$", "")).append('\n');
	}

	private static String currentStateId(final Animator animator) {
		final State state = animator.getCurrentState();
		return state == null ? "root" : state.getId();
	}

	/**
	 * gets the available tables from ProB in the background, then shows the
	 * table with the given command (if not <code>null</code>)
	 */
	private void loadCommands(final String then) {
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded()) {
			menus.setCommands(Collections.<CommandInfo> emptyList());
			setStatus("No model is being animated.", false);
			return;
		}
		final String stateId = currentStateId(animator);
		final Job job = new Job("ProB Table: available tables") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				List<CommandInfo> result;
				String error = null;
				try {
					result = GetCommandsCommand.getCommands(animator,
							Category.TABLE, stateId);
				} catch (ProBException e) {
					result = Collections.emptyList();
					error = "ProB could not list its tables: "
							+ e.getMessage();
				}
				final List<CommandInfo> loaded = result;
				final String loadError = error;
				asyncExec(new Runnable() {
					@Override
					public void run() {
						menus.setCommands(loaded);
						if (loadError != null)
							setStatus(loadError, true);
						else if (then != null)
							showTable(menus.find(then));
						else if (shown == null)
							setStatus("Choose a table in the toolbar menus. "
									+ COMPLETION_HINT, false);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/** gets the tables again, as they depend on the state, and the table */
	private void refresh() {
		loadCommands(shown == null ? null : shown.getCommand());
	}

	/**
	 * Shows the value of an expression as a table (ProB's Expression Table),
	 * e.g. for the State view's "Show Value as Table".
	 */
	public void showExpressionTable(final String expression) {
		expressionField.setText(expression);
		final CommandInfo command = menus.find(EXPRESSION_TABLE);
		if (command != null)
			showTable(command);
		else
			// the view was just opened and is still getting the tables
			loadCommands(EXPRESSION_TABLE);
	}

	/** computes a table in the background and shows it */
	private void showTable(final CommandInfo command) {
		if (command == null) {
			setStatus("This table is no longer available.", true);
			return;
		}
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded()) {
			setStatus("No model is being animated.", false);
			return;
		}
		shown = command;
		if (!command.isAvailable()) {
			setTable(Collections.<List<String>> emptyList());
			setStatus(command.getName() + " is not available: "
					+ command.getUnavailableReason(), true);
			return;
		}
		final List<PrologTerm> formulas = new ArrayList<PrologTerm>();
		String title = command.getName();
		if (command.getFormulaArguments() > 0) {
			final String text = expressionField.getText().trim();
			if (text.isEmpty()) {
				setTable(Collections.<List<String>> emptyList());
				setStatus(command.getName()
						+ " needs an expression: enter it above and press Enter. "
						+ COMPLETION_HINT, false);
				expressionField.setFocus();
				return;
			}
			try {
				formulas.add(CommandFormulas.parseExpression(animator, text));
			} catch (ProBParseException e) {
				setStatus("The expression has an error: " + e.getMessage(),
						true);
				expressionField.setFocus();
				return;
			}
			title = title.replaceFirst("\\.\\.\\.$", "") + " for " + text;
		}
		if (runningJob != null)
			runningJob.cancel();
		final String stateId = currentStateId(animator);
		final String shownTitle = title;
		setStatus("Computing " + title + " ...", false);
		final Job job = new Job("ProB Table: " + title) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				List<List<String>> result = null;
				String error = null;
				try {
					result = CallTableCommand.callTableCommand(animator,
							stateId, command.getCommand(), formulas);
				} catch (PrologException e) {
					// ProB's error messages, shown in the table
					error = "ProB could not compute " + shownTitle
							+ ". ProB reported:";
					result = new ArrayList<List<String>>();
					result.add(Collections.singletonList("Error"));
					for (String line : e.getMessage().split("\n"))
						if (!line.trim().isEmpty())
							result.add(Collections.singletonList(line));
				} catch (ProBException e) {
					error = "ProB could not compute " + shownTitle + ": "
							+ e.getMessage();
				}
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				final List<List<String>> computed = result;
				final String computeError = error;
				final Job self = this;
				asyncExec(new Runnable() {
					@Override
					public void run() {
						if (runningJob != self)
							return; // a newer table is being computed
						setRunningJob(null);
						if (computeError != null) {
							setTable(computed != null ? computed : Collections
									.<List<String>> emptyList());
							setStatus(computeError, true);
						} else {
							setTable(computed);
							setSavableTitle(shownTitle);
							final int n = rows.size();
							setStatus(shownTitle + " (state " + stateId
									+ "): " + n + (n == 1 ? " row" : " rows"),
									false);
						}
					}
				});
				return Status.OK_STATUS;
			}

			@Override
			protected void canceling() {
				Animator.getAnimator().sendUserInterruptSignal();
			}
		};
		setRunningJob(job);
		job.schedule();
	}

	/** the job computing the table; the Stop button is enabled while there is one */
	private void setRunningJob(final Job job) {
		runningJob = job;
		if (stop != null)
			stop.setEnabled(job != null);
	}

	private void asyncExec(final Runnable runnable) {
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (table != null && !table.isDisposed())
					runnable.run();
			}
		});
	}

	private void setStatus(final String message, final boolean error) {
		status.setText(message);
		status.setForeground(error ? status.getDisplay().getSystemColor(
				SWT.COLOR_RED) : null);
		status.getParent().layout();
	}

	/** @param table the rows; the first one has the column headers */
	private void setTable(final List<List<String>> table) {
		setSavableTitle(null);
		headers = table.isEmpty() ? Collections.<String> emptyList() : table
				.get(0);
		rows = table.isEmpty() ? Collections.<List<String>> emptyList()
				: new ArrayList<List<String>>(table.subList(1, table.size()));
		sortColumn = -1;
		this.table.setRedraw(false);
		try {
			this.table.removeAll();
			for (TableColumn column : this.table.getColumns())
				column.dispose();
			int columns = headers.size();
			for (List<String> row : rows)
				columns = Math.max(columns, row.size());
			for (int i = 0; i < columns; i++) {
				final TableColumn column = new TableColumn(this.table,
						SWT.LEFT);
				column.setText(i < headers.size() ? headers.get(i) : "");
				column.setToolTipText("Click to sort");
				final int index = i;
				column.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						sortBy(index);
					}
				});
			}
			this.table.setItemCount(rows.size());
			packColumns();
		} finally {
			this.table.setRedraw(true);
		}
	}

	/** sizes the columns for the headers and the first rows */
	private void packColumns() {
		final TableColumn[] columns = table.getColumns();
		final int sampleRows = Math.min(rows.size(), 200);
		final org.eclipse.swt.graphics.GC gc = new org.eclipse.swt.graphics.GC(
				table);
		try {
			gc.setFont(table.getFont());
			for (int i = 0; i < columns.length; i++) {
				int width = gc.textExtent(columns[i].getText()).x + 30;
				for (int r = 0; r < sampleRows; r++) {
					final List<String> row = rows.get(r);
					if (i < row.size())
						width = Math.max(width,
								gc.textExtent(row.get(i)).x + 16);
				}
				columns[i].setWidth(Math.min(width, 600));
			}
		} finally {
			gc.dispose();
		}
	}

	/** sorts by a column; again: the other direction */
	private void sortBy(final int column) {
		sortAscending = column == sortColumn ? !sortAscending : true;
		sortColumn = column;
		final Comparator<List<String>> comparator = new Comparator<List<String>>() {
			@Override
			public int compare(final List<String> a, final List<String> b) {
				final String x = column < a.size() ? a.get(column) : "";
				final String y = column < b.size() ? b.get(column) : "";
				final BigDecimal nx = number(x);
				final BigDecimal ny = number(y);
				if (nx != null && ny != null)
					return nx.compareTo(ny);
				return x.compareToIgnoreCase(y);
			}
		};
		Collections.sort(rows,
				sortAscending ? comparator : Collections.reverseOrder(comparator));
		table.setSortColumn(table.getColumn(column));
		table.setSortDirection(sortAscending ? SWT.UP : SWT.DOWN);
		table.clearAll();
	}

	private static BigDecimal number(final String text) {
		try {
			return new BigDecimal(text.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	/** copies the selected rows (or all) tab-separated, with the headers */
	private void copySelection() {
		int[] indices = table.getSelectionIndices();
		if (indices.length == 0) {
			indices = new int[rows.size()];
			for (int i = 0; i < indices.length; i++)
				indices[i] = i;
		}
		final StringBuilder text = new StringBuilder();
		text.append(String.join("\t", headers)).append('\n');
		for (int index : indices)
			text.append(String.join("\t", rows.get(index))).append('\n');
		clipboard.setContents(new Object[] { text.toString() },
				new Transfer[] { TextTransfer.getInstance() });
	}

	/** a new model: the tables shown belong to the old one */
	@Override
	public void reset() {
		asyncExec(new Runnable() {
			@Override
			public void run() {
				if (runningJob != null)
					runningJob.cancel();
				setRunningJob(null);
				shown = null;
				setTable(Collections.<List<String>> emptyList());
				loadCommands(null);
			}
		});
	}

	/**
	 * the state changed: whether the tables are available depends on it, e.g. some
	 * need an initialised state
	 */
	@Override
	public void currentStateChanged(final State currentState,
			final Operation operation) {
		menus.updateForCurrentState(Category.TABLE);
	}

	@Override
	public void setFocus() {
		if (table != null && !table.isDisposed())
			table.setFocus();
	}

	@Override
	public void dispose() {
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		menus.dispose();
		if (runningJob != null)
			runningJob.cancel();
		if (clipboard != null)
			clipboard.dispose();
		super.dispose();
	}
}
