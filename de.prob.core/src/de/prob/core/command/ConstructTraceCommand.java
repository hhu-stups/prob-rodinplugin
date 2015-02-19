/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import de.be4.classicalb.core.parser.analysis.prolog.ASTProlog;
import de.be4.classicalb.core.parser.exceptions.BException;
import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.core.domainobjects.StateError;
import de.prob.core.domainobjects.Variable;
import de.prob.core.domainobjects.eval.PredicateEvalElement;
import de.prob.core.internal.Activator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Command to execute an event that has not been enumerated by ProB.
 * 
 * @author Jens Bendisposto
 * 
 */
public final class ConstructTraceCommand implements IComposableCommand {

	private static final String RESULT_VARIABLE_STATES = "States";
	private static final String RESULT_VARIABLE_OPS = "Ops";
	private static final String RESULT_VARIABLE_ERRORS = "Errors";

	private final List<PredicateEvalElement> evalElement;
	private final List<String> name;
	private final List<String> errors = new ArrayList<String>();
	private List<Integer> executionNumber = new ArrayList<Integer>();

	public ConstructTraceCommand(final List<String> name,
			final List<String> predicates, Integer executionNumber) {
		this.name = name;
		this.evalElement = new ArrayList<PredicateEvalElement>();
		for (String string : predicates) {
			try {
				evalElement.add(PredicateEvalElement.create(string));
			} catch (BException e) {
				throw new IllegalArgumentException(
						"Formula must be a predicate: " + string);
			}
		}
		if (name.size() != evalElement.size()) {
			throw new IllegalArgumentException(
					"Must provide the same number of names and predicates.");
		}
		int size = this.name.size();
		for (int i = 0; i < size; ++i) {
			this.executionNumber.add(executionNumber);
		}
	}

	public ConstructTraceCommand(final List<String> name,
			final List<String> predicate) {
		this(name, predicate, 1);
	}

	public ConstructTraceCommand(final List<String> name,
			final List<String> predicate, final List<Integer> executionNumber) {
		this(name, predicate);
		this.executionNumber = executionNumber;
		if (name.size() != executionNumber.size()) {
			throw new IllegalArgumentException(
					"Must provide the same number of names and execution numbers.");
		}
	}

	/**
	 * This method is called when the command is prepared for sending. The
	 * method is called by the Animator class, most likely it is not interesting
	 * for other classes.
	 * 
	 * @throws ProBException
	 * 
	 * @see de.prob.animator.command.AbstractCommand#writeCommand(de.prob.prolog.output.IPrologTermOutput)
	 */
	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("prob_construct_trace");
		pto.openList();
		for (String n : name) {
			pto.printAtom(n);
		}
		pto.closeList();
		final ASTProlog prolog = new ASTProlog(pto, null);
		pto.openList();
		for (PredicateEvalElement cb : evalElement) {
			cb.getPrologAst().apply(prolog);
		}
		pto.closeList();
		pto.openList();
		for (Integer n : executionNumber) {
			pto.printNumber(n);
		}
		pto.closeList();
		pto.printVariable(RESULT_VARIABLE_STATES);
		pto.printVariable(RESULT_VARIABLE_OPS);
		pto.printVariable(RESULT_VARIABLE_ERRORS);
		pto.closeTerm();
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public List<String> getErrors() {
		return errors;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		// errors
		ListPrologTerm list = (ListPrologTerm) bindings
				.get(RESULT_VARIABLE_ERRORS);
		for (PrologTerm prologTerm : list) {
			errors.add(prologTerm.toString());
		}

		// Change history in Animator ...
		// need to reconstruct operations and states
		final Animator animator = Animator.getAnimator();

		ListPrologTerm operations = (ListPrologTerm) bindings
				.get(RESULT_VARIABLE_OPS);
		ListPrologTerm states = (ListPrologTerm) bindings
				.get(RESULT_VARIABLE_STATES);

		for (int i = 0; i < operations.size(); i++) {
			PrologTerm operationPrologTerm = operations.get(i);
			Operation op = Operation.fromPrologTerm(operationPrologTerm);

			// state(NewStateId,StateValues,Initialised,InvKO,MaxOpsReached,Timeout,OpTimeout,StateErrors,UnsatProps,OperationsForState)
			CompoundPrologTerm statePrologTerm = (CompoundPrologTerm) states
					.get(i);

			ListPrologTerm stateValuesPrologTerm = (ListPrologTerm) statePrologTerm
					.getArgument(2);
			List<Variable> stateValues = new LinkedList<Variable>();
			for (PrologTerm prologTerm : stateValuesPrologTerm) {
				stateValues.add(new Variable((CompoundPrologTerm) prologTerm));
			}

			ListPrologTerm stateErrorsPrologTerm = (ListPrologTerm) statePrologTerm
					.getArgument(8);
			Collection<StateError> stateErrors = new LinkedList<StateError>();
			for (PrologTerm prologTerm : stateErrorsPrologTerm) {
				stateErrors
						.add(new StateError((CompoundPrologTerm) prologTerm));
			}

			Set<String> opTimeouts = new HashSet<String>(
					PrologTerm.atomicStrings((ListPrologTerm) statePrologTerm
							.getArgument(7)));

			ArrayList<Operation> enabledOperations = new ArrayList<Operation>();

			for (PrologTerm enop : (ListPrologTerm) statePrologTerm
					.getArgument(10)) {
				final CompoundPrologTerm cpt = (CompoundPrologTerm) enop;
				enabledOperations.add(Operation.fromPrologTerm(cpt));
			}

			State s = new State(statePrologTerm.getArgument(1).toString(),
					statePrologTerm.getArgument(2).toString().equals("true"),
					statePrologTerm.getArgument(3).toString().equals("true"),
					statePrologTerm.getArgument(5).toString().equals("true"),
					statePrologTerm.getArgument(4).toString().equals("true"),
					stateValues, enabledOperations, stateErrors, opTimeouts);

			Activator.computedState(s);
			animator.getHistory().add(s, op);
			animator.announceCurrentStateChanged(s, op);
		}

	}
}
