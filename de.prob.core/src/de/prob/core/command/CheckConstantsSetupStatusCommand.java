/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

// returns true if constants are set-up

public final class CheckConstantsSetupStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String IS_CONSTANTS_SETUP_STATE = "constants_set_up";

	public static boolean isConstantsSetup(final Animator a, final String stateId)
			throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a,
				IS_CONSTANTS_SETUP_STATE, stateId);
	}

	public CheckConstantsSetupStatusCommand(final String stateId) {
		super(IS_CONSTANTS_SETUP_STATE, stateId);
	}

}
