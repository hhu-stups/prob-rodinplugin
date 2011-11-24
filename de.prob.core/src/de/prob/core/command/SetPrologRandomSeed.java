/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.core.domainobjects.RandomSeed;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class SetPrologRandomSeed implements IComposableCommand {

	private final RandomSeed seed;

	public SetPrologRandomSeed(final RandomSeed seed) {
		this.seed = seed;
	}

	public static void setSeed(final Animator a, final RandomSeed seed)
			throws ProBException {
		SetPrologRandomSeed cmd = new SetPrologRandomSeed(seed);
		a.execute(cmd);
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// no result, nothing to do
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("set_rand");
		pto.printNumber(seed.getSeedX());
		pto.printNumber(seed.getSeedY());
		pto.printNumber(seed.getSeedZ());
		pto.printNumber(seed.getSeedB());
		pto.closeTerm();
	}
}
