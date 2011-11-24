package de.prob.ui.stateview;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.core.Animator;
import de.prob.core.command.ExploreStateCommand;
import de.prob.core.command.GetFullTraceCommand;
import de.prob.core.command.SetStateCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public class LoadShortestTraceHandler extends AbstractHandler implements
		IHandler {

	private List<String> states;
	private final Animator animator;

	public LoadShortestTraceHandler() {
		animator = Animator.getAnimator();
	}

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		String stateId = animator.getCurrentState().getId();

		// set core state
		try {
			SetStateCommand.setState(animator, stateId);
		} catch (ProBException e) {
			// We cannot recover from this, but the user has been informed
			return null;
		}

		loadTrace();

		try {
			animator.getHistory().gotoPos(0);
		} catch (ProBException e) {
			// We cannot recover from this, but the user has been informed
			return null;
		}
		State state = animator.getCurrentState();

		try {
			for (String nextState : states) {
				final Operation op = getOperationByDstId(nextState,
						state.getEnabledOperations());

				state = ExploreStateCommand.exploreState(animator, stateId);

				// append state- and operation objects to history
				animator.getHistory().add(state, op);

				animator.announceCurrentStateChanged(state, op);
			}
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
		return null;
	}

	/**
	 * load the trace to state <code>stateId</code> from <code>animator</code>
	 * and set <code>states</code> and <code>operations</code>
	 */
	private void loadTrace() {
		try {
			states = GetFullTraceCommand.getTrace(animator).getStates();
		} catch (ProBException e) {
			e.notify();
			// We cannot recover from this, but the user has been informed
			return;
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
	private Operation getOperationByDstId(final String dstId,
			final List<Operation> operations) {

		for (Operation op : operations) {
			if (op.getDestination().equals(dstId))
				return op;
		}

		return null;
	}
}
