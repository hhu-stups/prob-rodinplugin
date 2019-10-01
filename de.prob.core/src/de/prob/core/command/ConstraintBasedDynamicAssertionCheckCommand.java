/**
 * 
 */
package de.prob.core.command;

import de.prob.core.domainobjects.Operation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command makes ProB search for a violation of the dynamic assertions on variables
 * derived from ConstraintBasedAssertionCheckCommand; one could parameterise it
 * @author leuschel
 */
public class ConstraintBasedDynamicAssertionCheckCommand implements IComposableCommand {

	public static enum ResultType {
		INTERRUPTED, COUNTER_EXAMPLE, NO_COUNTER_EXAMPLE_FOUND, NO_COUNTER_EXAMPLE_EXISTS
	};

	private static final String COMMAND_NAME = "cbc_dynamic_assertion_violation_checking";
	private static final String RESULT_VARIABLE = "R";

	private ResultType result;
	private Operation counterExampleOperation;
	private String counterExampleStateID;

	public ConstraintBasedDynamicAssertionCheckCommand() {
	}

	public ResultType getResult() {
		return result;
	}

	public Operation getCounterExampleOperation() {
		return counterExampleOperation;
	}

	public String getCounterExampleStateID() {
		return counterExampleStateID;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;
		if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("no_counterexample_found", 0)) {
			result = ResultType.NO_COUNTER_EXAMPLE_FOUND;
		}  else if (resultTerm.hasFunctor("no_counterexample_exists", 0)) {
			result = ResultType.NO_COUNTER_EXAMPLE_EXISTS;
		} else if (resultTerm.hasFunctor("counterexample_found", 2)) {
			result = ResultType.COUNTER_EXAMPLE;
			CompoundPrologTerm counterExampleTerm = (CompoundPrologTerm) resultTerm;
			counterExampleOperation = Operation
					.fromPrologTerm(counterExampleTerm.getArgument(1));
			counterExampleStateID = counterExampleTerm.getArgument(2)
					.toString();
		} else
			throw new CommandException(
					"unexpected result from dynamic assertion check: " + resultTerm);
		this.result = result;

	}
}
