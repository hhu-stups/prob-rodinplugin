/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetStateCommand implements IComposableCommand {
	private final String stateId;

	public SetStateCommand(final String stateID) {
		stateId = stateID;
	}

	public static void setState(final Animator a, final String stateId)
			throws ProBException {
		final SetStateCommand command = new SetStateCommand(stateId);
		a.execute(command);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// no results, do nothing
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("setCurrentState").printAtomOrNumber(stateId).closeTerm();
	}

}
