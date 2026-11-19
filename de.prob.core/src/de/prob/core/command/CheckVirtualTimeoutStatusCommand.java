/**
 * (c) 2009-2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

public final class CheckVirtualTimeoutStatusCommand extends
		CheckBooleanPropertyCommand {

	private static final String PROPERTY_NAME = "virtual_timeout_occurred";

	public CheckVirtualTimeoutStatusCommand(final String stateId) {
		super(PROPERTY_NAME, stateId);
	}

	public static boolean isVirtualTimeout(final Animator a, final String stateId)
			throws ProBException {
		return CheckBooleanPropertyCommand.isPropertyTrue(a, PROPERTY_NAME,
				stateId);
	}
	// checks if virtual_time_out occurred for computing operations 
	// state_property2(virtual_timeout_occurred,StateId) :- virtual_time_out_for_node(StateId).

}
