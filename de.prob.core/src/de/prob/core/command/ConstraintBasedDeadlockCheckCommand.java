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

/**
 * This command makes ProB search for a deadlock with an optional predicate to
 * limit the search space.
 * 
 * @author plagge
 */
public class ConstraintBasedDeadlockCheckCommand implements IComposableCommand {

	public static enum ResultType {
		DEADLOCK_FOUND, NO_DEADLOCK, ERROR, INTERRUPTED
	};

	private static final String COMMAND_NAME = "deadlock_freedom_check";
	private static final String RESULT_VARIABLE = "R";

	private final PrologTerm predicate;

	private ResultType result;
	private String deadlockStateId;
	private Operation deadlockOperation;

	/**
	 * @param predicate
	 *            is a parsed predicate or <code>null</code>
	 * @see LanguageDependendAnimationPart#parsePredicate(IPrologTermOutput,
	 *      String, boolean)
	 */
	public ConstraintBasedDeadlockCheckCommand(final PrologTerm predicate) {
		this.predicate = predicate;
	}

	public PrologTerm getPredicate() {
		return predicate;
	}

	public ResultType getResult() {
		return result;
	}

	public String getDeadlockStateId() {
		return deadlockStateId;
	}

	public Operation getDeadlockOperation() {
		return deadlockOperation;
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
		if (resultTerm.hasFunctor("no_deadlock_found", 0)) {
			result = ResultType.NO_DEADLOCK;
		} else if (resultTerm.hasFunctor("errors", 1)) {
			result = ResultType.ERROR;
		} else if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("deadlock", 2)) {
			CompoundPrologTerm deadlockTerm = (CompoundPrologTerm) resultTerm;
			result = ResultType.DEADLOCK_FOUND;
			deadlockOperation = Operation
					.fromPrologTerm((CompoundPrologTerm) deadlockTerm
							.getArgument(1));
			deadlockStateId = deadlockTerm.getArgument(2).toString();
		} else
			throw new CommandException(
					"unexpected result from deadlock check: " + resultTerm);
		this.result = result;

	}
}
