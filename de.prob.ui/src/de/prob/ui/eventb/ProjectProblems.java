/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.eventb;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob.core.Animator;
import de.prob.logging.Logger;

/**
 * The errors and warnings (problem markers) of a Rodin project, e.g. of the
 * static checker, which can make the animation behave unexpectedly (e.g.
 * missing variables).
 */
public final class ProjectProblems {

	private static final String PROBLEMS_VIEW = "org.eclipse.ui.views.ProblemView";

	public final IProject project;
	public final int errors;
	public final int warnings;
	/** "file: message" for each error and warning, errors first */
	public final List<String> lines;
	/** the files' names, for each of the lines */
	private final List<String> files;

	private ProjectProblems(final IProject project, final int errors,
			final int warnings, final List<String> lines,
			final List<String> files) {
		this.project = project;
		this.errors = errors;
		this.warnings = warnings;
		this.lines = lines;
		this.files = files;
	}

	/** @return the errors and warnings of the project (infos are ignored) */
	public static ProjectProblems of(final IProject project) {
		final List<String> errorLines = new ArrayList<String>();
		final List<String> warningLines = new ArrayList<String>();
		final List<String> errorFiles = new ArrayList<String>();
		final List<String> warningFiles = new ArrayList<String>();
		try {
			for (IMarker marker : project.findMarkers(IMarker.PROBLEM, true,
					IResource.DEPTH_INFINITE)) {
				final int severity = marker.getAttribute(IMarker.SEVERITY,
						IMarker.SEVERITY_INFO);
				final String file = marker.getResource().getName();
				final String line = file + ": "
						+ marker.getAttribute(IMarker.MESSAGE, "unknown problem");
				if (severity == IMarker.SEVERITY_ERROR) {
					errorLines.add(line);
					errorFiles.add(file);
				} else if (severity == IMarker.SEVERITY_WARNING) {
					warningLines.add(line);
					warningFiles.add(file);
				}
			}
		} catch (CoreException e) {
			// e.g. the project was closed: no problems known
		}
		final List<String> lines = new ArrayList<String>(errorLines);
		lines.addAll(warningLines);
		final List<String> files = new ArrayList<String>(errorFiles);
		files.addAll(warningFiles);
		return new ProjectProblems(project, errorLines.size(),
				warningLines.size(), lines, files);
	}

	public boolean isEmpty() {
		return errors == 0 && warnings == 0;
	}

	/** stores the numbers in the animator, for the State view */
	public void storeInAnimator() {
		Animator.getAnimator().setRodinProjectProblems(errors, warnings);
	}

	/** @return e.g. "Project has 2 errors and 1 warning!" */
	public static String summary(final int errors, final int warnings) {
		if (errors > 0 && warnings > 0)
			return "Project has " + count(errors, "error") + " and "
					+ count(warnings, "warning") + "!";
		if (errors > 0)
			return "Project has " + count(errors, "error") + "!";
		if (warnings > 0)
			return "Project has " + count(warnings, "warning") + "!";
		return "Project has no errors or warnings";
	}

	private static String count(final int n, final String what) {
		return n + " " + what + (n == 1 ? "" : "s");
	}

	/**
	 * @return the problems, one per line, at most max of them: the errors,
	 *         then the warnings, each grouped by file
	 */
	public String details(final int max) {
		final StringBuilder result = new StringBuilder();
		appendGroup(result, "Errors", 0, errors, max);
		appendGroup(result, "Warnings", errors, lines.size(),
				max - Math.min(errors, max));
		if (lines.size() > max)
			result.append("... and ").append(lines.size() - max)
					.append(" more (see the Problems view)\n");
		return result.toString();
	}

	private void appendGroup(final StringBuilder result, final String title,
			final int from, final int to, final int max) {
		if (from >= to || max <= 0)
			return;
		result.append(title).append(" (").append(to - from).append("):\n");
		// by file, in the order of their first problem
		final Map<String, List<String>> byFile = new LinkedHashMap<String, List<String>>();
		for (int i = from; i < to; i++) {
			List<String> messages = byFile.get(files.get(i));
			if (messages == null) {
				messages = new ArrayList<String>();
				byFile.put(files.get(i), messages);
			}
			messages.add(lines.get(i).substring(files.get(i).length() + 2));
		}
		int shown = 0;
		for (Map.Entry<String, List<String>> file : byFile.entrySet()) {
			if (shown >= max)
				break;
			result.append("  ").append(file.getKey()).append(":\n");
			for (String message : file.getValue()) {
				if (shown++ >= max)
					break;
				result.append("    ").append(message).append('\n');
			}
		}
	}

	/**
	 * Shows the problems in a (non-modal) notice, with a link to the Problems
	 * view.
	 */
	public void notifyUser() {
		if (isEmpty()) {
			Logger.notifyUserNonModal("Project " + project.getName()
					+ " has no errors or warnings (any more)."
					+ " Double-click the State view's label to reload the animation.",
					null);
			return;
		}
		Logger.notifyUserNonModal(
				summary(errors, warnings).replace("Project", "Project "
						+ project.getName()).replace("!", ".")
						+ " This can lead to unexpected behaviour when animating with ProB"
						+ " (e.g. missing variables).",
				details(100), "Show Problems View", new Runnable() {
					@Override
					public void run() {
						showProblemsView();
					}
				});
	}

	/** opens Eclipse's Problems view */
	public static void showProblemsView() {
		final IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null)
			return;
		final IWorkbenchPage page = window.getActivePage();
		if (page == null)
			return;
		try {
			page.showView(PROBLEMS_VIEW);
		} catch (PartInitException e) {
			Logger.notifyUser("Could not open the Problems view", e);
		}
	}
}
