/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.handler;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.prob.logging.Logger;

public class OpenWebsiteHandler extends AbstractHandler {

	private static final String URL = "http://www.stups.uni-duesseldorf.de/BMotionStudio/";

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser()
					.openURL(new URL(URL));
		} catch (PartInitException e) {
			final String message = "Part init exception occurred\n"
					+ e.getLocalizedMessage();
			Logger.notifyUser(message, e);
		} catch (MalformedURLException e) {
			final String message = "This really should never happen unless the http protocol changes";
			Logger.notifyUser(message, e);
		}
		return null;
	}

}
