package de.prob.ui.explorer;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.ui.historyview.SaveTraceHandler;

/**
 * Renames the trace selected in the Event-B Explorer. The extension
 * .prob2trace is kept (added if left out), so that the trace stays in the
 * Explorer; the trace stays with its model, as the model's name is stored in
 * the file.
 */
public class RenameTraceHandler extends AbstractHandler {

	private static final String TITLE = "Rename Trace";
	private static final String EXT = SaveTraceHandler.EXTENSION;

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final TraceNode trace = ReplayExplorerTraceHandler.selectedTrace(event);
		if (trace == null)
			return null;
		final IFile file = trace.getFile();
		final Shell shell = HandlerUtil.getActiveShell(event);
		final String oldName = withoutExtension(file.getName());
		final InputDialog dialog = new InputDialog(shell, TITLE,
				"New name of the trace (" + EXT + " is added):", oldName,
				new IInputValidator() {
					@Override
					public String isValid(final String text) {
						return validate(file, text);
					}
				});
		if (dialog.open() != Window.OK
				|| dialog.getValue().trim().equals(oldName))
			return null;
		try {
			final String newName = fileName(dialog.getValue());
			final Map<QualifiedName, String> status = TraceStatus.saved(file);
			file.move(file.getParent().getFullPath().append(newName), true,
					null);
			// in case the replay status did not move with the file
			TraceStatus.restoreIfMissing(file.getParent().getFile(
					Path.fromPortableString(newName)), status);
		} catch (CoreException e) {
			MessageDialog.openError(shell, TITLE, "Could not rename "
					+ file.getFullPath() + ": " + e.getMessage());
		}
		return null;
	}

	private static String validate(final IFile file, final String text) {
		final String name = text.trim();
		if (name.isEmpty())
			return "Enter a name.";
		if (name.equals(withoutExtension(file.getName())))
			return null; // unchanged
		final String fileName = fileName(name);
		final IStatus status = ResourcesPlugin.getWorkspace().validateName(
				fileName, IResource.FILE);
		if (!status.isOK())
			return status.getMessage();
		if (file.getParent().getFile(Path.fromPortableString(fileName)).exists())
			return "A trace named " + fileName + " already exists.";
		return null;
	}

	private static String fileName(final String name) {
		final String trimmed = name.trim();
		return trimmed.endsWith(EXT) ? trimmed : trimmed + EXT;
	}

	private static String withoutExtension(final String fileName) {
		return fileName.endsWith(EXT) ? fileName.substring(0,
				fileName.length() - EXT.length()) : fileName;
	}
}
