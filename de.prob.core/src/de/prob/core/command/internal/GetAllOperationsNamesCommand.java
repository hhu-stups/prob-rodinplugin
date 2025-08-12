package de.prob.core.command.internal;

import de.prob.core.command.CommandException;
import de.prob.core.command.IComposableCommand;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class GetAllOperationsNamesCommand implements IComposableCommand {

	private static final String NAMES_VARIABLE = "Names";
	private ListPrologTerm term;

	public ListPrologTerm getNamesTerm() {
		return term;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		term = (ListPrologTerm) bindings.get(NAMES_VARIABLE);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getAllOperations").printVariable(NAMES_VARIABLE)
				.closeTerm();
	}

}
