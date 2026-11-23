/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eventb.core.IContextRoot;
import org.eventb.core.IEventBRoot;
import org.eventb.core.IExtendsContext;
import org.eventb.core.IMachineRoot;
import org.eventb.core.IRefinesMachine;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.RodinDBException;

import de.prob.core.command.CommandException;
import de.prob.logging.Logger;

/**
 * Makes sure that the statically checked files ProB translates exist before
 * a model is loaded: the <code>.bcm</code> files of a machine and of the
 * machines it refines (they contain the seen contexts), or the
 * <code>.bcc</code> files of a context and of the contexts it extends. Rodin's
 * builder generates them; if one is missing, e.g. because a build is still
 * running, this waits for the build or builds the project incrementally. If
 * they are still missing, the user gets a notice naming them, with a link
 * that cleans and builds the project (like Project › Clean).
 */
public final class CheckedFiles {

	/** runs work that may wait for Rodin's builder, e.g. with a progress dialog */
	public interface Runner {
		/**
		 * @throws InterruptedException
		 *             if the user cancelled it
		 */
		void run(ICoreRunnable work) throws CoreException,
				InterruptedException;
	}

	private static final Runner DIRECT = new Runner() {
		@Override
		public void run(final ICoreRunnable work) throws CoreException {
			work.run(new NullProgressMonitor());
		}
	};

	private static volatile Runner runner = DIRECT;

	/** how long to wait at most for a running build */
	private static final long BUILD_WAIT_SECONDS = 120;

	private CheckedFiles() {
	}

	/**
	 * Sets how waiting for Rodin's builder is run, e.g. with a progress
	 * dialog when called in the UI thread (set by the UI plug-in).
	 */
	public static void setRunner(final Runner newRunner) {
		runner = newRunner == null ? DIRECT : newRunner;
	}

	/** a statically checked file that does not exist */
	private static final class Missing {
		final IEventBRoot component;
		final IRodinFile checked;
		/** e.g. "refined by m4", or null for the animated component */
		final String usedBy;

		Missing(final IEventBRoot component, final IRodinFile checked,
				final String usedBy) {
			this.component = component;
			this.checked = checked;
			this.usedBy = usedBy;
		}

		String describe() {
			return checked.getElementName() + " ("
					+ (component instanceof IMachineRoot ? "machine " : "context ")
					+ component.getComponentName()
					+ (usedBy == null ? "" : ", " + usedBy) + ")";
		}
	}

	/**
	 * Checks that the statically checked files of the model exist; builds the
	 * project if not.
	 *
	 * @throws CommandException
	 *             if files are still missing; the user has been notified
	 */
	public static void ensure(final IEventBRoot model) throws CommandException {
		List<Missing> missing = findMissing(model);
		if (missing.isEmpty())
			return;
		final IProject project = model.getRodinProject().getProject();
		final List<Missing> before = missing;
		final StringBuilder steps = new StringBuilder();
		if (Job.getJobManager().currentRule() == null) {
			try {
				runner.run(new ICoreRunnable() {
					@Override
					public void run(final IProgressMonitor monitor)
							throws CoreException {
						build(model, project, steps, monitor);
					}
				});
			} catch (CoreException e) {
				steps.append("Building the project failed: ")
						.append(e.getMessage()).append('\n');
				Logger.logWithoutDialog(IStatus.WARNING,
						"Building project " + project.getName()
								+ " for ProB failed", e);
			} catch (InterruptedException | OperationCanceledException e) {
				steps.append("Waiting for the build was cancelled.\n");
			}
			missing = findMissing(model);
			if (missing.isEmpty()) {
				Logger.info("ProB: the statically checked files "
						+ describe(before) + " were missing; generated after:\n"
						+ steps);
				return;
			}
		} else {
			steps.append("The project was not built, because the animation was started"
					+ " from an operation that locks the workspace.\n");
		}
		throw report(model, project, missing, steps.toString());
	}

	/** waits for a running build, else builds the project incrementally */
	private static void build(final IEventBRoot model, final IProject project,
			final StringBuilder steps, final IProgressMonitor monitor)
			throws CoreException {
		final SubMonitor sub = SubMonitor.convert(monitor,
				"Building project " + project.getName() + " for ProB", 2);
		final IJobManager jobs = Job.getJobManager();
		if (isBuilding()) {
			sub.subTask("Waiting for Rodin's build to finish");
			final IProgressMonitor deadline = withDeadline(sub.split(1),
					BUILD_WAIT_SECONDS * 1000);
			try {
				jobs.join(ResourcesPlugin.FAMILY_AUTO_BUILD, deadline);
				jobs.join(ResourcesPlugin.FAMILY_MANUAL_BUILD, deadline);
				steps.append("A build was running; ProB waited for it to finish.\n");
			} catch (OperationCanceledException e) {
				if (monitor.isCanceled())
					throw e;
				steps.append("A build was running; it did not finish within "
						+ BUILD_WAIT_SECONDS + " seconds.\n");
				return; // building now would wait for it as well
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				throw new OperationCanceledException();
			}
			if (findMissing(model).isEmpty())
				return;
		} else {
			sub.split(1);
			steps.append("No build was running.\n");
		}
		sub.subTask("Building project " + project.getName());
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD,
				sub.split(1));
		steps.append("ProB built the project incrementally"
				+ " (this does not regenerate files Rodin considers up to date).\n");
	}

	private static boolean isBuilding() {
		final IJobManager jobs = Job.getJobManager();
		return jobs.find(ResourcesPlugin.FAMILY_AUTO_BUILD).length > 0
				|| jobs.find(ResourcesPlugin.FAMILY_MANUAL_BUILD).length > 0;
	}

	private static IProgressMonitor withDeadline(
			final IProgressMonitor monitor, final long millis) {
		final long end = System.currentTimeMillis() + millis;
		return new ProgressMonitorWrapper(monitor) {
			@Override
			public boolean isCanceled() {
				return super.isCanceled() || System.currentTimeMillis() > end;
			}
		};
	}

	/** notifies the user; @return the exception for the load */
	private static CommandException report(final IEventBRoot model,
			final IProject project, final List<Missing> missing,
			final String steps) {
		final String headline = "ProB cannot load "
				+ model.getComponentName()
				+ ": Rodin has not generated "
				+ (missing.size() == 1 ? "the statically checked file "
						+ missing.get(0).checked.getElementName()
						: missing.size() + " statically checked files")
				+ " in project " + project.getName() + ".";
		final StringBuilder details = new StringBuilder();
		details.append("Missing:\n");
		for (Missing m : missing)
			details.append("  ").append(m.describe()).append('\n');
		details.append("\nBuild Automatically is ")
				.append(ResourcesPlugin.getWorkspace().isAutoBuilding() ? "on"
						: "off").append(".\n");
		details.append(steps);
		details.append("\nRodin's static checker generates these files when it builds the project."
				+ " Check the Problems view for errors of the builder, or clean and build the project"
				+ " (Project › Clean...), then start the animation again.");
		Logger.notifyUserNonModal(headline, details.toString(),
				"Clean and Build Project " + project.getName(),
				new Runnable() {
					@Override
					public void run() {
						cleanAndBuild(project, model);
					}
				});
		return new CommandException(headline + "\n" + details);
	}

	/** like Project › Clean... for one project, in the background */
	private static void cleanAndBuild(final IProject project,
			final IEventBRoot model) {
		final WorkspaceJob job = new WorkspaceJob("Cleaning and building project "
				+ project.getName()) {
			@Override
			public IStatus runInWorkspace(final IProgressMonitor monitor)
					throws CoreException {
				final SubMonitor sub = SubMonitor.convert(monitor, 2);
				project.build(IncrementalProjectBuilder.CLEAN_BUILD,
						sub.split(1));
				project.build(IncrementalProjectBuilder.FULL_BUILD,
						sub.split(1));
				final List<Missing> missing = findMissing(model);
				if (missing.isEmpty())
					Logger.notifyUserNonModal("Project " + project.getName()
							+ " has been cleaned and built. Start the animation of "
							+ model.getComponentName() + " again.", null);
				else
					Logger.notifyUserNonModal("Project " + project.getName()
							+ " has been cleaned and built, but statically checked files are still missing: "
							+ describe(missing) + ".",
							"Check the Problems and Error Log views for errors of Rodin's builder.");
				return Status.OK_STATUS;
			}
		};
		job.setRule(ResourcesPlugin.getWorkspace().getRuleFactory()
				.buildRule());
		job.setUser(true);
		job.schedule();
	}

	private static String describe(final List<Missing> missing) {
		final StringBuilder result = new StringBuilder();
		for (Missing m : missing) {
			if (result.length() > 0)
				result.append(", ");
			result.append(m.describe());
		}
		return result.toString();
	}

	/**
	 * @return the statically checked files ProB needs for the model that do
	 *         not exist
	 */
	private static List<Missing> findMissing(final IEventBRoot model) {
		final List<Missing> missing = new ArrayList<Missing>();
		try {
			visit(model, null, new HashSet<String>(), missing);
		} catch (RodinDBException e) {
			// the translation will report it
			Logger.info("ProB could not check the statically checked files of "
					+ model.getComponentName() + ": " + e.getMessage());
		}
		return missing;
	}

	private static void visit(final IEventBRoot root, final String usedBy,
			final Set<String> visited, final List<Missing> missing)
			throws RodinDBException {
		final IRodinFile file = root.getRodinFile();
		// a missing abstraction is an error Rodin's static checker reports
		if (!file.exists() || !visited.add(file.getPath().toString()))
			return;
		if (root instanceof IMachineRoot) {
			final IMachineRoot machine = (IMachineRoot) root;
			final IRodinFile checked = machine.getSCMachineRoot()
					.getRodinFile();
			if (!checked.exists())
				missing.add(new Missing(root, checked, usedBy));
			// the seen contexts are contained in the .bcm files
			for (IRefinesMachine refines : machine.getRefinesClauses()) {
				final IMachineRoot abstraction;
				try {
					abstraction = refines.getAbstractMachineRoot();
				} catch (RodinDBException e) {
					continue; // e.g. no machine named
				}
				visit(abstraction, "refined by " + root.getComponentName(),
						visited, missing);
			}
		} else if (root instanceof IContextRoot) {
			final IContextRoot context = (IContextRoot) root;
			final IRodinFile checked = context.getSCContextRoot()
					.getRodinFile();
			if (!checked.exists())
				missing.add(new Missing(root, checked, usedBy));
			for (IExtendsContext extendsContext : context.getExtendsClauses()) {
				final IContextRoot abstraction;
				try {
					abstraction = extendsContext.getAbstractContextRoot();
				} catch (RodinDBException e) {
					continue;
				}
				visit(abstraction, "extended by " + root.getComponentName(),
						visited, missing);
			}
		}
	}
}
