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

	private final Animator animator;

	public LoadShortestTraceHandler() {
		animator = Animator.getAnimator();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		loadShortestTraceTo(animator, animator.getCurrentState().getId());
		return null;
	}

	/**
	 * Makes the given state of the state space the current state, with the
	 * shortest trace from the root as history (e.g. after a double-click in
	 * the ProB Graph view)
	 */
	public static void loadShortestTraceTo(final Animator animator,
			final String stateId) {
		// set core state
		try {
			SetStateCommand.setState(animator, stateId);
		} catch (ProBException e) {
			// We cannot recover from this, but the user has been informed
			return;
		}

		final List<String> states = loadTrace(animator);
		if (states == null) {
			// We cannot recover from this, but the user has been informed
			return;
		}

		try {
			animator.getHistory().gotoPos(0);
		} catch (ProBException e) {
			// We cannot recover from this, but the user has been informed
			return;
		}
		State state = animator.getCurrentState();

		try {
			for (String nextState : states) {
				final Operation op = getOperationByDstId(nextState,
						state.getEnabledOperations());

				state = ExploreStateCommand.exploreState(animator, nextState);

				// append state- and operation objects to history
				animator.getHistory().add(state, op);

				animator.announceCurrentStateChanged(state, op);
			}
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
	}

	/**
	 * load the trace to the current state from <code>animator</code>
	 * 
	 * @return the state ids of the trace or <code>null</code> if loading
	 *         failed (the user has been informed)
	 */
	private static List<String> loadTrace(final Animator animator) {
		try {
			return GetFullTraceCommand.getTrace(animator).getStates();
		} catch (ProBException e) {
			e.notifyUserOnce();
			return null;
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
