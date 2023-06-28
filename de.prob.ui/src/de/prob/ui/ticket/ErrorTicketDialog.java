package de.prob.ui.ticket;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class ErrorTicketDialog extends ErrorDialog {
	public ErrorTicketDialog(final Shell parentShell, final String dialogTitle,
			final String message, final IStatus status, final int displayMask) {
		super(parentShell, dialogTitle, message, status, displayMask);
	}

	public static int openError(final Shell parentShell, final String title,
			final String message, final IStatus status) {
		ErrorTicketDialog dialog = new ErrorTicketDialog(parentShell, title,
				message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING
						| IStatus.ERROR);
		return dialog.open();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.IGNORE_LABEL, true);
		// createDetailsButton(parent);
	}
}
