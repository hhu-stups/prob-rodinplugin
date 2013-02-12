/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.exceptions;

import de.prob.logging.Logger;

public abstract class ProBException extends Exception {

	private static final long serialVersionUID = -1797280394427366051L;

	protected boolean handled;

	public boolean isHandled() {
		return handled;
	}

	public ProBException(final String exception) {
		this(exception, null, false);
	}

	public ProBException(final Throwable e) {
		this(e.getLocalizedMessage(), e, false);
	}

	public ProBException(final String message, final Throwable e,
			final boolean b) {
		super(message, e);
		this.handled = b;
	}

	public ProBException(final String message, final boolean b) {
		super(message);
		this.handled = b;
	}

	public final void notifyUserOnce() {
		if (!handled) {
			handled = true;
			Logger.notifyUser(this.getMessage(), this);
		}
	}

}
