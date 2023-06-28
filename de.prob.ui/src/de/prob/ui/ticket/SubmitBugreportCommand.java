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
		try {
			IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
			browser.openURL(new URL("https://github.com/hhu-stups/prob-issues/issues/new/choose"));
		} catch (MalformedURLException | PartInitException e) {
			throw new ExecutionException(e.getMessage(), e);
		}
		return null;
	}

}
