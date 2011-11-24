/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.domainobjects.Variable;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetStateValuesCommand implements IComposableCommand {

	private final String stateId;
	private List<Variable> result;

	public GetStateValuesCommand(final String stateID) {
		stateId = stateID;
	}

	public List<Variable> getResult() {
		return result;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<Variable> variables = new ArrayList<Variable>();

		ListPrologTerm list;
		try {
			list = BindingGenerator.getList(bindings, "Bindings");
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}

		for (PrologTerm term : list) {
			CompoundPrologTerm compoundTerm;
			try {
				compoundTerm = BindingGenerator.getCompoundTerm(term,
						"binding", 3);
			} catch (ResultParserException e) {
				CommandException commandException = new CommandException(
						e.getLocalizedMessage(), e);
				commandException.notifyUserOnce();
				throw commandException;
			}
			variables.add(new Variable(compoundTerm));
		}
		result = variables;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getStateValues").printAtomOrNumber(stateId)
				.printVariable("Bindings").closeTerm();
	}

}
