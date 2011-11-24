/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.cli.CliException;
import de.prob.core.Animator;
import de.prob.core.LimitedLogger;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class ExecuteOperationCommand implements IComposableCommand {

	private final Operation operation;
	private final boolean fireCurrentStateChanged;
	private final ExploreStateCommand exloreStateCmd;
	private final SetStateCommand setStateCmd;
	private final IComposableCommand cmds;

	private ExecuteOperationCommand(final Operation operation) {
		this(operation, false);
	}

	private ExecuteOperationCommand(final Operation operation,
			final boolean silent) {
		this.operation = operation;
		this.fireCurrentStateChanged = !silent;
		final String stateId = operation.getDestination();
		this.exloreStateCmd = new ExploreStateCommand(stateId);
		this.setStateCmd = new SetStateCommand(stateId);
		this.cmds = new ComposedCommand(exloreStateCmd, setStateCmd);
	}

	public static void executeOperation(final Animator a, final Operation op)
			throws ProBException {
		ExecuteOperationCommand executeOperationCommand = new ExecuteOperationCommand(
				op);
		a.execute(executeOperationCommand);
	}

	/**
	 * If <i>silent</i> is set to true the <i>currentStateChanged</i> event is
	 * not fired on the Activator.
	 * 
	 * @throws CliException
	 */
	public static void executeOperation(final Animator a, final Operation op,
			final boolean silent) throws ProBException {
		ExecuteOperationCommand executeOperationCommand = new ExecuteOperationCommand(
				op, silent);
		a.execute(executeOperationCommand);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		cmds.processResult(bindings);
		final Animator animator = Animator.getAnimator();
		final State state = exloreStateCmd.getState();

		// Change history in Animator
		animator.getHistory().add(state, operation);

		if (fireCurrentStateChanged) {
			animator.announceCurrentStateChanged(state, operation);
		}
	}

	public void writeCommand(final IPrologTermOutput pto) throws CommandException {
		LimitedLogger.getLogger().log("execute operation", operation.getName(),
				null);
		cmds.writeCommand(pto);
	}
}
