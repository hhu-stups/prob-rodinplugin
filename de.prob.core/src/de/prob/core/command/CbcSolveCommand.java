package de.prob.core.command;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.AIntegerPrologTerm;
import de.prob.prolog.term.ListPrologTerm;
import de.prob.prolog.term.PrologTerm;

/**
 * Searches values for the free identifiers of a predicate with ProB's
 * constraint solver, in a state of the animation: the identifiers of the
 * model have their values in that state, all others are treated as
 * existentially quantified. Calls
 * <code>cbc_timed_solve_with_opts(Solver,Options,Predicate,Identifiers,Result,Milliseconds)</code>.
 */
public final class CbcSolveCommand implements IComposableCommand {

	public enum Outcome {
		/** values for the free identifiers were found */
		SOLUTION,
		/** the predicate has no solution */
		NO_SOLUTION,
		/** ProB could neither find a solution nor prove there is none */
		UNKNOWN,
		/** ProB reported an error */
		ERROR
	}

	private static final String SOLVER = "PROB";
	// the pretty-printed values are truncated after this number of characters
	private static final int TRUNCATE = 50000;

	private static final String IDENTIFIERS = "Identifiers";
	private static final String RESULT = "Result";
	private static final String MILLISECONDS = "Milliseconds";

	private final String stateId;
	private final PrologTerm predicate;

	private Outcome outcome;
	private String reason;
	private Map<String, String> solution = Collections.emptyMap();
	private long milliseconds;

	/**
	 * @param stateId
	 *            the state in which the identifiers of the model are evaluated
	 * @param predicate
	 *            the untyped predicate, as written by the parser (without the
	 *            <code>bpred</code> wrapper)
	 */
	public CbcSolveCommand(final String stateId, final PrologTerm predicate) {
		this.stateId = stateId;
		this.predicate = predicate;
	}

	public static CbcSolveCommand solve(final Animator animator,
			final String stateId, final PrologTerm predicate)
			throws ProBException {
		final CbcSolveCommand command = new CbcSolveCommand(stateId, predicate);
		animator.execute(command);
		return command;
	}

	@Override
	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("cbc_timed_solve_with_opts");
		pto.printAtom(SOLVER);
		pto.openList();
		pto.openTerm("solve_in_visited_state");
		pto.printAtomOrNumber(stateId);
		pto.closeTerm();
		pto.openTerm("truncate");
		pto.printNumber(TRUNCATE);
		pto.closeTerm();
		// expand symbolic values, e.g. BOOL --> BOOL as a set of functions
		pto.printAtom("force_evaluation");
		pto.closeList();
		predicate.toTermOutput(pto);
		pto.printVariable(IDENTIFIERS);
		pto.printVariable(RESULT);
		pto.printVariable(MILLISECONDS);
		pto.closeTerm();
	}

	@Override
	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		final PrologTerm result = bindings.get(RESULT);
		final PrologTerm time = bindings.get(MILLISECONDS);
		if (time instanceof AIntegerPrologTerm)
			milliseconds = ((AIntegerPrologTerm) time).longValue();

		if (result.hasFunctor("solution", 1)) {
			outcome = Outcome.SOLUTION;
			final Map<String, String> values = new LinkedHashMap<String, String>();
			for (PrologTerm binding : (ListPrologTerm) result.getArgument(1)) {
				// binding(Id,Value,PrettyPrintedValue)
				values.put(PrologTerm.atomicString(binding.getArgument(1)),
						PrologTerm.atomicString(binding.getArgument(3)));
			}
			solution = Collections.unmodifiableMap(values);
		} else if (result.hasFunctor("contradiction_found", 0)) {
			outcome = Outcome.NO_SOLUTION;
		} else if (result.hasFunctor("time_out", 0)) {
			outcome = Outcome.UNKNOWN;
			reason = "time-out";
		} else if (result.hasFunctor("interrupted", 0)) {
			outcome = Outcome.UNKNOWN;
			reason = "interrupted";
		} else if (result.hasFunctor("no_solution_found", 1)) {
			outcome = Outcome.UNKNOWN;
			reason = describeReason(result.getArgument(1));
		} else if (result.hasFunctor("error", 0)) {
			outcome = Outcome.ERROR;
		} else {
			outcome = Outcome.ERROR;
			reason = "unexpected result " + result;
		}
	}

	private static String describeReason(final PrologTerm reason) {
		if (reason.hasFunctor("clpfd_overflow", 0))
			return "CLPFD integer overflow";
		if (reason.hasFunctor("unfixed_deferred_sets", 0))
			return "unfixed deferred sets";
		return reason.isAtom() ? PrologTerm.atomicString(reason) : reason
				.toString();
	}

	public Outcome getOutcome() {
		return outcome;
	}

	/**
	 * @return why the outcome is {@link Outcome#UNKNOWN} or
	 *         {@link Outcome#ERROR}, may be <code>null</code>
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @return the pretty-printed values of the identifiers of the predicate
	 *         (also those of the model), empty unless the outcome is
	 *         {@link Outcome#SOLUTION}
	 */
	public Map<String, String> getSolution() {
		return solution;
	}

	public long getMilliseconds() {
		return milliseconds;
	}
}
