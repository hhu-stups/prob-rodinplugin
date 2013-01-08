/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetPluginResultCommand implements IComposableCommand {

	private final String resultID;
	private ListPrologTerm result;

	public GetPluginResultCommand(final String resultID) {
		this.resultID = resultID;
	}

	public ListPrologTerm getResult() {
		return result;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		try {
			result = BindingGenerator.getList(bindings, "Bindings");
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_plugin_output").printAtomOrNumber(resultID)
				.printVariable("Bindings").closeTerm();
	}

}
