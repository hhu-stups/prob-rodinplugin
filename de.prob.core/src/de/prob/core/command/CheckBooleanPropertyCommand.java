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
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

/*
 * This class is quasi abstract, do not instantiate this class. Use the derived
 * classes or the static methods to retrieve the boolean values
 */
public class CheckBooleanPropertyCommand implements IComposableCommand {
	private static final String PROP_RESULT = "PropResult";
	private static final PrologTerm PROLOG_TRUE = new CompoundPrologTerm("true");
	private static final PrologTerm PROLOG_FALSE = new CompoundPrologTerm(
			"false");

	private final String stateId;
	private final String propertyName;
	private Boolean result;

	protected CheckBooleanPropertyCommand(final String propertyName,
			final String stateId) {
		this.propertyName = propertyName;
		this.stateId = stateId;
	}

	public static boolean isPropertyTrue(final Animator a,
			final String propertyName, final String stateId)
			throws ProBException {
		CheckBooleanPropertyCommand checkPropertyCommand = new CheckBooleanPropertyCommand(
				propertyName, stateId);
		a.execute(checkPropertyCommand);
		return checkPropertyCommand.getResult();
	}

	//
	// IComposableCommand
	//

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		PrologTerm answer = bindings.get(PROP_RESULT);
		if (PROLOG_TRUE.equals(answer)) {
			result = true;
		} else if (PROLOG_FALSE.equals(answer)) {
			result = false;
		} else {
			result = null;
			throw new CommandException("Expected true or false, but was: "
					+ answer);
		}
	}

	private static void writeCommand(final IPrologTermOutput pto,
			final String propertyName, final String stateId) {
		pto.openTerm("state_property");
		pto.printAtom(propertyName);
		pto.printAtomOrNumber(stateId);
		pto.printVariable(PROP_RESULT);
		pto.closeTerm();
	}

	public void writeCommand(final IPrologTermOutput pto) {
		writeCommand(pto, propertyName, stateId);
	}

	public boolean getResult() {
		if (result == null)
			throw new IllegalStateException(
					"Cannot get result before finishing query");
		return result;
	}

}
