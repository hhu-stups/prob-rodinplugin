package de.prob.ui.ticket;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class SubmitBugreportCommand extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		BugReportWizard wizard = new BugReportWizard();
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		dialog.open();
		return null;
	}

}
