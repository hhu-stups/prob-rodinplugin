/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core;

import de.prob.exceptions.ProBException;

public class PrologException extends ProBException {

	private static final long serialVersionUID = -7087955720127900792L;

	public PrologException(final String message) {
		super(message, true);
	}
}
