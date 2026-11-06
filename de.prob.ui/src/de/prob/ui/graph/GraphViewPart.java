package de.prob.ui.graph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.PrologException;
import de.prob.core.command.CallDotCommand;
import de.prob.core.command.CommandFormulas;
import de.prob.core.command.GetCommandsCommand;
import de.prob.core.command.GetCommandsCommand.Category;
import de.prob.core.command.GetCommandsCommand.CommandInfo;
import de.prob.core.command.GetCurrentPreferencesCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.CommandMenus;
import de.prob.ui.StaticListenerRegistry;
import de.prob.ui.console.ConsoleViewPart;
import de.prob.ui.stateview.LoadShortestTraceHandler;

/**
 * Shows the graphs that ProB can compute in the current state, e.g. the state
 * space or a formula as a tree. The graphs are offered in one toolbar
 * drop-down menu per group. ProB writes a GraphViz dot file, GraphViz lays it
 * out as SVG, and an embedded browser shows it with zoom and pan.
 * Double-clicking a state of the state space makes it the current state.
 * Graphs that need a formula take it from the field above the graph.
 */
public class GraphViewPart extends ViewPart implements ILifecycleListener,
		IAnimationListener {

	public static final String VIEW_ID = "de.prob.ui.GraphView";

	// the menus' names for ProB's groups, in this order; others follow,
	// named after the group
	private static final Map<String, String> GROUP_LABELS = new LinkedHashMap<String, String>();
	static {
		GROUP_LABELS.put("state_space_visualisation", "State Space");
		GROUP_LABELS.put("state_visualisation", "State");
		GROUP_LABELS.put("formula_visualisation", "Formula");
		GROUP_LABELS.put("trace_visualisation", "Trace");
		GROUP_LABELS.put("data_flow_info", "Data Flow");
		GROUP_LABELS.put("machine_info", "Machine");
		GROUP_LABELS.put("debug_info", "Debug");
	}

	// groups of ProB that are not offered; edit to show them
	private static final Set<String> HIDDEN_GROUPS = new HashSet<String>(
			Arrays.asList("debug_info"));
	// commands of ProB that are not offered, as they do not apply to Event-B
	// models in Rodin; edit to show them
	private static final Set<String> HIDDEN_COMMANDS = new HashSet<String>(
			Arrays.asList(//
					"definitions", // B and Z only
					"operations", // classical B only
					"custom_graph", // needs DEFINITIONS of a B machine
					"goal", // needs a GOAL definition
					"last_mcts_tree", // needs GAME_OVER, ... definitions
					"rule_dependency_graph", // rules machines only
					"proof_tree_graph" // sequent prover only
			));

	// graphs whose nodes are states, named by their ids
	private static final Set<String> STATE_GRAPHS = new HashSet<String>(
			Arrays.asList("state_space", "state_space_sfdp", "current_state",
					"history"));

	/** ProB's command for the value of an expression as a graph */
	public static final String EXPRESSION_GRAPH = "expr_as_graph";
	/** ProB's command for the state space projected onto an expression */
	public static final String EXPRESSION_PROJECTION = "transition_diagram";

	// shown when Enter is pressed and the current graph needs no formula
	private static final String DEFAULT_FORMULA_GRAPH = EXPRESSION_PROJECTION;

	// the ProB preference with the path of GraphViz's dot program
	private static final String DOT_PREFERENCE = "DOT";
	// tried if the preference does not work: Rodin started from the macOS
	// Finder does not see the PATH of a shell
	private static final String[] DOT_LOCATIONS = { "dot",
			"/opt/homebrew/bin/dot", "/usr/local/bin/dot", "/usr/bin/dot",
			"C:/Program Files/Graphviz/bin/dot.exe" };
	private static final long DOT_TIMEOUT_SECONDS = 120;

	private static final String COMPLETION_HINT = "Tab or Ctrl+Space in the formula field completes identifiers.";

	/** for messages, e.g. ProB's errors, instead of a graph */
	private static final String MESSAGE_PAGE = "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><style>"
			+ "body { margin: 0; background: #ffffff; font-family: sans-serif; }"
			+ "#message { padding: 12px; color: #555555; white-space: pre-wrap; }"
			+ "#message.error { color: #b00000; }"
			+ "</style></head><body>%CONTENT%</body></html>";

	private Browser browser;
	private Text formulaField;
	private String template;
	private Job runningJob;
	private Action stop;
	private Action save;
	/** the graph shown, for Save; null while a message is shown */
	private Graph shownGraph;
	// the menus with the graphs from ProB
	private CommandMenus menus;
	private CommandInfo shown;

	@Override
	public void createPartControl(final Composite parent) {
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 2;
		parent.setLayout(layout);
		createFormulaField(parent);
		final Composite browserArea = new Composite(parent, SWT.NONE);
		browserArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		browserArea.setLayout(new FillLayout());
		try {
			browser = new Browser(browserArea, SWT.NONE);
		} catch (SWTError e) {
			// e.g. no WebKitGTK on Linux
			final Label label = new Label(browserArea, SWT.WRAP);
			label.setText("The ProB Graph view needs an embedded web browser, which is not available: "
					+ e.getMessage());
			return;
		}
		template = loadTemplate();
		new BrowserFunction(browser, "probGotoState") {
			@Override
			public Object function(final Object[] arguments) {
				if (arguments.length == 1 && arguments[0] instanceof String)
					gotoState((String) arguments[0]);
				return null;
			}
		};
		createActions();
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
		StaticListenerRegistry.registerListener((IAnimationListener) this);
		loadCommands(null);
	}

	/** the formula for graphs that need one; Enter shows the graph again */
	private void createFormulaField(final Composite parent) {
		final Composite row = new Composite(parent, SWT.NONE);
		row.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		final GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 2;
		row.setLayout(layout);
		final Label label = new Label(row, SWT.NONE);
		label.setText("Formula:");
		formulaField = new Text(row, SWT.SINGLE | SWT.BORDER);
		formulaField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		formulaField
				.setMessage("for the graphs ending in \"...\", e.g. State Space Expression Projection...");
		formulaField
				.setToolTipText("The expression or predicate for the graphs that need one (their names end in \"...\")."
						+ "\nEnter: show that graph again, or the State Space Expression Projection"
						+ "\nTab or Ctrl+Space: complete identifiers");
		formulaField.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetDefaultSelected(final SelectionEvent e) {
				if (shown != null && shown.getFormulaArguments() > 0)
					showGraph(shown);
				else if (menus != null)
					showGraph(menus.find(DEFAULT_FORMULA_GRAPH));
			}
		});
		ConsoleViewPart.installMathSymbols(formulaField);
		ConsoleViewPart.installCompletion(formulaField);
	}

	private void createActions() {
		final IToolBarManager toolBar = getViewSite().getActionBars()
				.getToolBarManager();
		menus = new CommandMenus(getViewSite(), "de.prob.ui.graph", "graphs",
				GROUP_LABELS, HIDDEN_GROUPS, HIDDEN_COMMANDS,
				new CommandMenus.Handler() {
					@Override
					public void run(final CommandInfo command) {
						showGraph(command);
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
		refresh.setToolTipText("Compute the graph again (e.g. after animating or model checking)");
		toolBar.add(refresh);
		stop = new Action("Stop") {
			@Override
			public void run() {
				if (runningJob == null)
					return;
				runningJob.cancel();
				setRunningJob(null);
				showMessage("Stopped. Choose a graph or press Refresh to compute it again.");
			}
		};
		stop.setToolTipText("Stop computing the graph: interrupts ProB, or GraphViz if ProB has finished");
		final ISharedImages images = PlatformUI.getWorkbench()
				.getSharedImages();
		stop.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP));
		stop.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ELCL_STOP_DISABLED));
		stop.setEnabled(false);
		toolBar.add(stop);
		toolBar.add(new Separator());
		toolBar.add(new Action("+") {
			{
				setToolTipText("Zoom In");
			}

			@Override
			public void run() {
				execute("window.probZoom && probZoom(1.25);");
			}
		});
		toolBar.add(new Action("\u2212") {
			{
				setToolTipText("Zoom Out");
			}

			@Override
			public void run() {
				execute("window.probZoom && probZoom(0.8);");
			}
		});
		toolBar.add(new Action("Fit") {
			{
				setToolTipText("Fit the Graph into the View");
			}

			@Override
			public void run() {
				execute("window.probFit && probFit();");
			}
		});
		toolBar.add(new Separator());
		save = new Action("Save Graph As...") {
			@Override
			public void run() {
				saveGraph();
			}
		};
		save.setToolTipText("Save the graph as an SVG image (.svg) or as a GraphViz file (.dot)");
		save.setImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT));
		save.setDisabledImageDescriptor(images
				.getImageDescriptor(ISharedImages.IMG_ETOOL_SAVEAS_EDIT_DISABLED));
		save.setEnabled(false);
		toolBar.add(save);
	}

	private void setShownGraph(final Graph graph) {
		shownGraph = graph;
		if (save != null)
			save.setEnabled(graph != null);
	}

	/** saves the shown graph as .svg or .dot, chosen by the file's extension */
	private void saveGraph() {
		final Graph graph = shownGraph;
		if (graph == null)
			return;
		final FileDialog dialog = new FileDialog(getSite().getShell(),
				SWT.SAVE);
		dialog.setText("Save Graph As");
		dialog.setFilterExtensions(new String[] { "*.svg", "*.dot" });
		dialog.setFilterNames(new String[] { "SVG image (*.svg)",
				"GraphViz dot file (*.dot)" });
		dialog.setFileName(fileName(graph.title) + ".svg");
		dialog.setOverwrite(true);
		String path = dialog.open();
		if (path == null)
			return;
		final String lower = path.toLowerCase(Locale.ROOT);
		final boolean dot;
		if (lower.endsWith(".dot") || lower.endsWith(".gv"))
			dot = true;
		else if (lower.endsWith(".svg"))
			dot = false;
		else {
			// no known extension: the chosen file type's
			dot = dialog.getFilterIndex() == 1;
			path += dot ? ".dot" : ".svg";
		}
		try {
			Files.write(new File(path).toPath(), (dot ? graph.dot : graph.svg)
					.getBytes(StandardCharsets.UTF_8));
			getViewSite().getActionBars().getStatusLineManager()
					.setMessage("Saved the graph to " + path);
		} catch (IOException e) {
			MessageDialog.openError(getSite().getShell(), "Save Graph As",
					"Could not save the graph to " + path + ": "
							+ e.getMessage());
		}
	}

	/** @return the title without characters that are not allowed in files */
	public static String fileName(final String title) {
		final String name = title.replaceAll("[\\\\/:*?\"<>|\\s]+", "_")
				.replaceAll("_+$", "").replaceAll("^_+", "");
		return name.isEmpty() ? "graph"
				: name.length() > 60 ? name.substring(0, 60) : name;
	}

	private void execute(final String script) {
		if (browser != null && !browser.isDisposed())
			browser.execute(script);
	}

	private static String currentStateId(final Animator animator) {
		final State state = animator.getCurrentState();
		return state == null ? "root" : state.getId();
	}

	/**
	 * gets the available graphs from ProB in the background, then shows the
	 * graph with the given command (if not <code>null</code>)
	 */
	private void loadCommands(final String then) {
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded()) {
			menus.setCommands(Collections.<CommandInfo> emptyList());
			showMessage("No model is being animated.");
			return;
		}
		final String stateId = currentStateId(animator);
		final Job job = new Job("ProB Graph: available graphs") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				List<CommandInfo> result;
				String error = null;
				try {
					result = GetCommandsCommand.getCommands(animator,
							Category.DOT, stateId);
				} catch (ProBException e) {
					result = Collections.emptyList();
					error = failure("ProB could not list its graphs", e);
				}
				final List<CommandInfo> loaded = result;
				final String loadError = error;
				asyncExec(new Runnable() {
					@Override
					public void run() {
						menus.setCommands(loaded);
						if (loadError != null)
							showError(loadError);
						else if (then != null)
							showGraph(menus.find(then));
						else if (shown == null)
							showMessage("Choose a graph in the toolbar menus, e.g. State Space \u203a State Space.\n"
									+ "Mouse wheel: zoom, drag: move, double-click on a state: go to that state.\n"
									+ COMPLETION_HINT);
					}
				});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	/** gets the graphs again, as they depend on the state, and the graph */
	private void refresh() {
		if (browser == null || browser.isDisposed())
			return;
		loadCommands(shown == null ? null : shown.getCommand());
	}

	/**
	 * Shows a graph for a formula, e.g. for the State view's "Show as Graph".
	 * 
	 * @param command
	 *            ProB's command, e.g. {@link #EXPRESSION_GRAPH}
	 */
	public void showFormulaGraph(final String command, final String formula) {
		if (menus == null)
			return; // no browser
		formulaField.setText(formula);
		final CommandInfo info = menus.find(command);
		if (info != null)
			showGraph(info);
		else
			// the view was just opened and is still getting the graphs
			loadCommands(command);
	}

	/** computes a graph in the background and shows it */
	private void showGraph(final CommandInfo command) {
		if (browser == null || browser.isDisposed())
			return;
		if (command == null) {
			showError("This graph is not available.");
			return;
		}
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded()) {
			showMessage("No model is being animated.");
			return;
		}
		shown = command;
		if (!command.isAvailable()) {
			showError(command.getName() + " is not available: "
					+ command.getUnavailableReason());
			return;
		}
		final List<PrologTerm> formulas = new ArrayList<PrologTerm>();
		String title = command.getName();
		if (command.getFormulaArguments() > 0) {
			final String text = formulaField.getText().trim();
			if (text.isEmpty()) {
				showMessage(command.getName()
						+ " needs a formula: enter it above the graph and press Enter.\n"
						+ COMPLETION_HINT);
				formulaField.setFocus();
				return;
			}
			try {
				formulas.add(CommandFormulas.parseFormula(animator, text));
			} catch (ProBParseException e) {
				showError("The formula has an error: " + e.getMessage());
				formulaField.setFocus();
				return;
			}
			title = title.replaceFirst("\\.\\.\\.$", "") + " for " + text;
		}
		if (runningJob != null)
			runningJob.cancel();
		final String stateId = currentStateId(animator);
		final boolean clickable = STATE_GRAPHS.contains(command.getCommand());
		final String shownTitle = title;
		showMessage("ProB is computing " + title
				+ " ...\nThe red Stop button interrupts it.");
		final Job job = new Job("ProB Graph: " + title) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				final Job self = this;
				final Runnable layingOut = new Runnable() {
					@Override
					public void run() {
						asyncExec(new Runnable() {
							@Override
							public void run() {
								if (runningJob == self)
									showMessage("GraphViz is laying out "
											+ shownTitle
											+ " ...\nThe red Stop button interrupts it.");
							}
						});
					}
				};
				Graph graph = null;
				String content;
				try {
					graph = computeGraph(animator, stateId, command, formulas,
							shownTitle, layingOut, monitor);
					content = graph.svgForHtml();
				} catch (GraphException e) {
					content = e.getMessage();
				}
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				show(this, content, graph, clickable);
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

	/** the job computing the graph; the Stop button is enabled while there is one */
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
				if (browser != null && !browser.isDisposed())
					runnable.run();
			}
		});
	}

	/**
	 * @return the message for a failed ProB command: ProB's error messages
	 *         below, or the reason after <code>what</code>
	 */
	private static String failure(final String what, final ProBException e) {
		if (e instanceof PrologException)
			return what + ". ProB reported:\n\n" + e.getMessage();
		return what + ": " + e.getMessage();
	}

	private static final class GraphException extends Exception {
		private static final long serialVersionUID = 1L;

		GraphException(final String message) {
			super(message);
		}
	}

	/** a computed graph: ProB's dot file and its SVG laid out by GraphViz */
	private static final class Graph {
		final String title;
		final String dot;
		final String svg;

		Graph(final String title, final String dot, final String svg) {
			this.title = title;
			this.dot = dot;
			this.svg = svg;
		}

		/** without the XML declaration and DOCTYPE, to embed it in HTML */
		String svgForHtml() {
			final int start = svg.indexOf("<svg");
			return start < 0 ? svg : svg.substring(start);
		}
	}

	/** @return the graph, laid out by GraphViz */
	private static Graph computeGraph(final Animator animator,
			final String stateId, final CommandInfo command,
			final List<PrologTerm> formulas, final String title,
			final Runnable layingOut, final IProgressMonitor monitor)
			throws GraphException {
		File dotFile = null;
		File svgFile = null;
		try {
			dotFile = File.createTempFile("prob_graph", ".dot");
			svgFile = File.createTempFile("prob_graph", ".svg");
			try {
				CallDotCommand.writeDot(animator, stateId,
						command.getCommand(), formulas, dotFile);
			} catch (ProBException e) {
				throw new GraphException(failure("ProB could not compute "
						+ command.getName(), e));
			}
			if (monitor.isCanceled())
				throw new GraphException("Cancelled.");
			layingOut.run();
			runDot(animator, command, dotFile, svgFile, monitor);
			return new Graph(title, new String(Files.readAllBytes(dotFile
					.toPath()), StandardCharsets.UTF_8), new String(
					Files.readAllBytes(svgFile.toPath()), StandardCharsets.UTF_8));
		} catch (IOException e) {
			throw new GraphException("Could not create the graph: "
					+ e.getMessage());
		} finally {
			if (dotFile != null)
				dotFile.delete();
			if (svgFile != null)
				svgFile.delete();
		}
	}

	private static void runDot(final Animator animator,
			final CommandInfo command, final File dotFile, final File svgFile,
			final IProgressMonitor monitor) throws GraphException {
		final List<String> candidates = new ArrayList<String>();
		try {
			final String preference = GetCurrentPreferencesCommand
					.getCurrentPreferences(animator).get(DOT_PREFERENCE);
			if (preference != null && !preference.trim().isEmpty())
				candidates.add(preference.trim());
		} catch (ProBException e) {
			// use the default locations
		}
		for (String location : DOT_LOCATIONS)
			if (!candidates.contains(location))
				candidates.add(location);

		for (String dot : candidates) {
			final List<String> arguments = new ArrayList<String>();
			arguments.add(dot);
			// the layout ProB suggests, e.g. sfdp for large state spaces
			if (command.getPreferredDotType() != null)
				arguments.add("-K" + command.getPreferredDotType());
			arguments.addAll(command.getExtraArguments());
			arguments.addAll(Arrays.asList("-Tsvg", "-o",
					svgFile.getAbsolutePath(), dotFile.getAbsolutePath()));
			final Process process;
			try {
				process = new ProcessBuilder(arguments).redirectErrorStream(
						true).start();
			} catch (IOException e) {
				continue; // not there, try the next location
			}
			final String output = waitFor(process, monitor);
			if (process.exitValue() != 0)
				throw new GraphException("GraphViz (" + dot
						+ ") failed with exit code " + process.exitValue()
						+ ":\n" + output);
			return;
		}
		throw new GraphException(
				"GraphViz's dot program was not found. Please install GraphViz (https://graphviz.org)"
						+ " or set the path of dot in ProB's preference DOT (advanced preferences).\n"
						+ "Tried: " + String.join(", ", candidates));
	}

	/** @return the output of the process */
	private static String waitFor(final Process process,
			final IProgressMonitor monitor) throws GraphException {
		final ByteArrayOutputStream output = new ByteArrayOutputStream();
		final Thread reader = new Thread(new Runnable() {
			@Override
			public void run() {
				try (InputStream in = process.getInputStream()) {
					final byte[] buffer = new byte[4096];
					int n;
					while ((n = in.read(buffer)) > 0)
						output.write(buffer, 0, n);
				} catch (IOException e) {
					// the process ended
				}
			}
		}, "ProB Graph: dot output");
		reader.setDaemon(true);
		reader.start();
		final long deadline = System.currentTimeMillis()
				+ TimeUnit.SECONDS.toMillis(DOT_TIMEOUT_SECONDS);
		try {
			while (!process.waitFor(200, TimeUnit.MILLISECONDS)) {
				if (monitor.isCanceled()) {
					process.destroyForcibly();
					throw new GraphException("Cancelled.");
				}
				if (System.currentTimeMillis() > deadline) {
					process.destroyForcibly();
					throw new GraphException("GraphViz did not finish within "
							+ DOT_TIMEOUT_SECONDS
							+ " seconds; the graph is probably too large."
							+ " Try State Space \u203a Signature Merge or State Space (Fast) instead.");
				}
			}
			reader.join(1000);
		} catch (InterruptedException e) {
			process.destroyForcibly();
			Thread.currentThread().interrupt();
			throw new GraphException("Interrupted.");
		}
		return new String(output.toByteArray(), StandardCharsets.UTF_8);
	}

	private void show(final Job job, final String content,
			final Graph graph, final boolean statesClickable) {
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (browser == null || browser.isDisposed())
					return;
				if (runningJob == job)
					setRunningJob(null);
				else
					return; // a newer graph is being computed
				if (graph != null) {
					browser.setText(template.replace("%CLICKABLE%",
							Boolean.toString(statesClickable)).replace(
							"%CONTENT%", content));
					setShownGraph(graph);
				} else
					showError(content);
			}
		});
	}

	private void gotoState(final String stateId) {
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded())
			return;
		// after the browser's event, so that it is not blocked meanwhile
		browser.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				LoadShortestTraceHandler.loadShortestTraceTo(animator, stateId);
			}
		});
	}

	private void showMessage(final String message) {
		setHtml("<div id=\"message\">" + escape(message) + "</div>");
	}

	private void showError(final String message) {
		setHtml("<div id=\"message\" class=\"error\">" + escape(message)
				+ "</div>");
	}

	/**
	 * shows a message on a plain page, which scrolls and whose text can be
	 * selected (unlike a graph, which is moved by dragging)
	 */
	private void setHtml(final String content) {
		setShownGraph(null);
		if (browser != null && !browser.isDisposed())
			browser.setText(MESSAGE_PAGE.replace("%CONTENT%", content));
	}

	private static String escape(final String text) {
		return text.replace("&", "&amp;").replace("<", "&lt;")
				.replace(">", "&gt;");
	}

	private String loadTemplate() {
		try (InputStream in = GraphViewPart.class
				.getResourceAsStream("graph.html")) {
			if (in != null)
				return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			// use the minimal page below
		}
		return "<html><body>%CONTENT%<script>var x=%CLICKABLE%;</script></body></html>";
	}

	/** a new model: the graph shown belongs to the old one */
	@Override
	public void reset() {
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (browser == null || browser.isDisposed())
					return;
				if (runningJob != null)
					runningJob.cancel();
				setRunningJob(null);
				shown = null;
				loadCommands(null);
			}
		});
	}

	/**
	 * the state changed: whether the graphs are available depends on it, e.g. some
	 * need an initialised state
	 */
	@Override
	public void currentStateChanged(final State currentState,
			final Operation operation) {
		menus.updateForCurrentState(Category.DOT);
	}

	@Override
	public void setFocus() {
		if (browser != null && !browser.isDisposed())
			browser.setFocus();
	}

	@Override
	public void dispose() {
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		menus.dispose();
		if (runningJob != null)
			runningJob.cancel();
		super.dispose();
	}
}
