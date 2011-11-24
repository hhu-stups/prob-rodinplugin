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

/**
 * This command makes ProB search for a invariant violation with an optional
 * selection of events.
 * 
 * @author plagge
 */
public class ConstraintBasedInvariantCheckCommand implements IComposableCommand {

	public static enum ResultType {
		VIOLATION_FOUND, NO_VIOLATION_FOUND, INTERRUPTED
	};

	public static class InvariantCheckCounterExample {
		private final String eventName;
		private final Operation step1, step2;

		public InvariantCheckCounterExample(final String eventName,
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

	private static final String COMMAND_NAME = "invariant_check";
	private static final String RESULT_VARIABLE = "R";

	private final Collection<String> events;

	private ResultType result;
	private Collection<InvariantCheckCounterExample> counterexamples;

	/**
	 * @param events
	 *            is a collection of names of that events that should be
	 *            checked. May be <code>null</code>. In that case, all events
	 *            are checked.
	 */
	public ConstraintBasedInvariantCheckCommand(final Collection<String> events) {
		this.events = events == null ? null : Collections
				.unmodifiableCollection(new ArrayList<String>(events));
	}

	public Collection<String> getEvents() {
		return events;
	}

	public ResultType getResult() {
		return result;
	}

	public Collection<InvariantCheckCounterExample> getCounterExamples() {
		return counterexamples;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND_NAME);
		if (events != null && !events.isEmpty()) {
			pto.openTerm("ops");
			pto.openList();
			for (final String event : events) {
				pto.printAtom(event);
			}
			pto.closeList();
			pto.closeTerm();
		} else {
			pto.printAtom("all");
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
		final Collection<InvariantCheckCounterExample> counterexamples;
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

	private Collection<InvariantCheckCounterExample> extractExamples(
			final ListPrologTerm ceTerm) {
		Collection<InvariantCheckCounterExample> examples = new ArrayList<ConstraintBasedInvariantCheckCommand.InvariantCheckCounterExample>();
		for (final PrologTerm t : ceTerm) {
			final CompoundPrologTerm term = (CompoundPrologTerm) t;
			final String eventName = PrologTerm.atomicString(term
					.getArgument(1));
			final Operation step1 = Operation
					.fromPrologTerm((CompoundPrologTerm) term.getArgument(2));
			final Operation step2 = Operation
					.fromPrologTerm((CompoundPrologTerm) term.getArgument(3));
			final InvariantCheckCounterExample ce = new InvariantCheckCounterExample(
					eventName, step1, step2);
			examples.add(ce);
		}
		return examples;
	}
}
