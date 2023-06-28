package de.prob.ui.ticket;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

public class SubmitBugreportCommand extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

//		IWorkbenchWindow window = HandlerUtil
//				.getActiveWorkbenchWindowChecked(event);
//
//		BugReportWizard wizard = new BugReportWizard();
//		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
//		dialog.open();
//		return null;
		IWebBrowser browser;
		try {
			browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("jira");
			browser.openURL(new URL("https://probjira.atlassian.net"));
			// TODO: should we switch to https://github.com/hhu-stups/prob-issues/issues/new
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
