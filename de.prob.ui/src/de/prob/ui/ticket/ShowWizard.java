package de.prob.ui.ticket;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowWizard extends AbstractHandler{

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		
		BugReportWizard wizard = new BugReportWizard();
		WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(arg0), wizard);
		dialog.open();
		return null;
	}

}
