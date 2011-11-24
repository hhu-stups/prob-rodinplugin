/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

public final class CheckMaxOperationReachedStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "max_operations_reached";

	public CheckMaxOperationReachedStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public static boolean maxOperationReached(final Animator a,
			final String stateId) throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a, PROPERTY_NAME,
				stateId);
	}

}
