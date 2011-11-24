package de.prob.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import de.prob.logging.Logger;

public class OpenWebsiteCommand extends AbstractHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		String url = event.getParameter("de.prob.ui.openwebsite.url");

		URL websiteurl = null;
		try {
			websiteurl = new URL(url);
		} catch (MalformedURLException e1) {
			// File a bug report!
			String message = "Internal error. Malformed website URL (" + url
					+ "). Please file a bug report.";
			Logger.notifyUser(message, e1);
			return null;
		}

		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
				.getBrowserSupport();

		try {
			browserSupport.getExternalBrowser().openURL(websiteurl);
		} catch (PartInitException e1) {
			String message = "Internal error. Cannt open external browser.";
			Logger.notifyUser(message, e1);
			return null;
		}

		return null;
	}

}
