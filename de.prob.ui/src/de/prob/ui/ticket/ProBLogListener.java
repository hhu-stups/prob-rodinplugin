/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ticket;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;

import de.prob.logging.Logger;

public final class ProBLogListener implements ILogListener {

	private static final Display display = Display.getDefault();

	public ProBLogListener() {
	}

	public void logging(final IStatus status, final String plugin) {
		if (display == null || display.isDisposed()) {
			return;
		}

		final int code = status.getCode();

		if (code == Logger.BUGREPORT || code == Logger.NOBUGREPORT) {
			display.asyncExec(new Runnable() {
				public void run() {
					String title = (status.getSeverity() == IStatus.ERROR) ? "An Error occured"
							: "Warning";
					ErrorDialog.openError(display.getActiveShell(),
							"ProB Problem", title, status);
				}
			});
		}

		display.asyncExec(new Runnable() {
			public void run() {
				LogView.writeToLog(status.getMessage());
			}
		});

	}
}
