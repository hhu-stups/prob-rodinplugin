package de.prob.ui.graph;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.BrowserFunction;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import de.prob.core.Animator;
import de.prob.core.ILifecycleListener;
import de.prob.core.command.GetCurrentPreferencesCommand;
import de.prob.core.command.WriteDotCommand;
import de.prob.core.command.WriteDotCommand.Graph;
import de.prob.exceptions.ProBException;
import de.prob.ui.StaticListenerRegistry;
import de.prob.ui.stateview.LoadShortestTraceHandler;

/**
 * Shows graphs of the state space computed by ProB (prototype): ProB writes a
 * GraphViz dot file, GraphViz's dot program lays it out as SVG, and an
 * embedded browser shows it with zoom and pan. Double-clicking a state of the
 * state space makes it the current state.
 */
public class GraphViewPart extends ViewPart implements ILifecycleListener {

	public static final String VIEW_ID = "de.prob.ui.GraphView";

	// the ProB preference with the path of GraphViz's dot program
	private static final String DOT_PREFERENCE = "DOT";
	// tried if the preference does not work: Rodin started from the macOS
	// Finder does not see the PATH of a shell
	private static final String[] DOT_LOCATIONS = { "dot",
			"/opt/homebrew/bin/dot", "/usr/local/bin/dot", "/usr/bin/dot",
			"C:/Program Files/Graphviz/bin/dot.exe" };
	private static final long DOT_TIMEOUT_SECONDS = 120;

	private Browser browser;
	private String template;
	private Graph graph = Graph.STATE_SPACE;
	private Job runningJob;
	private final List<Action> graphActions = new ArrayList<Action>();

	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(new FillLayout());
		try {
			browser = new Browser(parent, SWT.NONE);
		} catch (SWTError e) {
			// e.g. no WebKitGTK on Linux
			final Label label = new Label(parent, SWT.WRAP);
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
		showMessage("Choose a graph in the toolbar, e.g. State Space, to show it.\n"
				+ "Mouse wheel: zoom, drag: move, double-click on a state: go to that state.");
		createActions();
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
	}

	private void createActions() {
		final IToolBarManager toolBar = getViewSite().getActionBars()
				.getToolBarManager();
		for (final Graph g : Graph.values()) {
			final Action action = new Action(g.getLabel(),
					IAction.AS_RADIO_BUTTON) {
				@Override
				public void run() {
					if (isChecked()) {
						graph = g;
						refresh();
					}
				}
			};
			action.setToolTipText("Show the " + g.getLabel());
			action.setChecked(g == graph);
			graphActions.add(action);
			toolBar.add(action);
		}
		toolBar.add(new Separator());
		final Action refresh = new Action("Refresh") {
			@Override
			public void run() {
				refresh();
			}
		};
		refresh.setToolTipText("Compute the graph again (e.g. after animating or model checking)");
		toolBar.add(refresh);
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
	}

	private void execute(final String script) {
		if (browser != null && !browser.isDisposed())
			browser.execute(script);
	}

	/** computes the chosen graph in the background and shows it */
	private void refresh() {
		if (browser == null || browser.isDisposed())
			return;
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded()) {
			showMessage("No model is being animated.");
			return;
		}
		if (runningJob != null)
			runningJob.cancel();
		final Graph shown = graph;
		showMessage("Computing the " + shown.getLabel() + " ...");
		final Job job = new Job("ProB Graph: " + shown.getLabel()) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				String content;
				boolean isSvg = false;
				try {
					content = computeSvg(animator, shown, monitor);
					isSvg = true;
				} catch (GraphException e) {
					content = e.getMessage();
				}
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				show(this, content, isSvg, shown == Graph.STATE_SPACE);
				return Status.OK_STATUS;
			}

			@Override
			protected void canceling() {
				Animator.getAnimator().sendUserInterruptSignal();
			}
		};
		runningJob = job;
		job.schedule();
	}

	private static final class GraphException extends Exception {
		private static final long serialVersionUID = 1L;

		GraphException(final String message) {
			super(message);
		}
	}

	/** @return the SVG of the graph, laid out by GraphViz */
	private static String computeSvg(final Animator animator,
			final Graph graph, final IProgressMonitor monitor)
			throws GraphException {
		File dotFile = null;
		File svgFile = null;
		try {
			dotFile = File.createTempFile("prob_graph", ".dot");
			svgFile = File.createTempFile("prob_graph", ".svg");
			try {
				WriteDotCommand.writeDot(animator, graph, dotFile);
			} catch (ProBException e) {
				throw new GraphException("ProB could not compute the graph: "
						+ e.getMessage());
			}
			if (monitor.isCanceled())
				throw new GraphException("Cancelled.");
			runDot(animator, dotFile, svgFile, monitor);
			final String svg = new String(Files.readAllBytes(svgFile.toPath()),
					StandardCharsets.UTF_8);
			// without the XML declaration and DOCTYPE, to embed it in HTML
			final int start = svg.indexOf("<svg");
			return start < 0 ? svg : svg.substring(start);
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

	private static void runDot(final Animator animator, final File dotFile,
			final File svgFile, final IProgressMonitor monitor)
			throws GraphException {
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
			final Process process;
			try {
				process = new ProcessBuilder(dot, "-Tsvg", "-o",
						svgFile.getAbsolutePath(), dotFile.getAbsolutePath())
						.redirectErrorStream(true).start();
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
							+ " Try the Signature Merge instead.");
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
			final boolean isSvg, final boolean statesClickable) {
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				if (browser == null || browser.isDisposed())
					return;
				if (runningJob == job)
					runningJob = null;
				else
					return; // a newer graph is being computed
				if (isSvg)
					browser.setText(template.replace("%CLICKABLE%",
							Boolean.toString(statesClickable)).replace(
							"%CONTENT%", content));
				else
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

	private void setHtml(final String content) {
		if (browser != null && !browser.isDisposed())
			browser.setText(template.replace("%CLICKABLE%", "false").replace(
					"%CONTENT%", content));
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
				if (browser != null && !browser.isDisposed())
					showMessage("A new model was loaded. Press Refresh to show its "
							+ graph.getLabel() + ".");
			}
		});
	}

	@Override
	public void setFocus() {
		if (browser != null && !browser.isDisposed())
			browser.setFocus();
	}

	@Override
	public void dispose() {
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		if (runningJob != null)
			runningJob.cancel();
		super.dispose();
	}
}
