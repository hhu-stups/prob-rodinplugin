package de.prob.ui.ticket;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

public class ErrorTicketDialog extends ErrorDialog {

	private static final int OPEN_BUGREPORT_ID = IDialogConstants.CLIENT_ID + 1;
	private static final String OPEN_BUGREPORT_LABEL = "Submit Bugreport";

	private final IStatus status;
	private final boolean bugreport;

	public ErrorTicketDialog(final Shell parentShell, final String dialogTitle,
			final String message, final IStatus status, final int displayMask,
			final boolean bugreport) {
		super(parentShell, dialogTitle, message, status, displayMask);
		this.status = status;
		this.bugreport = bugreport;
	}

	public static int openError(final Shell parentShell, final String title,
			final String message, final IStatus status, final boolean bugreport) {
		ErrorTicketDialog dialog = new ErrorTicketDialog(parentShell, title,
				message, status, IStatus.OK | IStatus.INFO | IStatus.WARNING
						| IStatus.ERROR, bugreport);
		return dialog.open();
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		// create Button to open the BugReport-Wizard
		if (bugreport)
			createButton(parent, OPEN_BUGREPORT_ID, OPEN_BUGREPORT_LABEL, false);
		// create OK and Details buttons
		createButton(parent, IDialogConstants.OK_ID,
				IDialogConstants.IGNORE_LABEL, true);
		// createDetailsButton(parent);
	}

	@Override
	protected void buttonPressed(final int id) {
		if (id == IDialogConstants.DETAILS_ID) {
			// was the details button pressed?
			// toggleDetailsArea();
		} else if (id == OPEN_BUGREPORT_ID) {
			openBugReportWizard();
			super.buttonPressed(IDialogConstants.OK_ID);
		} else {
			super.buttonPressed(id);
		}
	}

	private void openBugReportWizard() {

		StringBuffer sb = new StringBuffer();
		sb.append(status.getMessage());

		printOperatingSystemInfo(sb);
		printStackTrace(sb, status.getException());

		BugReportWizard wizard = new BugReportWizard("ProB internal error",
				true, sb.toString());
		WizardDialog dialog = new WizardDialog(
				HandlerUtil.getActiveShell(new ExecutionEvent()), wizard);
		dialog.setPageSize(400, 300);
		dialog.open();
	}

	private void printStackTrace(final StringBuffer sb,
			final Throwable exception) {
		if (exception != null) {
			sb.append("\n\n");
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			sb.append(sw.toString());
		}
	}

	private void printOperatingSystemInfo(final StringBuffer sb) {
		sb.append("\n\n");
		sb.append("Java Version: ");
		sb.append(System.getProperty("java.version"));
		sb.append("\nOS: ");
		sb.append(System.getProperty("os.name"));
		sb.append("\nArchitecture: ");
		sb.append(System.getProperty("os.arch"));
		sb.append("\nOS-Version: ");
		sb.append(System.getProperty("os.version"));
	}

}
