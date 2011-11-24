/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.cli;

import de.prob.exceptions.ProBException;

public class CliException extends ProBException {

	private static final long serialVersionUID = -1376277300420758117L;

	public CliException(final Throwable e) {
		super(e);
	}

	public CliException(final String message, final Throwable e, final boolean b) {
		super(message, e, b);
	}

	public CliException(final String message, final boolean b) {
		super(message, b);
	}

	public CliException(final String exception) {
		super(exception);
	}
}
