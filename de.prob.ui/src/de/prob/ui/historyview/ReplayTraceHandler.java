package de.prob.ui.historyview;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.ReplayJsonTraceCommand;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.exceptions.ProBException;

/**
 * Replays a ProB2 JSON trace file (.prob2trace), e.g. one saved by
 * {@link SaveTraceHandler}, and shows the replayed trace in the history.
 */
public class ReplayTraceHandler extends AbstractHandler {

	private static final String TITLE = "Replay Trace";

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

		final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setText(TITLE);
		dialog.setFilterExtensions(new String[] {
				"*" + SaveTraceHandler.EXTENSION, "*.json", "*.*" });
		dialog.setFilterNames(new String[] { "ProB2 Trace Files",
				"JSON Files", "All Files" });
		// the traces saved by the Save Trace button
		final IFolder folder = root.getRodinProject().getProject()
				.getFolder(SaveTraceHandler.FOLDER);
		if (folder.exists() && folder.getLocation() != null)
			dialog.setFilterPath(folder.getLocation().toOSString());
		final String path = dialog.open();
		if (path == null)
			return null;
		replay(shell, SaveTraceHandler.statusLine(HandlerUtil
				.getActivePart(event)), new File(path));
		return null;
	}

	/**
	 * Replays a trace file in the background, e.g. from the Event-B
	 * Explorer, and reports the outcome: in the status line if the replay was
	 * perfect, else in a dialog.
	 * 
	 * @param statusLine
	 *            may be <code>null</code>
	 */
	public static void replay(final Shell shell,
			final IStatusLineManager statusLine, final File file) {
		final Animator animator = Animator.getAnimator();
		final Job job = new Job("Replaying trace " + file.getName()) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				ReplayJsonTraceCommand.Result result = null;
				String error = null;
				try {
					result = ReplayJsonTraceCommand.replay(animator, file);
				} catch (ProBException e) {
					error = "ProB could not replay " + file.getName()
							+ ":\n\n" + e.getMessage();
				}
				final ReplayJsonTraceCommand.Result replayed = result;
				final String message = error;
				final Display display = Display.getDefault();
				if (!display.isDisposed())
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							showResult(shell == null || shell.isDisposed() ? null
									: shell, statusLine, file, replayed,
									message);
						}
					});
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	private static void showResult(final Shell shell,
			final IStatusLineManager statusLine, final File file,
			final ReplayJsonTraceCommand.Result result, final String error) {
		if (error != null) {
			MessageDialog.openError(shell, TITLE, error);
			return;
		}
		final String summary = "Replayed " + result.getReplayedSteps()
				+ " of " + result.getSteps() + " steps of " + file.getName()
				+ " (" + result.getStatus() + " replay).";
		if (result.isPerfect()
				&& result.getReplayedSteps() == result.getSteps()) {
			if (statusLine != null)
				statusLine.setMessage(summary);
			return;
		}
		final StringBuilder text = new StringBuilder(summary);
		if (!result.getMessages().isEmpty()) {
			text.append("\n\nProB reported:\n");
			for (String message : result.getMessages())
				text.append('\n').append(message);
		}
		if (result.getReplayedSteps() < result.getSteps())
			MessageDialog.openError(shell, TITLE, text.toString());
		else
			MessageDialog.openWarning(shell, TITLE, text.toString());
	}
}
