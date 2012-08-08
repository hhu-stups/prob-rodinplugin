package de.prob.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

public class OpenGroovyConsole extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
					.getBrowserSupport();
			browserSupport.createBrowser(IWorkbenchBrowserSupport.AS_EDITOR,
					"prob", "prob2", "prob3").openURL(
					new URL("http://localhost:8080"));
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
