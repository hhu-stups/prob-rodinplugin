/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.translator;

import de.prob.exceptions.ProBException;

public class TranslationFailedException extends ProBException {

	private static final long serialVersionUID = 591728225914801177L;

	public TranslationFailedException(final Exception e) {
		super(e);
	}

	public TranslationFailedException(final String component,
			final String details) {
		this(component, details, null);
	}

	public TranslationFailedException(final String component,
			final String details, Throwable e) {
		super("Translation of " + component + " failed.\nTry cleaning your workspace.\nDetails: \n" + details, e, false);
		notifyUserOnce();
	}
}
