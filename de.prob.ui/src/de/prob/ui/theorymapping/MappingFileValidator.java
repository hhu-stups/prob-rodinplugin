/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import de.prob.core.Animator;
import de.prob.core.command.GetTheoryMappingTargetsCommand;
import de.prob.eventb.translator.TheoryMappingChecker;
import de.prob.eventb.translator.TheoryMappingChecker.Problem;
import de.prob.eventb.translator.TheoryMappingFiles;
import de.prob.eventb.translator.TheoryMappingFiles.Theory;
import de.prob.eventb.translator.TheoryMappingTemplate;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ProbUiPlugin;

/**
 * Checks the theory mapping files (.ptm) of the projects with
 * {@link TheoryMappingChecker} and shows the problems as markers, in the
 * editor and in the Problems view: when a file is added or changed (e.g.
 * saved), and when it is opened in the editor.
 */
public final class MappingFileValidator {

	/** the marker type, see plugin.xml */
	public static final String MARKER = ProbUiPlugin.PLUGIN_ID
			+ ".theoryMappingProblem";

	/** probcli's tags, asked once; null until asked */
	private static volatile TheoryMappingTemplate tags;

	private static final Set<IFile> PENDING = new LinkedHashSet<IFile>();

	private static final Job JOB = new Job("Checking ProB theory mapping files") {
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			while (true) {
				final IFile file;
				synchronized (PENDING) {
					if (PENDING.isEmpty())
						return Status.OK_STATUS;
					file = PENDING.iterator().next();
					PENDING.remove(file);
				}
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				try {
					check(file);
				} catch (CoreException e) {
					Logger.info("Could not check " + file.getFullPath() + ": "
							+ e.getMessage());
				}
			}
		}
	};

	static {
		JOB.setSystem(true);
	}

	private MappingFileValidator() {
	}

	/** checks the files when they are added or changed */
	public static void register() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				MappingFileValidator::resourceChanged,
				IResourceChangeEvent.POST_CHANGE);
	}

	/** checks the file in the background */
	public static void validate(final IFile file) {
		if (file == null || !MappingFilesContentProvider.isMappingFile(file))
			return;
		synchronized (PENDING) {
			PENDING.add(file);
		}
		JOB.schedule(200);
	}

	private static void resourceChanged(final IResourceChangeEvent event) {
		final IResourceDelta delta = event.getDelta();
		if (delta == null)
			return;
		// the .ptm files directly in a project, where ProB reads them
		for (IResourceDelta project : delta.getAffectedChildren())
			for (IResourceDelta d : project.getAffectedChildren(
					IResourceDelta.ADDED | IResourceDelta.CHANGED))
				if (d.getResource() instanceof IFile
						&& MappingFilesContentProvider.isMappingFile(d
								.getResource())
						&& (d.getKind() == IResourceDelta.ADDED || (d
								.getFlags() & IResourceDelta.CONTENT) != 0))
					validate((IFile) d.getResource());
	}

	private static void check(final IFile file) throws CoreException {
		if (!file.exists())
			return;
		final String text = read(file);
		final boolean theoryKnown = TheoryMappingFiles
				.isTheoryPluginInstalled();
		Theory theory = null;
		if (theoryKnown) {
			try {
				theory = TheoryMappingFiles.theoryOf(file);
			} catch (CoreException e) {
				Logger.info("Could not read the theory of "
						+ file.getFullPath() + ": " + e.getMessage());
			}
		}
		final String name = file.getFullPath().removeFileExtension()
				.lastSegment();
		final List<Problem> problems = TheoryMappingChecker.check(text, name,
				theory, theoryKnown, tags());
		final IWorkspace workspace = file.getWorkspace();
		workspace.run(monitor -> {
			if (!file.exists())
				return;
			file.deleteMarkers(MARKER, false, IResource.DEPTH_ZERO);
			for (Problem p : problems) {
				final IMarker marker = file.createMarker(MARKER);
				marker.setAttribute(IMarker.MESSAGE, p.message);
				marker.setAttribute(IMarker.SEVERITY, severity(p));
				marker.setAttribute(IMarker.LINE_NUMBER, p.line);
				if (p.start >= 0) {
					marker.setAttribute(IMarker.CHAR_START, p.start);
					marker.setAttribute(IMarker.CHAR_END, p.end);
				}
				marker.setAttribute(IMarker.LOCATION, "line " + p.line);
			}
		}, workspace.getRuleFactory().markerRule(file), IWorkspace.AVOID_UPDATE,
				null);
	}

	private static int severity(final Problem p) {
		switch (p.severity) {
		case ERROR:
			return IMarker.SEVERITY_ERROR;
		case WARNING:
			return IMarker.SEVERITY_WARNING;
		default:
			return IMarker.SEVERITY_INFO;
		}
	}

	private static String read(final IFile file) throws CoreException {
		try (InputStream in = file.getContents(true)) {
			Charset cs;
			try {
				cs = Charset.forName(file.getCharset());
			} catch (IllegalArgumentException e) {
				cs = Charset.forName("UTF-8");
			}
			return new String(in.readAllBytes(), cs);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR,
					ProbUiPlugin.PLUGIN_ID, "Could not read "
							+ file.getFullPath() + ": " + e.getMessage(), e));
		}
	}

	/**
	 * @return probcli's tags and defaults, asked once (they only change with
	 *         probcli); without them if probcli cannot tell them
	 */
	static TheoryMappingTemplate tags() {
		TheoryMappingTemplate t = tags;
		if (t != null)
			return t;
		final GetTheoryMappingTargetsCommand command = new GetTheoryMappingTargetsCommand();
		try {
			Animator.getAnimator().executeQuietly(command);
			t = new TheoryMappingTemplate(command.getTargets(),
					command.getDefaults());
		} catch (ProBException e) {
			// an older probcli without get_theory_mapping_targets/2
			Logger.info("probcli cannot list the tags of theory mapping files: "
					+ e.getMessage());
			t = new TheoryMappingTemplate(null, null);
		}
		tags = t;
		return t;
	}
}
