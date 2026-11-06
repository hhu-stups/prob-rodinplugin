/**
 * (c) 2009-2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Replays a ProB2 JSON trace file (.prob2trace), using
 * <code>prob2_replay_json_trace_file_with_messages/5</code>, and makes the
 * replayed trace the animator's history.
 */
public final class ReplayJsonTraceCommand implements IComposableCommand {

	private static final String STATUS = "Status";
	private static final String TRANSITIONS = "TransIds";
	private static final String STEPS = "Steps";
	private static final String MESSAGES = "Messages";

	/** the outcome of a replay */
	public static final class Result {
		private final String status;
		private final int replayedSteps;
		private final int steps;
		private final List<String> messages;

		Result(final String status, final int replayedSteps, final int steps,
				final List<String> messages) {
			this.status = status;
			this.replayedSteps = replayedSteps;
			this.steps = steps;
			this.messages = messages;
		}

		/** @return perfect, imperfect or partial */
		public String getStatus() {
			return status;
		}

		public boolean isPerfect() {
			return "perfect".equals(status);
		}

		/** @return the number of steps in the history */
		public int getReplayedSteps() {
			return replayedSteps;
		}

		/** @return the number of steps in the trace file */
		public int getSteps() {
			return steps;
		}

		/** @return ProB's error and warning messages of the replay */
		public List<String> getMessages() {
			return messages;
		}
	}

	private final File file;
	private String status;
	private final List<Long> transitionIds = new ArrayList<Long>();
	private int steps;
	private List<String> messages = Collections.emptyList();

	public ReplayJsonTraceCommand(final File file) {
		this.file = file;
	}

	/**
	 * Replays the trace file and makes the replayed steps the history. Does
	 * not notify the user if ProB fails: the exception's message has ProB's
	 * error messages, see {@link Animator#callQuietly}.
	 */
	public static Result replay(final Animator animator, final File file)
			throws ProBException {
		return Animator.callQuietly(new Animator.ProBCall<Result>() {
			@Override
			public Result call() throws ProBException {
				final ReplayJsonTraceCommand command = new ReplayJsonTraceCommand(
						file);
				animator.execute(command);
				final int replayed = command.setHistory(animator);
				return new Result(command.status, replayed, command.steps,
						command.messages);
			}
		});
	}

	/**
	 * executes the replayed transitions from the root, as the history view
	 * shows the plug-in's history
	 *
	 * @return the number of executed transitions
	 */
	private int setHistory(final Animator animator) throws ProBException {
		animator.getHistory().reset();
		ExploreStateCommand.exploreState(animator, "root");
		SetStateCommand.setState(animator, "root");
		int replayed = 0;
		for (Iterator<Long> it = transitionIds.iterator(); it.hasNext();) {
			final long id = it.next();
			final State state = animator.getCurrentState();
			final Operation op = state == null ? null : findOperation(id,
					state.getEnabledOperations());
			if (op == null) {
				final List<String> all = new ArrayList<String>(messages);
				all.add("Transition " + id + " of step " + (replayed + 1)
						+ (state == null ? " cannot be replayed: there is no current state."
								: " is not enabled in state " + state.getId() + "."));
				messages = all;
				break;
			}
			// only the last step announces the new state to the views
			ExecuteOperationCommand.executeOperation(animator, op,
					it.hasNext());
			replayed++;
		}
		if (replayed == 0 && animator.getCurrentState() != null)
			animator.announceCurrentStateChanged(animator.getCurrentState(),
					Operation.NULL_OPERATION);
		return replayed;
	}

	private static Operation findOperation(final long id,
			final List<Operation> operations) {
		for (Operation op : operations)
			if (op.getId() == id)
				return op;
		return null;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_replay_json_trace_file_with_messages");
		pto.printAtom(file.getAbsolutePath());
		pto.printVariable(STATUS);
		pto.printVariable(TRANSITIONS);
		pto.printVariable(STEPS);
		pto.printVariable(MESSAGES);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		status = GetCommandsCommand.text(bindings.get(STATUS));
		for (PrologTerm id : BindingGenerator.getList(bindings, TRANSITIONS))
			// skipped steps are not transitions
			if (id instanceof AIntegerPrologTerm
					&& ((AIntegerPrologTerm) id).longValueExact() >= 0)
				transitionIds.add(((AIntegerPrologTerm) id).longValueExact());
		steps = ((AIntegerPrologTerm) bindings.get(STEPS)).intValueExact();
		messages = PrologTerm.atomicStrings(BindingGenerator.getList(bindings,
				MESSAGES));
	}
}
