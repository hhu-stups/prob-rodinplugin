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

public class ClearMachineCommand implements IComposableCommand {
	public ClearMachineCommand() {}

	public static void clearMachine(final Animator animator)
			throws ProBException {
		animator.execute(new ClearMachineCommand());
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("clear_loaded_machines").closeTerm();
	}
}
