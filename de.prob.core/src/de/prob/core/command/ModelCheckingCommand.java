/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.*;

public final class ModelCheckingCommand implements IComposableCommand {
	private final int time;
	private final List<String> options;
	/** the most states to check in this call; null for ProB's default */
	private final Integer maxStates;
	private ModelCheckingResult<Result> result;

	public static enum Result {
		ok(true), ok_not_all_nodes_considered(true), deadlock(true), invariant_violation(
				true), assertion_violation(true), not_yet_finished(false), state_error(
				true), well_definedness_error(true), general_error(true),full_coverage(true);
		// I assume true means we can stop the model checking
		private final boolean abort;

		private Result(final boolean abort) {
			this.abort = abort;
		}

		public boolean isAbort() {
			return abort;
		}
	}

	ModelCheckingCommand(final int time, final List<String> options) {
		this(null, time, options);
	}

	private ModelCheckingCommand(final Integer maxStates, final int time,
			final List<String> options) {
		this.maxStates = maxStates;
		this.time = time;
		this.options = options;
	}

	/**
	 * Like {@link #modelcheck(Animator, int, List)}, but checks at most
	 * maxStates states in this call (<code>do_modelchecking/5</code>).
	 */
	public static ModelCheckingResult<Result> modelcheck(final Animator a,
			final int maxStates, final int time, final List<String> options)
			throws ProBException {
		ModelCheckingCommand command = new ModelCheckingCommand(maxStates,
				time, options);
		a.execute(command);
		return command.getResult();
	}

	/**
	 * ProB's constant in <code>not_yet_finished(N)</code>, see
	 * build_modelcheck_return/2 in prob2_interface.pl
	 */
	private static final int NOT_YET_FINISHED_BASE = 100000;

	/**
	 * @return how many states a call with maxStates checked that returned
	 *         not_yet_finished: all of them if it reached the limit,
	 *         otherwise (at the time limit) as told by ProB; maxStates if
	 *         ProB's answer cannot be read
	 */
	public static int statesCheckedInCall(
			final ModelCheckingResult<Result> result, final int maxStates) {
		try {
			final int n = ((AIntegerPrologTerm) result.getArgument(0))
					.intValueExact();
			// not_yet_finished(100000): the limit was reached
			if (n == NOT_YET_FINISHED_BASE)
				return maxStates;
			final int checked = NOT_YET_FINISHED_BASE - n;
			return checked > 0 && checked <= maxStates ? checked : maxStates;
		} catch (RuntimeException e) {
			return maxStates;
		}
	}

	public static ModelCheckingResult<Result> modelcheck(final Animator a,
			final int time, final List<String> options) throws ProBException {
		ModelCheckingCommand command = new ModelCheckingCommand(time, options);
		a.execute(command);
		return command.getResult();
	}

	private ModelCheckingResult<Result> getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {

		CompoundPrologTerm term = (CompoundPrologTerm) bindings.get("Result");

		CompoundPrologTerm stats = (CompoundPrologTerm) bindings.get("Stats");
		int processedTotal = ((AIntegerPrologTerm) stats.getArgument(3)).intValueExact();
		int numStates = ((AIntegerPrologTerm) stats.getArgument(1)).intValueExact();
		int numTransitions = ((AIntegerPrologTerm) stats.getArgument(2)).intValueExact();

		result = new ModelCheckingResult<Result>(Result.class, term,
				processedTotal, numStates, numTransitions);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_modelchecking");
		if (maxStates != null)
			pto.printNumber(maxStates);
		pto.printNumber(time).openList();
		for (String o : options) {
			pto.printAtom(o);
		}
		pto.closeList().printVariable("Result");
		pto.printVariable("Stats").closeTerm();
	}
}
