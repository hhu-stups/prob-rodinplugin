/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.CompoundPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * @ Andriy: Das ist jetzt deine Baustelle :)
 */
public final class LtlCheckingCommand implements IComposableCommand {

	private static final String VARIABLE_NAME_ATOMICS = "A";
	private static final String VARIABLE_NAME_STRUCTURE = "S";
	private static final String VARIABLE_NAME_RESULT = "R";

	public static enum StartMode {
		init, starthere, checkhere
	};

	public static enum Status {
		incomplete(false), ok(true), counterexample(true), nostart(true), typeerror(
				true);
		private final boolean abort;

		private Status(final boolean abort) {
			this.abort = abort;
		}

		public boolean isAbort() {
			return abort;
		}
	}

	public static enum PathType {
		INFINITE, FINITE, REDUCED
	};

	public static class Result {
		private final Status status;
		private final ListPrologTerm atomics;
		private final PrologTerm structure;
		private final ListPrologTerm counterexample;
		private final PathType pathType;
		private final int loopEntry;
		private final Operation[] initPathOps;

		public Result(final Status status, final ListPrologTerm atomics,
				final PrologTerm structure,
				final ListPrologTerm counterexample, final PathType pathType,
				final int loopEntry, final Operation[] initPathOps) {
			this.status = status;
			this.atomics = atomics;
			this.structure = structure;
			this.counterexample = counterexample;
			this.pathType = pathType;
			this.loopEntry = loopEntry;
			this.initPathOps = initPathOps;
		}

		/**
		 * @return the basic outcome of the model-checking, never
		 *         <code>null</code>
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * @return if the model-checking is finished after the call.
		 */
		public boolean isAbort() {
			return status.isAbort();
		}

		/**
		 * Returns list of pretty-printed sub-formulas, each of the form
		 * ap(Text) or tp(Text).
		 * 
		 * E.g. for G({x=0} U [start]), the list contains ap('{x=0}') and
		 * tp('[start]').
		 * 
		 * @return A list of atomic formulas, never <code>null</code>
		 */
		public ListPrologTerm getAtomics() {
			return atomics;
		}

		/**
		 * A tree structure representing the formula. Atomic propositions or
		 * transition propositions are represented by integers, referring to the
		 * index of the corresponding formula in {@link #atomics}
		 * 
		 * Lets assume that {@link #getAtomics()} returns the list
		 * [ap('{x=0}'),ap('{y=1}'),tp('[start]')] for the LTL formula
		 * 
		 * G({y=1} => ({x=0} U [start])).
		 * 
		 * The corresponding structure would be: globally(implies(1,until(0,2)))
		 * 
		 * @return the structure, <code>null</code> in case of a typecheck-error
		 */
		public PrologTerm getStructure() {
			return structure;
		}

		/**
		 * Returns the list of atoms of the counter-example. Each atom is a term
		 * of the form
		 * atom(StateId,EvalList,NextOperationId,NextOperationString) where
		 * StateId is the ID of the corresponding state and NextOperationId is
		 * the ID of the operation that leads to the next atom.
		 * NextOperationString is a pretty-printed string of that operation.
		 * EvalList is a list of 0s and 1s, it has the same length of the list
		 * returned by {@link #getAtomics()}. Each number represents the
		 * evaluation of the corresponding atomic formula (in
		 * {@link #getAtomics()}) in the current atom. 0=false and 1=true.
		 * 
		 * @return A list of atoms, <code>null</code> if no counter-example is
		 *         found.
		 */
		public ListPrologTerm getCounterexample() {
			return counterexample;
		}

		/**
		 * @return the path type, <code>null</code> if there is no
		 *         counter-example
		 */
		public PathType getPathType() {
			return pathType;
		}

		/**
		 * This value is only defined if {@link #getPathType()} returns LOOP.
		 * 
		 * @return the index in the counter-example where the loop of the
		 *         lasso-form starts, -1 if there is no counterexample or the
		 *         example is not in lasso form.
		 */
		public int getLoopEntry() {
			return loopEntry;
		}

		public Operation[] getInitPathOps() {
			return initPathOps;
		}
	}

	private final PrologTerm ltlFormula;
	private final int max;
	private final StartMode mode;
	private Result result;

	public LtlCheckingCommand(final PrologTerm ltlFormula, final int max,
			final StartMode mode) {
		this.ltlFormula = ltlFormula;
		this.max = max;
		this.mode = mode;
	}

	public static Result modelCheck(final Animator a, final PrologTerm fomula,
			final int max, final StartMode mode) throws ProBException {
		LtlCheckingCommand command = new LtlCheckingCommand(fomula, max, mode);
		a.execute(command);
		return command.getResult();
	}

	private Result getResult() {
		return result;
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		CompoundPrologTerm term = (CompoundPrologTerm) bindings
				.get(VARIABLE_NAME_RESULT);

		final Status status = Enum.valueOf(Status.class, term.getFunctor());

		final ListPrologTerm counterexample;
		final PathType pathType;
		final int loopEntry;
		final Operation[] initPath;
		if (term.hasFunctor("counterexample", 3)) {
			counterexample = (ListPrologTerm) term.getArgument(1);
			CompoundPrologTerm loopStatus = (CompoundPrologTerm) term
					.getArgument(2);
			if (loopStatus.hasFunctor("no_loop", 0)) {
				pathType = PathType.REDUCED;
				loopEntry = -1;
			} else if (loopStatus.hasFunctor("deadlock", 0)) {
				pathType = PathType.FINITE;
				loopEntry = -1;
			} else if (loopStatus.hasFunctor("loop", 1)) {
				pathType = PathType.INFINITE;
				loopEntry = ((AIntegerPrologTerm) loopStatus.getArgument(1)).intValueExact();
			} else
				throw new CommandException(
						"LTL model check returned unexpected loop status: "
								+ loopStatus);
			final ListPrologTerm operationIds = (ListPrologTerm) term
					.getArgument(3);
			initPath = new Operation[operationIds.size()];
			int i = 0;
			for (final PrologTerm opTerm : operationIds) {
				initPath[i] = Operation.fromPrologTerm(opTerm);
				i++;
			}
		} else {
			counterexample = null;
			pathType = null;
			loopEntry = -1;
			initPath = null;
		}

		final ListPrologTerm atomics = (ListPrologTerm) bindings
				.get(VARIABLE_NAME_ATOMICS);
		final PrologTerm structure = bindings.get(VARIABLE_NAME_STRUCTURE);
		final boolean noStructure = (structure instanceof ListPrologTerm)
				&& ((ListPrologTerm) structure).isEmpty();

		result = new Result(status, atomics, noStructure ? null : structure,
				counterexample, pathType, loopEntry, initPath);
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("do_ltl_modelcheck");
		ltlFormula.toTermOutput(pto);
		pto.printNumber(max);
		pto.printAtom(mode.toString());
		pto.printVariable(VARIABLE_NAME_ATOMICS);
		pto.printVariable(VARIABLE_NAME_STRUCTURE);
		pto.printVariable(VARIABLE_NAME_RESULT);
		pto.closeTerm();
	}
}
