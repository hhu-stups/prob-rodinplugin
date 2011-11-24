/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.Iterator;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.command.GetFullTraceCommand.TraceResult;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public final class ReplayTraceCommand {
	private static final String ROOT = "root";

	private ReplayTraceCommand() {
	}

	public static void replay(final Animator animator) throws ProBException {
		final TraceResult trace = GetFullTraceCommand.getTrace(animator);
		animator.getHistory().reset();
		ExploreStateCommand.exploreState(animator, ROOT);
		SetStateCommand.setState(animator, ROOT);
		for (Iterator<String> it = trace.getStates().iterator(); it.hasNext();) {
			final String nextState = it.next();
			final State state = animator.getCurrentState();
			final Operation op = getOperationByDstId(nextState,
					state.getEnabledOperations());
			final boolean silent = it.hasNext();
			ExecuteOperationCommand.executeOperation(animator, op, silent);
		}
	}

	/**
	 * Find and return the first Operation in <code>operations</code> with
	 * <code>dstId</code> as destination state. If no such Operation can be
	 * found, returns <code>null</code>.
	 * 
	 * @param dstId
	 * @param operations
	 * @return
	 */
	private static Operation getOperationByDstId(final String dstId,
			final List<Operation> operations) {

		for (Operation op : operations) {
			if (op.getDestination().equals(dstId))
				return op;
		}

		return null;
	}

}
