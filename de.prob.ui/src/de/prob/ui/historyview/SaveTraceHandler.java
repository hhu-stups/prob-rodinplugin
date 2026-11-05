package de.prob.ui.historyview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.SaveTraceCommand;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;

/**
 * Saves the trace from the root to the current state as a ProB2 JSON trace
 * file in the folder "traces" of the animated model's project, named
 * ModelName_Nr.prob2trace with the first unused number.
 */
public class SaveTraceHandler extends AbstractHandler {

	static final String FOLDER = "traces";
	static final String EXTENSION = ".prob2trace";
	private static final String TITLE = "Save Trace";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (!animator.isMachineLoaded() || !(ldp instanceof EventBAnimatorPart)) {
			MessageDialog.openError(shell, TITLE,
					"No Event-B model is being animated.");
			return null;
		}
		final IEventBRoot root = ((EventBAnimatorPart) ldp).getRoot();
		final IFolder folder = root.getRodinProject().getProject()
				.getFolder(FOLDER);
		final IFile file;
		try {
			if (!folder.exists())
				folder.create(true, true, null);
			file = nextFreeFile(folder, root.getComponentName());
		} catch (CoreException e) {
			MessageDialog.openError(shell, TITLE, "Could not create the folder "
					+ folder.getFullPath() + ": " + e.getMessage());
			return null;
		}
		final IStatusLineManager statusLine = statusLine(HandlerUtil
				.getActivePart(event));
		final IPath modelLocation = root.getRodinFile().getResource()
				.getLocation();
		final String modelFile = modelLocation == null ? root
				.getComponentName() : modelLocation.toOSString();

		final Job job = new Job("Saving trace to " + file.getFullPath()) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				String error = null;
				try {
					SaveTraceCommand.saveCurrentTrace(animator, modelFile,
							file.getLocation().toFile());
				} catch (ProBException e) {
					error = "ProB could not save the trace to "
							+ file.getFullPath() + ":\n\n" + e.getMessage();
				}
				try {
					folder.refreshLocal(IResource.DEPTH_ONE, monitor);
				} catch (CoreException e) {
					// the file is there, only not yet shown
				}
				final String message = error;
				final Display display = Display.getDefault();
				if (!display.isDisposed())
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (message != null)
								MessageDialog.openError(
										shell.isDisposed() ? null : shell,
										TITLE, message);
							else if (statusLine != null)
								statusLine.setMessage("Saved the trace to "
										+ file.getFullPath());
						}
					});
				return Status.OK_STATUS;
			}
		};
		job.setUser(false);
		job.schedule();
		return null;
	}

	/** @return ModelName_Nr.prob2trace with the first unused number */
	private static IFile nextFreeFile(final IFolder folder, final String model) {
		for (int n = 1;; n++) {
			final IFile file = folder.getFile(model + "_" + n + EXTENSION);
			// also files not yet known to the workspace
			if (!file.exists() && !file.getLocation().toFile().exists())
				return file;
		}
	}

	static IStatusLineManager statusLine(final IWorkbenchPart part) {
		if (part == null || !(part.getSite() instanceof IViewSite))
			return null;
		return ((IViewSite) part.getSite()).getActionBars()
				.getStatusLineManager();
	}
}
