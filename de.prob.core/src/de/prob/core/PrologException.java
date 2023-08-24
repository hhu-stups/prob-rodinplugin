/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import org.eclipse.core.runtime.IStatus;

import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class PrologException extends ProBException {

	private static final long serialVersionUID = -7087955720127900792L;
	private final boolean onlyWarnings;

	public PrologException(final String message, final boolean onlyWarnings) {
		super(message, true);
		this.onlyWarnings = onlyWarnings;
	}

	@Override
	public final void notifyUserOnce() {
		if (!handled) {
			handled = true;
			if (onlyWarnings) {
				Logger.log(IStatus.WARNING, Logger.NOTIFY_USER,
						this.getMessage(), this);
			} else {
				Logger.notifyUser(this.getMessage(), this);
			}
		}
	}
}
