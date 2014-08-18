/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.command;

import java.util.*;

import de.prob.prolog.term.*;

public class ModelCheckingResult<T extends Enum<T>> {

	private final T result;
	private final List<PrologTerm> arguments = new ArrayList<PrologTerm>();
	private final int processedTotal, numStates, numTransitions;

	public ModelCheckingResult(final Class<T> enumeration,
			final CompoundPrologTerm term, int total, int numStates,
			int numTransitions) {
		result = Enum.valueOf(enumeration, term.getFunctor());
		for (int i = 1; i <= term.getArity(); i++) {
			arguments.add(term.getArgument(i));
		}
		processedTotal = total;
		this.numStates = numStates;
		this.numTransitions = numTransitions;
	}

	public PrologTerm getArgument(final int i) {
		return arguments.get(i);
	}

	public T getResult() {
		return result;
	}

	public int getProcessedTotal() {
		return processedTotal;
	}

	public int getNumStates() {
		return numStates;
	}

	public int getNumTransitions() {
		return numTransitions;
	}

	public int getWorked() {
		return (int) (1000 * Math.pow((double) processedTotal / numStates, 5));
	}
}
