/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.StateError;
import de.prob.core.domainobjects.Variable;
import de.prob.core.internal.Activator;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class ExploreStateCommand implements IComposableCommand {

	private final String stateId;
	private final GetEnabledOperationsCommand getOpsCmd;
	private final GetStateValuesCommand getValuesCmd;
	private final CheckBooleanPropertyCommand checkInitialisedCmd;
	private final CheckBooleanPropertyCommand checkInvCmd;
	private final CheckBooleanPropertyCommand checkMaxOpCmd;
	private final CheckBooleanPropertyCommand checkTimeoutCmd;
	private final GetStateBasedErrorsCommand getStateErrCmd;
	private final ComposedCommand allCommands;

	private State state;
	private final GetTimeoutedOperationsCommand checkTimeoutOpsCmd;

	public ExploreStateCommand(final String stateID) {
		stateId = stateID;
		getOpsCmd = new GetEnabledOperationsCommand(stateId);
		getValuesCmd = new GetStateValuesCommand(stateId);
		checkInitialisedCmd = new CheckInitialisationStatusCommand(stateId);
		checkInvCmd = new CheckInvariantStatusCommand(stateId);
		checkMaxOpCmd = new CheckMaxOperationReachedStatusCommand(stateId);
		checkTimeoutCmd = new CheckTimeoutStatusCommand(stateId);
		checkTimeoutOpsCmd = new GetTimeoutedOperationsCommand(stateId);
		getStateErrCmd = new GetStateBasedErrorsCommand(stateId);
		this.allCommands = new ComposedCommand(getOpsCmd, getValuesCmd,
				checkInitialisedCmd, checkInvCmd, checkMaxOpCmd,
				checkTimeoutCmd, checkTimeoutOpsCmd, getStateErrCmd);
	}

	public static State exploreState(final Animator a, final String stateID)
			throws ProBException {
		ExploreStateCommand command = new ExploreStateCommand(stateID);
		a.execute(command);
		return command.getState();
	}

	public String getStateID() {
		return stateId;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		allCommands.processResult(bindings);

		final boolean initialised = checkInitialisedCmd.getResult();
		final boolean invariantOk = checkInvCmd.getResult();
		final boolean timeoutOccured = checkTimeoutCmd.getResult();
		final boolean maxOperationsReached = checkMaxOpCmd.getResult();
		final List<Operation> enabledOperations = getOpsCmd
				.getEnabledOperations();
		final List<Variable> variables = getValuesCmd.getResult();
		final Collection<StateError> stateErrors = getStateErrCmd.getResult();

		if (!initialised && enabledOperations.isEmpty() && !timeoutOccured) {
			Logger.notifyUserWithoutBugreport("ProB could not find valid constants. This might be caused by the animation settings (e.g., Integer range or deferred set size) or by an inconsistency in the axioms");
		}

		Set<String> timeouts = new HashSet<String>(
				checkTimeoutOpsCmd.getTimeouts());
		state = new State(stateId, initialised, invariantOk, timeoutOccured,
				maxOperationsReached, variables, enabledOperations,
				stateErrors, timeouts);

		// Fire computed(state) event
		Activator.computedState(state);
	}

	public void writeCommand(final IPrologTermOutput pto) throws CommandException {
		allCommands.writeCommand(pto);
	}

	public State getState() {
		return state;
	}

}
