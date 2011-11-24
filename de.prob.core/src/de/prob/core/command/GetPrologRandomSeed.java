/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.math.BigInteger;

import de.prob.core.Animator;
import de.prob.core.domainobjects.RandomSeed;
import de.prob.exceptions.ProBException;
import de.prob.parser.BindingGenerator;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parser.ResultParserException;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class GetPrologRandomSeed implements IComposableCommand {

	private RandomSeed randomSeed;

	public static RandomSeed getSeed(final Animator a) throws ProBException {
		final GetPrologRandomSeed cmd = new GetPrologRandomSeed();
		a.execute(cmd);
		return cmd.getSeed();
	}

	public RandomSeed getSeed() {
		return randomSeed;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		BigInteger x, y, z, b;
		try {
			x = BindingGenerator.getInteger(bindings.get("X")).getValue();
			y = BindingGenerator.getInteger(bindings.get("Y")).getValue();
			z = BindingGenerator.getInteger(bindings.get("Z")).getValue();
			b = BindingGenerator.getInteger(bindings.get("B")).getValue();
		} catch (ResultParserException e) {
			CommandException commandException = new CommandException(
					e.getLocalizedMessage(), e);
			commandException.notifyUserOnce();
			throw commandException;
		}

		randomSeed = new RandomSeed(x, y, z, b);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("get_rand").printVariable("X").printVariable("Y")
				.printVariable("Z").printVariable("B").closeTerm();
	}

}
