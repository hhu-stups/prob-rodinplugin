/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.List;
import java.util.Random;

import org.eclipse.core.runtime.IProgressMonitor;

import de.prob.core.Animator;
import de.prob.core.ProblemHandler;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public class ExecuteRandomStepsCommand {

	private static final Random random = new Random();

	public static void executeOperation(final Animator animator, final int count)
			throws ProBException {
		executeOperation(animator, count, null);
	}

	/**
	 * @param monitor
	 *            may be <code>null</code>; if it is cancelled, no further
	 *            steps are executed
	 */
	public static void executeOperation(final Animator animator,
			final int count, final IProgressMonitor monitor)
			throws ProBException {
		execute(animator, count, monitor);
	}

	/** why the random steps ended */
	public enum Stop {
		/** all steps were executed */
		DONE,
		/** no event is enabled */
		DEADLOCK,
		/** the invariant is violated in the reached state */
		INVARIANT_VIOLATED,
		/** cancelled, e.g. in the Progress view */
		CANCELLED
	}

	/** the outcome of {@link #execute} */
	public static final class Outcome {
		/** the number of executed steps */
		public final int steps;
		public final Stop stop;
		/** the last executed step, or null if none */
		public final Operation last;

		Outcome(final int steps, final Stop stop, final Operation last) {
			this.steps = steps;
			this.stop = stop;
			this.last = last;
		}
	}

	/**
	 * Executes up to count random enabled events from the current state; it
	 * stops in a deadlock or a state violating the invariant.
	 * 
	 * @param monitor
	 *            may be <code>null</code>; if it is cancelled, no further
	 *            steps are executed
	 * @return how many steps were executed and why it stopped
	 */
	public static Outcome execute(final Animator animator, final int count,
			final IProgressMonitor monitor) throws ProBException {
		if (count < 1) {
			final String message = "Count for Random Animation must be greater than zero";
			ProblemHandler.raiseCommandException(message);
		}
		Operation operation = null;
		int steps = 0;
		Stop stop = Stop.DONE;
		if (monitor != null) {
			monitor.beginTask("Executing random operations", count);
		}
		for (int i = 0; i < count; i++) {
			if (monitor != null) {
				if (monitor.isCanceled()) {
					stop = Stop.CANCELLED;
					break;
				}
				monitor.worked(1);
			}
			try {
				// e.g. the UI thread may wait for the animator
				animator.letWaitingCommandsRun();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				stop = Stop.CANCELLED;
				break;
			}
			State currentState = animator.getCurrentState();
			List<Operation> ops = currentState.getEnabledOperations();
			if (ops.isEmpty()) {
				stop = Stop.DEADLOCK;
				break; // / This is a deadlock
			}

			operation = ops.get(random.nextInt(ops.size()));

			ExecuteOperationCommand.executeOperation(animator, operation, true);
			steps++;

			if (animator.getCurrentState().isInvariantViolated()) {
				stop = Stop.INVARIANT_VIOLATED;
				break; // Stop in case of Invariant violation
			}
		}

		if (operation != null) {
			animator.announceCurrentStateChanged(animator.getCurrentState(),
					operation);
		}

		return new Outcome(steps, stop, operation);
	}

}
