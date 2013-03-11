/**
 * 
 */
package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import de.prob.core.domainobjects.Operation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
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

	private ResultType result;
	private Collection<RefinementCheckCounterExample> counterexamples;

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

	public Collection<RefinementCheckCounterExample> getCounterExamples() {
		return counterexamples;
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
		final Collection<RefinementCheckCounterExample> counterexamples;
		if (resultTerm.hasFunctor("interrupted", 0)) {
			result = ResultType.INTERRUPTED;
			counterexamples = null;
		} else if (resultTerm.isList()) {
			ListPrologTerm ceTerm = (ListPrologTerm) resultTerm;
			result = ceTerm.isEmpty() ? ResultType.NO_VIOLATION_FOUND
					: ResultType.VIOLATION_FOUND;
			counterexamples = Collections
					.unmodifiableCollection(extractExamples(ceTerm));
		} else
			throw new CommandException(
					"unexpected result from invariant check: " + resultTerm);
		this.result = result;
		this.counterexamples = counterexamples;
	}

	private Collection<RefinementCheckCounterExample> extractExamples(
			final ListPrologTerm ceTerm) {
		Collection<RefinementCheckCounterExample> examples = new ArrayList<ConstraintBasedRefinementCheckCommand.RefinementCheckCounterExample>();
		for (final PrologTerm t : ceTerm) {
			final CompoundPrologTerm term = (CompoundPrologTerm) t;
			final String eventName = PrologTerm.atomicString(term
					.getArgument(1));
			final Operation step1 = Operation.fromPrologTerm(term
					.getArgument(2));
			final Operation step2 = Operation.fromPrologTerm(term
					.getArgument(3));
			final RefinementCheckCounterExample ce = new RefinementCheckCounterExample(
					eventName, step1, step2);
			examples.add(ce);
		}
		return examples;
	}
}
