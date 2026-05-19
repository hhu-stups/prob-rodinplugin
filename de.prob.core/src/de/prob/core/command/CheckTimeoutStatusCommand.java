/**
 * (c) 2009-2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

public final class CheckTimeoutStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "timeout_occurred";

	public CheckTimeoutStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public static boolean isTimeout(final Animator a, final String stateId)
			throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a, PROPERTY_NAME,
				stateId);
	}
	// checks if time_out or virtual_time_out occurred for computing operations or for invariant
	// state_property2(timeout_occurred,StateId) :- inv_timeout_occurred(StateId) ; timeout_occurred(StateId).

}
