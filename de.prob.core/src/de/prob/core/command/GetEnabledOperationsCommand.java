/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.List;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

public final class GetEnabledOperationsCommand implements IComposableCommand {

	private static final String OPERATIONS_VARIABLE = "PLOps";
	private final String id;
	private List<Operation> enabledOperations;

	public GetEnabledOperationsCommand(final String id) {
		this.id = id;
	}

	public static List<Operation> getOperations(final Animator animator,
			final String id) throws ProBException {
		GetEnabledOperationsCommand command = new GetEnabledOperationsCommand(
				id);
		animator.execute(command);
		return command.getEnabledOperations();
	}

	//
	// IComposableCommand
	//

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings) {
		enabledOperations = new ArrayList<Operation>();

		final ListPrologTerm prologTerm = (ListPrologTerm) bindings
				.get(OPERATIONS_VARIABLE);
		for (PrologTerm op : prologTerm) {
			final CompoundPrologTerm cpt = (CompoundPrologTerm) op;
			enabledOperations.add(Operation.fromPrologTerm(cpt));
		}
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("computeOperationsForState");
		pto.printAtomOrNumber(id);
		pto.printVariable(OPERATIONS_VARIABLE);
		pto.closeTerm();
	}

	public List<Operation> getEnabledOperations() {
		return enabledOperations;
	}
}
