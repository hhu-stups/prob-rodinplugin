package de.prob.core.command;

import java.util.List;

import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class GetTimeoutedOperationsCommand implements IComposableCommand {

	private static final String TIMEOUT_VARIABLE = "TO";
	private final String state;
	private List<String> timeouts;

	public GetTimeoutedOperationsCommand(final String state) {
		this.state = state;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		try {
			timeouts = PrologTerm.atomicStrings(BindingGenerator.getList(
					bindings, TIMEOUT_VARIABLE));
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("op_timeout_occurred").printAtomOrNumber(state)
				.printVariable(TIMEOUT_VARIABLE).closeTerm();
	}

	public List<String> getTimeouts() {
		return timeouts;
	}
}
