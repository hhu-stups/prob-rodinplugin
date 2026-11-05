package de.prob.ui.explorer;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * Opens the trace selected in the Event-B Explorer in Eclipse's text editor
 * (the JSON file). A double-click replays the trace instead. Editing the
 * trace resets its replay status (see {@link TraceStatus}).
 */
public class EditTraceHandler extends AbstractHandler {

	/** always present; other editors may hand .prob2trace to another program */
	private static final String TEXT_EDITOR = "org.eclipse.ui.DefaultTextEditor";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = ReplayExplorerTraceHandler.selectedTrace(event);
		if (trace == null)
			return null;
		final IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindowChecked(
				event).getActivePage();
		try {
			IDE.openEditor(page, trace.getFile(), TEXT_EDITOR);
		} catch (PartInitException e) {
			MessageDialog.openError(HandlerUtil.getActiveShell(event),
					"Edit Trace", "Could not open " + trace.getFile().getName()
							+ ": " + e.getMessage());
		}
		return null;
	}
}
