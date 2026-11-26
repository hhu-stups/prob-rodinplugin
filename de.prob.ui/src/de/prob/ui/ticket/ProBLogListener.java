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

	@Override
	public void logging(final IStatus status, final String plugin) {
		if (display == null || display.isDisposed()) {
			return;
		}

		if (status.getCode() == Logger.NO_DIALOG)
			return; // only for the Error Log
		if (status.getSeverity() == IStatus.ERROR || status.getSeverity() == IStatus.WARNING) {
			// a resizable dialog with a list of the problems and a
			// scrollable text; further problems are added to an open one
			display.asyncExec(() -> ProBProblemDialog.show(
					display.getActiveShell(), status));
		}
	}
}
