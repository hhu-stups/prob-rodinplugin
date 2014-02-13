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
	private ModelCheckingResult<Result> result;

	public static enum Result {
		ok(true), ok_not_all_nodes_considered(true), deadlock(true), invariant_violation(
				true), assertion_violation(true), not_yet_finished(false), state_error(
				true), well_definedness_error(true), general_error(true);
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
		this.time = time;
		this.options = options;
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
		int processedTotal = ((IntegerPrologTerm) stats.getArgument(3))
				.getValue().intValue();
		int numStates = ((IntegerPrologTerm) stats.getArgument(1)).getValue()
				.intValue();
		int numTransitions = ((IntegerPrologTerm) stats.getArgument(2))
				.getValue().intValue();

		result = new ModelCheckingResult<Result>(Result.class, term,
				processedTotal, numStates, numTransitions);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_modelchecking").printNumber(time).openList();
		for (String o : options) {
			pto.printAtom(o);
		}
		pto.closeList().printVariable("Result");
		pto.printVariable("Stats").closeTerm();
	}
}
