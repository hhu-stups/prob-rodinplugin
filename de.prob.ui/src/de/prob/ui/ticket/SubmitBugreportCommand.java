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
		IWebBrowser browser;
		try {
			browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("jira");
			browser.openURL(new URL("https://probjira.atlassian.net"));
			// TODO: should we switch to https://github.com/hhu-stups/prob-issues/issues/new
		} catch (MalformedURLException | PartInitException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
