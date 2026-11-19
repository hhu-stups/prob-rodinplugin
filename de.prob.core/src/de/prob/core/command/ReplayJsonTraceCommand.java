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
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Replays a ProB2 JSON trace file (.prob2trace), using
 * <code>prob2_replay_json_trace_file_with_table/6</code>, and makes the
 * replayed trace the animator's history. With an older probcli, which does not
 * know this predicate, <code>prob2_replay_json_trace_file_with_messages/5</code>
 * is used, which does not explain the replay of each step.
 */
public final class ReplayJsonTraceCommand implements IComposableCommand {

	private static final String STATUS = "Status";
	private static final String TRANSITIONS = "TransIds";
	private static final String STEPS = "Steps";
	private static final String MESSAGES = "Messages";
	private static final String TABLE = "Table";

	/**
	 * whether probcli knows prob2_replay_json_trace_file_with_table/6; null
	 * until a replay has found out
	 */
	private static volatile Boolean tableSupported;

	/** how one step of the trace file was replayed */
	public static final class Step {
		private final int number;
		private final String traceFile;
		private final String replayed;
		private final String match;
		private final List<String> mismatches;
		private final List<String> errors;

		Step(final int number, final String traceFile, final String replayed,
				final String match, final List<String> mismatches,
				final List<String> errors) {
			this.number = number;
			this.traceFile = traceFile;
			this.replayed = replayed;
			this.match = match;
			this.mismatches = mismatches;
			this.errors = errors;
		}

		/** @return the number of the step in the trace file, from 1 */
		public int getNumber() {
			return number;
		}

		/** @return the step as stored in the trace file */
		public String getTraceFile() {
			return traceFile;
		}

		/** @return the replayed operation, "skipped" or "-" */
		public String getReplayed() {
			return replayed;
		}

		/**
		 * @return how the step was matched, e.g. precise, precise_with_errs,
		 *         params_and_results, keep_name or failed
		 */
		public String getMatch() {
			return match;
		}

		/**
		 * @return the identifiers whose values differ from the trace file
		 *         (Name if the operation differs)
		 */
		public List<String> getMismatches() {
			return mismatches;
		}

		/** @return the replay errors of the step */
		public List<String> getErrors() {
			return errors;
		}

		/** @return true if the step was replayed precisely without errors */
		public boolean isPrecise() {
			return "precise".equals(match) && errors.isEmpty()
					&& mismatches.isEmpty();
		}

		public boolean isFailed() {
			return "failed".equals(match) || "-".equals(match);
		}
	}

	/** the outcome of a replay */
	public static final class Result {
		private final String status;
		private final int replayedSteps;
		private final int steps;
		private final List<String> messages;
		private final List<Step> table;

		Result(final String status, final int replayedSteps, final int steps,
				final List<String> messages, final List<Step> table) {
			this.status = status;
			this.replayedSteps = replayedSteps;
			this.steps = steps;
			this.messages = messages;
			this.table = table;
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

		/**
		 * @return how each step of the trace file was replayed; empty with an
		 *         older probcli
		 */
		public List<Step> getTable() {
			return table;
		}
	}

	private final File file;
	private String status;
	private final List<Long> transitionIds = new ArrayList<Long>();
	private int steps;
	private List<String> messages = Collections.emptyList();
	private final boolean withTable;
	private List<Step> table = Collections.emptyList();

	public ReplayJsonTraceCommand(final File file) {
		this(file, true);
	}

	/**
	 * @param withTable
	 *            false to use prob2_replay_json_trace_file_with_messages/5,
	 *            for an older probcli
	 */
	public ReplayJsonTraceCommand(final File file, final boolean withTable) {
		this.file = file;
		this.withTable = withTable;
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
				final ReplayJsonTraceCommand command = execute(animator, file);
				final int replayed = command.setHistory(animator);
				return new Result(command.status, replayed, command.steps,
						command.messages, command.table);
			}
		});
	}

	/**
	 * Replays with the table if probcli may know the predicate; if this fails
	 * but the older predicate works, probcli does not know it.
	 */
	private static ReplayJsonTraceCommand execute(final Animator animator,
			final File file) throws ProBException {
		if (Boolean.FALSE.equals(tableSupported)) {
			final ReplayJsonTraceCommand command = new ReplayJsonTraceCommand(
					file, false);
			animator.execute(command);
			return command;
		}
		final ReplayJsonTraceCommand command = new ReplayJsonTraceCommand(
				file, true);
		try {
			animator.execute(command);
			tableSupported = Boolean.TRUE;
			return command;
		} catch (ProBException e) {
			if (tableSupported != null)
				throw e;
			final ReplayJsonTraceCommand older = new ReplayJsonTraceCommand(
					file, false);
			animator.execute(older); // throws if the replay itself fails
			tableSupported = Boolean.FALSE;
			return older;
		}
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
		pto.openTerm(withTable ? "prob2_replay_json_trace_file_with_table"
				: "prob2_replay_json_trace_file_with_messages");
		pto.printAtom(file.getAbsolutePath());
		pto.printVariable(STATUS);
		pto.printVariable(TRANSITIONS);
		pto.printVariable(STEPS);
		pto.printVariable(MESSAGES);
		if (withTable)
			pto.printVariable(TABLE);
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
		if (withTable) {
			final List<Step> rows = new ArrayList<Step>();
			for (PrologTerm row : BindingGenerator.getList(bindings, TABLE))
				if (row instanceof CompoundPrologTerm && row.getArity() == 6)
					rows.add(step((CompoundPrologTerm) row));
			table = rows;
		}
	}

	/** @param row row(Step,TraceFileTxt,ReplayedTxt,MatchInfo,Mismatches,Errors) */
	private static Step step(final CompoundPrologTerm row) {
		final PrologTerm nr = row.getArgument(1);
		return new Step(nr instanceof AIntegerPrologTerm
				? ((AIntegerPrologTerm) nr).intValueExact() : 0,
				GetCommandsCommand.text(row.getArgument(2)),
				GetCommandsCommand.text(row.getArgument(3)),
				GetCommandsCommand.text(row.getArgument(4)),
				texts(row.getArgument(5)), texts(row.getArgument(6)));
	}

	private static List<String> texts(final PrologTerm list) {
		final List<String> result = new ArrayList<String>();
		if (list instanceof ListPrologTerm)
			for (PrologTerm t : (ListPrologTerm) list)
				result.add(GetCommandsCommand.text(t));
		return result;
	}
}
