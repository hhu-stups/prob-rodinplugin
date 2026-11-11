/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Asks ProB for the states in an equivalence class of its last state space
 * reduction, i.e., in a node of the last Signature Merge or State Space
 * Expression Projection graph, using
 * <code>prob2_get_equivalence_class_states/4</code>.
 */
public final class GetEquivalenceClassStatesCommand implements
		IComposableCommand {

	private static final String STATES = "States";
	private static final String TOTAL = "Total";

	private final long classId;
	private final int limit;
	private List<String> states = Collections.emptyList();
	private int total;

	/**
	 * @param limit
	 *            the maximal number of states returned
	 */
	public GetEquivalenceClassStatesCommand(final long classId,
			final int limit) {
		this.classId = classId;
		this.limit = limit;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob2_get_equivalence_class_states")
				.printNumber(classId).printNumber(limit)
				.printVariable(STATES).printVariable(TOTAL).closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<String> result = new ArrayList<String>();
		for (PrologTerm id : BindingGenerator.getList(bindings, STATES))
			result.add(GetCommandsCommand.text(id));
		states = result;
		final PrologTerm t = bindings.get(TOTAL);
		total = t instanceof AIntegerPrologTerm ? ((AIntegerPrologTerm) t)
				.intValueExact() : result.size();
	}

	/** @return the ids of the states, e.g. root or 12 */
	public List<String> getStates() {
		return states;
	}

	/** @return the number of states in the class */
	public int getTotal() {
		return total;
	}
}
