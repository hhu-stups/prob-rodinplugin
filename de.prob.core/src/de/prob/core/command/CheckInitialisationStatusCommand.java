/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

public final class CheckInitialisationStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String IS_INITIALISED_STATE = "isInitialisedState";

	public static boolean isInitialized(final Animator a, final String stateId)
			throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a,
				IS_INITIALISED_STATE, stateId);
	}

	public CheckInitialisationStatusCommand(final String stateId) {
		super("initialised", stateId);
	}

}
