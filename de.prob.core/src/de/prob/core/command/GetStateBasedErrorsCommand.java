/**
 * 
 */
package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.domainobjects.StateError;
import de.prob.exceptions.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * This command asks ProB if a certain state has errors associated to it.
 * 
 * @author plagge
 */
public class GetStateBasedErrorsCommand implements IComposableCommand {

	private final String stateId;
	private Collection<StateError> stateErrors;

	public GetStateBasedErrorsCommand(final String stateId) {
		this.stateId = stateId;
	}

	public static Collection<StateError> getStateValues(final Animator a,
			final String id) throws ProBException {
		GetStateBasedErrorsCommand command = new GetStateBasedErrorsCommand(id);
		a.execute(command);
		return command.getResult();
	}

	//
	// ComposableCommand
	//

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final List<StateError> errors;
		ListPrologTerm list;
		try {
			list = BindingGenerator.getList(bindings, "Errors");
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}

		if (list.isEmpty()) {
			errors = Collections.emptyList();
		} else {
			errors = new ArrayList<StateError>();
			for (PrologTerm term : list) {
				CompoundPrologTerm compoundTerm;
				try {
					compoundTerm = BindingGenerator.getCompoundTerm(term,
							"error", 3);
				} catch (ResultParserException e) {
					CommandException commandException = new CommandException(
							e.getLocalizedMessage(), e);
					commandException.notifyUserOnce();
					throw commandException;
				}
				errors.add(new StateError(compoundTerm));
			}
		}
		this.stateErrors = errors;
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_state_errors").printAtomOrNumber(stateId)
				.printVariable("Errors").closeTerm();
	}

	public Collection<StateError> getResult() {
		return stateErrors;
	}

}
