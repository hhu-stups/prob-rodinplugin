package de.prob.ui.explorer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eventb.core.IEventBRoot;

import de.prob.core.Animator;
import de.prob.core.command.ReplayJsonTraceCommand;
import de.prob.exceptions.ProBException;
import de.prob.ui.historyview.ReplayTraceHandler;

/**
 * Replays all traces of the "Traces" node selected in the Event-B Explorer,
 * one after the other, and stores their status (see {@link TraceStatus}).
 * The animation of the model is started first if necessary. The history
 * shows the last trace afterwards, and a summary lists the outcome.
 */
public class ReplayAllTracesHandler extends AbstractHandler {

	private static final String TITLE = "Replay All Traces";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		if (selection == null)
			selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)
				|| !(((IStructuredSelection) selection).getFirstElement() instanceof TracesNode))
			return null;
		final IEventBRoot root = ((TracesNode) ((IStructuredSelection) selection)
				.getFirstElement()).getRoot();
		final List<IFile> traces = TraceFiles.of(root);
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (traces.isEmpty()
				|| !ReplayExplorerTraceHandler.ensureAnimated(root))
			return null;

		final Animator animator = Animator.getAnimator();
		final Job job = new Job("Replaying the traces of "
				+ root.getComponentName()) {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask(getName(), traces.size());
				final List<String> perfect = new ArrayList<String>();
				final List<String> imperfect = new ArrayList<String>();
				final List<String> failed = new ArrayList<String>();
				int done = 0;
				for (IFile trace : traces) {
					if (monitor.isCanceled())
						break;
					monitor.subTask(TraceFiles.displayName(trace));
					ReplayJsonTraceCommand.Result result = null;
					try {
						result = ReplayJsonTraceCommand.replay(animator, trace
								.getLocation().toFile());
					} catch (ProBException e) {
						// stored as failed
					}
					final TraceStatus.Status status = ReplayTraceHandler
							.storeStatus(trace.getLocation().toFile(), root,
									result);
					final String line = result == null ? TraceFiles.displayName(trace)
							+ " (ProB failed)" : TraceFiles.displayName(trace) + " ("
							+ result.getReplayedSteps() + " of "
							+ result.getSteps() + " steps)";
					if (status == TraceStatus.Status.PERFECT)
						perfect.add(line);
					else if (status == TraceStatus.Status.IMPERFECT)
						imperfect.add(line);
					else
						failed.add(line);
					done++;
					monitor.worked(1);
				}
				monitor.done();
				showSummary(shell, root, traces.size(), done, perfect,
						imperfect, failed);
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
		return null;
	}

	private static void showSummary(final Shell shell, final IEventBRoot root,
			final int all, final int done, final List<String> perfect,
			final List<String> imperfect, final List<String> failed) {
		final StringBuilder text = new StringBuilder();
		text.append("Replayed ").append(done).append(" of ").append(all)
				.append(" traces of ").append(root.getComponentName())
				.append(done < all ? " (cancelled)." : ".");
		append(text, "Perfect", perfect);
		append(text, "Imperfect", imperfect);
		append(text, "Failed", failed);
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				final Shell parent = shell == null || shell.isDisposed() ? null
						: shell;
				if (!failed.isEmpty())
					MessageDialog.openError(parent, TITLE, text.toString());
				else if (!imperfect.isEmpty())
					MessageDialog.openWarning(parent, TITLE, text.toString());
				else
					MessageDialog.openInformation(parent, TITLE,
							text.toString());
			}
		});
	}

	private static void append(final StringBuilder text, final String title,
			final List<String> traces) {
		if (traces.isEmpty())
			return;
		text.append("\n\n").append(title).append(" (").append(traces.size())
				.append("):");
		for (String trace : traces)
			text.append("\n  ").append(trace);
	}
}
