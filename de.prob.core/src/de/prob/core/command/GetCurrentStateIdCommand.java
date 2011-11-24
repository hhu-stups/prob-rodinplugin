/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

public final class GetCurrentStateIdCommand implements IComposableCommand {

	public static String getStateID(final PrologTerm term) {
		final String result;
		if (term.isAtom()) {
			result = PrologTerm.atomicString(term);
		} else {
			result = term.toString();
		}
		return result;
	}

	public static List<String> getStateIDs(final Collection<PrologTerm> terms) {
		final List<String> ids = new ArrayList<String>(terms.size());
		for (final PrologTerm term : terms) {
			ids.add(getStateID(term));
		}
		return ids;
	}

	private String currentID;

	private GetCurrentStateIdCommand() {
	}

	public static String getID(final Animator a) throws ProBException {
		GetCurrentStateIdCommand cmd = new GetCurrentStateIdCommand();
		a.execute(cmd);
		return cmd.getCurrentStateId();
	}

	private String getCurrentStateId() {
		return currentID;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		currentID = getStateID(bindings.get("ID"));
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("getCurrentStateID").printVariable("ID").closeTerm();
	}
}
