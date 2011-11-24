/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.List;
import java.util.Random;

import de.prob.core.Animator;
import de.prob.core.ProblemHandler;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public class ExecuteRandomStepsCommand {

	private static final Random random = new Random();

	public static void executeOperation(final Animator animator, final int count)
			throws ProBException {
		if (count < 1) {
			final String message = "Count for Random Animation must be greater than zero";
			ProblemHandler.raiseCommandException(message);
		}
		Operation operation = null;
		for (int i = 0; i < count; i++) {
			State currentState = animator.getCurrentState();
			List<Operation> ops = currentState.getEnabledOperations();
			if (ops.isEmpty()) {
				break; // / This is a deadlock
			}

			operation = ops.get(random.nextInt(ops.size()));

			ExecuteOperationCommand.executeOperation(animator, operation, true);

			if (animator.getCurrentState().isInvariantViolated()) {
				break; // Stop in case of Invariant violation
			}
		}

		if (operation != null) {
			animator.announceCurrentStateChanged(animator.getCurrentState(),
					operation);
		}

		return;
	}

}
