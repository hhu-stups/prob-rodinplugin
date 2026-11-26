/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.theorymapping;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import de.prob.core.Animator;
import de.prob.core.command.GetTheoryMappingTargetsCommand;
import de.prob.core.command.GetTheoryMappingTargetsCommand.Default;
import de.prob.core.command.GetTheoryMappingTargetsCommand.Target;
import de.prob.eventb.translator.TheoryMappingFiles;
import de.prob.eventb.translator.TheoryMappingFiles.Theory;
import de.prob.eventb.translator.TheoryMappingLinks;
import de.prob.eventb.translator.TheoryMappingTemplate;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * Creates the theory mapping files (.ptm) of the theories a project uses, or
 * that are deployed in it: for each theory with axiomatic operators or types,
 * <code>T.ptm</code> in the theory's project, where ProB looks for it. A new
 * file has a line for each operator, with ProB's default if there is one
 * (see {@link TheoryMappingTemplate}); an existing file gets the operators it
 * does not map yet at its end. The files are opened in the text editor.
 */
public final class CreateTheoryMappingFiles {

	private static final String TEXT_EDITOR = "org.eclipse.ui.DefaultTextEditor";

	private CreateTheoryMappingFiles() {
	}

	/** makes the links in notices with ProB's messages work */
	public static void register() {
		TheoryMappingLinks.setCreator(CreateTheoryMappingFiles::run);
	}

	/** creates the files in a job; can be called from any thread */
	public static void run(final IProject project) {
		if (!TheoryMappingFiles.isTheoryPluginInstalled()) {
			Logger.notifyUserNonModal(
					"The Theory plug-in is not installed: there are no theories to create ProB mapping files for.",
					null);
			return;
		}
		final Job job = new Job("Creating ProB theory mapping files for "
				+ project.getName()) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					createFiles(project);
				} catch (CoreException e) {
					Logger.notifyUserNonModal(
							"Could not create the ProB theory mapping files for "
									+ project.getName() + ".", e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		// no scheduling rule: probcli may first have to finish another
		// command; only writing the files locks the workspace
		job.schedule();
	}

	private static void createFiles(final IProject project)
			throws CoreException {
		final List<Theory> theories = TheoryMappingFiles.theoriesOf(project);
		if (theories.isEmpty()) {
			Logger.notifyUserNonModal("The theories used by " + project.getName()
					+ " have no axiomatic operators or types: they need no ProB mapping file.",
					null);
			return;
		}
		final TheoryMappingTemplate template = template();
		final String date = LocalDate.now().toString();
		final List<IFile> changed = new ArrayList<IFile>();
		final StringBuilder details = new StringBuilder();
		ResourcesPlugin.getWorkspace().run(new IWorkspaceRunnable() {
			@Override
			public void run(final IProgressMonitor monitor) {
				for (Theory theory : theories) {
					final IFile file = theory.mappingFile();
					try {
						if (!file.exists()) {
							final String text = template.newFile(theory.name,
									theory.project.getName(), theory.operators,
									date);
							// no setCharset: it would change the project's
							// settings and make Rodin rebuild the project
							file.create(bytes(text, theory.project
									.getDefaultCharset()), true, null);
							changed.add(file);
							details.append("Created ").append(file.getFullPath())
									.append('\n');
							continue;
						}
						final Set<String> mapped = TheoryMappingFiles
								.mappedOperators(theory);
						final String additions = template.additions(
								theory.operators, mapped, date);
						if (additions == null) {
							details.append(file.getFullPath())
									.append(" already maps all axiomatic operators and types\n");
							continue;
						}
						file.appendContents(
								bytes(additions, file.getCharset()), true,
								true, null);
						changed.add(file);
						details.append("Added the missing operators to ")
								.append(file.getFullPath()).append('\n');
					} catch (CoreException e) {
						details.append("Could not create or change ")
								.append(file.getFullPath()).append(": ")
								.append(e.getMessage()).append('\n');
					}
				}
			}
		}, ResourcesPlugin.getWorkspace().getRoot(), 0, null);

		final String message = changed.isEmpty() ? "No ProB theory mapping file was created or changed for "
				+ project.getName() + "."
				: (changed.size() == 1 ? "1 ProB theory mapping file was"
						: changed.size() + " ProB theory mapping files were")
						+ " created or extended for "
						+ project.getName()
						+ ". Operators with a default of ProB have a line with it; complete the lines marked TODO."
						+ " The animation and the disprover use the files the next time they load the model.";
		Logger.notifyUserNonModal(message, details.toString());
		final Display display = PlatformUI.isWorkbenchRunning() ? PlatformUI
				.getWorkbench().getDisplay() : null;
		if (display != null && !changed.isEmpty())
			display.asyncExec(() -> open(changed));
	}

	/** @return the template with probcli's tags, if it can list them */
	private static TheoryMappingTemplate template() {
		final GetTheoryMappingTargetsCommand command = new GetTheoryMappingTargetsCommand();
		List<Target> targets = null;
		List<Default> defaults = null;
		try {
			Animator.getAnimator().executeQuietly(command);
			targets = command.getTargets();
			defaults = command.getDefaults();
		} catch (ProBException e) {
			// an older probcli without get_theory_mapping_targets/2
			Logger.info("probcli cannot list the tags of theory mapping files: "
					+ e.getMessage());
		}
		return new TheoryMappingTemplate(targets, defaults);
	}

	private static ByteArrayInputStream bytes(final String text,
			final String charset) {
		Charset cs;
		try {
			cs = Charset.forName(charset);
		} catch (IllegalArgumentException e) {
			cs = Charset.forName("UTF-8");
		}
		return new ByteArrayInputStream(text.getBytes(cs));
	}

	private static void open(final List<IFile> files) {
		final IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		final IWorkbenchPage page = window == null ? null : window
				.getActivePage();
		if (page == null)
			return;
		for (IFile file : files) {
			try {
				IDE.openEditor(page, file, TEXT_EDITOR);
			} catch (PartInitException e) {
				Logger.info("Could not open " + file.getFullPath() + ": "
						+ e.getMessage());
			}
		}
	}
}
