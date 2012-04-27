/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.ticket;

import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
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
			final boolean bugreport = false; //;code == Logger.BUGREPORT;
			display.asyncExec(new Runnable() {
				public void run() {
					// Notice: ErrorTICKETDialog to provide Bugreport-Button
					ErrorTicketDialog.openError(display.getActiveShell(),
							"Error", "An error occured.", status, bugreport);
				}
			}
			);
		}

		display.asyncExec(new Runnable() {
			public void run() {
				LogView.writeToLog(status.getMessage());
			}
		});

	}
}
