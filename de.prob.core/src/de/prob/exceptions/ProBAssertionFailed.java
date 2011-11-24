/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.exceptions;

public class ProBAssertionFailed extends RuntimeException {

	private static final long serialVersionUID = -7604689708047639171L;

	public ProBAssertionFailed(final String assertion) {
		super(assertion);
	}

}
