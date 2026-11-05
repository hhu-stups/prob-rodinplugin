package de.prob.ui.explorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Deletes the trace selected in the Event-B Explorer, after asking. The
 * file's history is kept in the workspace's local history.
 */
public class DeleteTraceHandler extends AbstractHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = ReplayExplorerTraceHandler.selectedTrace(event);
		if (trace == null)
			return null;
		final IFile file = trace.getFile();
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (!MessageDialog.openConfirm(shell, "Delete Trace",
				"Delete the trace " + file.getName() + "?"))
			return null;
		try {
			file.delete(true, true, null);
		} catch (CoreException e) {
			MessageDialog.openError(shell, "Delete Trace", "Could not delete "
					+ file.getFullPath() + ": " + e.getMessage());
		}
		return null;
	}
}
