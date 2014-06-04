/**
 * 
 */
package de.prob.core.command;

import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.domainobjects.Operation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class FindValidStateCommand implements IComposableCommand {

	public static enum ResultType {
		STATE_FOUND, NO_STATE_FOUND, INTERRUPTED, ERROR
	};

	private static final String COMMAND_NAME = "find_state_satisfying_predicate";
	private static final String RESULT_VARIABLE = "R";

	private final PrologTerm predicate;

	private ResultType result;
	private String stateId;
	private Operation operation;

	/**
	 * @param predicate
	 *            is a parsed predicate or <code>null</code>
	 * @see LanguageDependendAnimationPart#parsePredicate(IPrologTermOutput,
	 *      String, boolean)
	 */
	public FindValidStateCommand(final PrologTerm predicate) {
		this.predicate = predicate;
	}

	public PrologTerm getPredicate() {
		return predicate;
	}

	public ResultType getResult() {
		return result;
	}

	public String getStateId() {
		return stateId;
	}

	public Operation getOperation() {
		return operation;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		if (predicate != null) {
			predicate.toTermOutput(pto);
		}
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;

		if (resultTerm.hasFunctor("no_valid_state_found", 0)) {
			result = ResultType.NO_STATE_FOUND;
		} else if (resultTerm.hasFunctor("errors", 1)) {
			result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("state_found", 2)) {
			CompoundPrologTerm term = (CompoundPrologTerm) resultTerm;
			result = ResultType.STATE_FOUND;
			operation = Operation.fromPrologTerm(term.getArgument(1));
			stateId = term.getArgument(2).toString();
		} else
			throw new CommandException(
					"unexpected result from deadlock check: " + resultTerm);

		this.result = result;
	}
}
