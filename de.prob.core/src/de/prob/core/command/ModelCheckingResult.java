/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.PrologTerm;

public class ModelCheckingResult<T extends Enum<T>> {

	private final T result;
	private final List<PrologTerm> arguments = new ArrayList<PrologTerm>();

	public ModelCheckingResult(final Class<T> enumeration,
			final CompoundPrologTerm term) {
		result = Enum.valueOf(enumeration, term.getFunctor());
		for (int i = 1; i <= term.getArity(); i++) {
			arguments.add(term.getArgument(i));
		}
	}

	public PrologTerm getArgument(final int i) {
		return arguments.get(i);
	}

	public T getResult() {
		return result;
	}

}
