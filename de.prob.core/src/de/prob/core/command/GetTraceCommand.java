/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetTraceCommand implements IComposableCommand {

	private static final String TRACE_VARIABLE = "Trace";

	private final static class Occurence {
		private final String text;

		private int count;

		public Occurence(final String text) {
			this.text = text;
			this.count = 1;
		}

		public synchronized void inc() {
			this.count++;
		}

		@Override
		public synchronized String toString() {
			return text + ((count > 1) ? " (" + count + " times)" : "");
		}

	}

	private List<String> trace;

	private GetTraceCommand() {
	}

	public static List<String> getTrace(final Animator a) throws ProBException {
		GetTraceCommand getTraceCommand = new GetTraceCommand();
		a.execute(getTraceCommand);
		return getTraceCommand.getTrace();
	}

	private List<String> getTrace() {
		return trace;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		List<Occurence> res = new LinkedList<Occurence>();

		ListPrologTerm list = (ListPrologTerm) bindings.get(TRACE_VARIABLE);

		Occurence current = null;
		for (PrologTerm term : list) {
			if (current == null || !current.text.equals(term.toString())) {
				current = new Occurence(term.toString());
				res.add(current);
			} else {
				current.inc();
			}
		}

		final List<String> actions = new ArrayList<String>();
		for (Occurence occurence : res) {
			actions.add(occurence.toString());
		}

		this.trace = actions;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("find_shortest_trace_to_current_state")
				.printVariable(TRACE_VARIABLE).closeTerm();
	}

}
