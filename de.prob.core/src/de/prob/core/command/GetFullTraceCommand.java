/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetFullTraceCommand implements IComposableCommand {

	private static final String IDS_VARIABLE = "IDs";
	private static final String ACTIONS_VARIABLE = "Actions";

	public static class TraceResult {
		private final List<String> operations;
		private final List<String> states;

		public TraceResult(final List<String> operations,
				final List<String> states) {
			Assert.isTrue(operations.size() == states.size());
			this.operations = Collections.unmodifiableList(operations);
			this.states = Collections.unmodifiableList(states);
		}

		public List<String> getOperations() {
			return operations;
		}

		public List<String> getStates() {
			return states;
		}
	}

	private TraceResult trace;

	private GetFullTraceCommand() {
	}

	public static TraceResult getTrace(final Animator a) throws ProBException {
		GetFullTraceCommand cmd = new GetFullTraceCommand();
		a.execute(cmd);
		return cmd.getTrace();
	}

	private TraceResult getTrace() {
		return trace;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		List<String> operations = PrologTerm
				.atomicStrings((ListPrologTerm) bindings.get(ACTIONS_VARIABLE));
		List<String> states = GetCurrentStateIdCommand
				.getStateIDs((ListPrologTerm) bindings.get(IDS_VARIABLE));
		trace = new TraceResult(operations, states);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_shortest_trace_to_current_state2");
		pto.printVariable(ACTIONS_VARIABLE);
		pto.printVariable(IDS_VARIABLE).closeTerm();
	}
}
