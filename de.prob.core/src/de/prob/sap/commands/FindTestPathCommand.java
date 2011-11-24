/**
 * 
 */
package de.prob.sap.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eventb.core.ast.Predicate;

import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;
import de.prob.sap.util.FormulaUtils;

/**
 * This is a command that takes a list of events and a predicate over a final
 * state and ask ProB to find a concrete path (with concrete states and
 * parameter values).
 * 
 * If ProB finds a value, the result can be accessed via {@link #getTrace()}
 * 
 * @author plagge
 */
public class FindTestPathCommand implements IComposableCommand {
	private final String COMMAND = "sap_find_test_path";
	private final String TRACE_VAR = "Trace";

	private final List<String> events;
	private final Predicate endPredicate;
	private final int timeout;

	public enum TestPathStatus {
		FOUND, NOT_FOUND, TIMEOUT, INTERRUPTED
	};

	private List<Operation> trace;
	private TestPathStatus status;

	/**
	 * @param events
	 *            a list of event names, never <code>null</code>
	 * @param endPredicate
	 *            the predicate that constrains the last reached state.
	 * @param timeout
	 *            how many milliseconds the operation should run maximally (0 if
	 *            no time out is needed)
	 */
	public FindTestPathCommand(final List<String> events,
			final Predicate endPredicate, final int timeout) {
		this.events = Collections.unmodifiableList(events);
		this.endPredicate = endPredicate;
		this.timeout = timeout <= 0 ? 0 : timeout;
	}

	/**
	 * @param events
	 *            a list of event names, never <code>null</code>
	 * @param endPredicate
	 *            the predicate that constrains the last reached state.
	 */
	public FindTestPathCommand(final List<String> events,
			final Predicate endPredicate) {
		this(events, endPredicate, 0);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final TestPathStatus status;
		final List<Operation> trace;

		final PrologTerm answer = bindings.get(TRACE_VAR);
		if (answer.hasFunctor("timeout", 0)) {
			status = TestPathStatus.TIMEOUT;
			trace = null;
		} else if (answer.hasFunctor("interrupt", 0)) {
			status = TestPathStatus.INTERRUPTED;
			trace = null;
		} else if (answer.isList()) {
			List<PrologTerm> pTrace = (ListPrologTerm) answer;
			if (pTrace.isEmpty()) {
				status = TestPathStatus.NOT_FOUND;
				trace = null;
			} else {
				status = TestPathStatus.FOUND;
				trace = new ArrayList<Operation>(pTrace.size());
				for (final PrologTerm opTerm : pTrace) {
					final CompoundPrologTerm cpt = (CompoundPrologTerm) opTerm;
					trace.add(Operation.fromPrologTerm(cpt));
				}
			}
		} else {
			throw new CommandException("Unexpected Prolog answer: " + answer);
		}

		this.status = status;
		this.trace = trace;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm(COMMAND);
		pto.openList();
		for (final String event : events) {
			pto.printAtom(event);
		}
		pto.closeList();
		FormulaUtils.printPredicate(endPredicate, pto);
		pto.printNumber(timeout);
		pto.printVariable(TRACE_VAR);
		pto.closeTerm();
	}

	/**
	 * Returns the found trace if one was found.
	 * 
	 * @return a list of events or <code>null</code> if non was found.
	 */
	public List<Operation> getTrace() {
		return trace;
	}

	public TestPathStatus getStatus() {
		return status;
	}
}
