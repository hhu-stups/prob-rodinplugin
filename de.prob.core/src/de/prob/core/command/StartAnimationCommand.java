package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public class StartAnimationCommand implements IComposableCommand {

	public static void start(final Animator a) throws ProBException {
		StartAnimationCommand cmd = new StartAnimationCommand();
		a.execute(cmd);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// nothing to do
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.printAtom("start_animation");
	}

}
