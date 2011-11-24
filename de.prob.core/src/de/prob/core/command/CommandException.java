/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.command;

import de.prob.exceptions.ProBException;

public class CommandException extends ProBException {
	private static final long serialVersionUID = -533794234155971265L;

	public CommandException(final String message, final Throwable e) {
		super(message, e, true);
	}

	public CommandException(final String message) {
		super(message, true);
	}
}
