package de.prob.eventb.disprover.core;

import de.prob.exceptions.ProBException;

public class DisproverException extends ProBException {

	private static final long serialVersionUID = -454323116496165151L;

	public DisproverException(final String message, final Throwable e) {
		super(message, e, true);
	}

	public DisproverException(final String message) {
		super(message, true);
	}
}
