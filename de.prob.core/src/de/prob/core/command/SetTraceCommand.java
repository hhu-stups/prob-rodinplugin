/**
 * 
 */
package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * @author plagge
 * 
 */
public class SetTraceCommand implements IComposableCommand {

	private final Collection<Operation> operations;
	private final ExploreStateCommand[] exploreStateCmds;
	private final ComposedCommand compExplore;

	private boolean hasBeenProcessed = false;

	public SetTraceCommand(final Collection<Operation> operations) {
		super();
		this.operations = new ArrayList<Operation>(operations);
		this.exploreStateCmds = toExplore(operations);
		compExplore = new ComposedCommand(exploreStateCmds);
	}

	private static ExploreStateCommand[] toExplore(
			Collection<Operation> operations) {
		final int size = operations.size();
		ExploreStateCommand[] states = new ExploreStateCommand[size + 1];
		if (operations.isEmpty()) {
			states[0] = new ExploreStateCommand("root");
		} else {
			final String initial = operations.iterator().next().getSource();
			states[0] = new ExploreStateCommand(initial);
			int i = 1;
			for (final Operation op : operations) {
				final String dest = op.getDestination();
				states[i] = new ExploreStateCommand(dest);
				i++;
			}
		}
		return states;
	}

	@Override
	public void writeCommand(IPrologTermOutput pto) throws CommandException {
		compExplore.writeCommand(pto);
	}

	@Override
	public void processResult(ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		compExplore.processResult(bindings);
		hasBeenProcessed = true;
	}

	public void setTraceInHistory(final Animator animator,
			final Integer currentPosition) {
		if (!hasBeenProcessed) {
			throw new IllegalStateException(
					"command must be sent to ProB before calling setTraceInHistory.");
		}
		final History history = animator.getHistory();
		history.reset();
		Operation curOp = null;
		State curState = null;
		// let's start in the root state
		final State rootState = exploreStateCmds[0].getState();
		history.add(rootState, null);
		if (currentPosition != null && currentPosition == 0) {
			curState = rootState;
		}
		int pos = 1;
		for (final Operation operation : operations) {
			final State state = exploreStateCmds[pos].getState();
			history.add(state, operation);
			if (currentPosition != null && pos == currentPosition) {
				curOp = operation;
				curState = state;
			}
			pos++;
		}
		if (curState != null) {
			animator.announceCurrentStateChanged(curState, curOp);
		}
	}
}
