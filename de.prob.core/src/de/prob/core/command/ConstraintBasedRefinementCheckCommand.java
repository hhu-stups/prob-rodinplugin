/**
 * 
 */
package de.prob.core.command;

import de.prob.core.domainobjects.Operation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ConstraintBasedRefinementCheckCommand implements
		IComposableCommand {

	public static enum ResultType {
		VIOLATION_FOUND, NO_VIOLATION_FOUND, INTERRUPTED
	};

	public static class RefinementCheckCounterExample {
		private final String eventName;
		private final Operation step1, step2;

		public RefinementCheckCounterExample(final String eventName,
				final Operation step1, final Operation step2) {
			this.eventName = eventName;
			this.step1 = step1;
			this.step2 = step2;
		}

		public String getEventName() {
			return eventName;
		}

		public Operation getStep1() {
			return step1;
		}

		public Operation getStep2() {
			return step2;
		}
	}

	private static final String COMMAND_NAME = "refinement_check";
	private static final String RESULT_VARIABLE = "R";
	private static final String RESULT_STRINGS_VARIABLE = "S";

	private ResultType result;
	private String resultsString = "";

	/**
	 * @param events
	 *            is a collection of names of that events that should be
	 *            checked. May be <code>null</code>. In that case, all events
	 *            are checked.
	 */
	public ConstraintBasedRefinementCheckCommand() {
	}

	public ResultType getResult() {
		return result;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		pto.printVariable(RESULT_STRINGS_VARIABLE);
		pto.printVariable(RESULT_VARIABLE);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final PrologTerm resultTerm = bindings.get(RESULT_VARIABLE);
		final ResultType result;

		final ListPrologTerm resultStringTerm = (ListPrologTerm) bindings
				.get(RESULT_STRINGS_VARIABLE);

		System.out.println(resultStringTerm.toString());

		for (PrologTerm t : resultStringTerm) {
			resultsString += PrologTerm.atomicString(t) + "\n";
		}

		if (resultTerm.hasFunctor("time_out", 0)) {
			result = ResultType.INTERRUPTED;
		} else if (resultTerm.hasFunctor("true", 0)) { // Errors were found
			result = ResultType.VIOLATION_FOUND;
			// TODO extract message
		} else if (resultTerm.hasFunctor("false", 0)) { // Errors were found
			result = ResultType.NO_VIOLATION_FOUND;
		} else
			throw new CommandException(
					"unexpected result from refinement check: " + resultTerm);
		this.result = result;
	}

	public String getResultsString() {
		return resultsString;
	}
}
