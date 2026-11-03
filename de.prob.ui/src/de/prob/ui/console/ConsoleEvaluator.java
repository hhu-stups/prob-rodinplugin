package de.prob.ui.console;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.prob.core.Animator;
import de.prob.core.ILifecycleListener;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.CbcSolveCommand;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.State;
import de.prob.core.langdep.EventBAnimatorPart;
import de.prob.core.langdep.EventBAnimatorPart.OpenFormula;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;
import de.prob.unicode.UnicodeTranslator;

/**
 * Evaluates the predicates and expressions entered in the ProB console in
 * the current state of the animator, using the same commands as the formulas
 * of the State view. Identifiers that do not belong to the model are treated
 * as existentially quantified: ProB's constraint solver searches values for
 * them.
 * 
 * Must not be called from the UI thread: it waits for ProB.
 */
public class ConsoleEvaluator implements ILifecycleListener {

	public enum Kind {
		/** a predicate that is true */
		TRUE,
		/** a predicate that is false */
		FALSE,
		/** the value of an expression */
		VALUE,
		/** not evaluated, e.g. no model loaded or not initialised */
		NOT_AVAILABLE,
		/** the solver found neither a solution nor that there is none */
		UNKNOWN,
		/** e.g. a syntax error or a well-definedness error */
		ERROR
	}

	public static final class Result {
		private final Kind kind;
		private final String text;

		public Result(final Kind kind, final String text) {
			this.kind = kind;
			this.text = text;
		}

		public Kind getKind() {
			return kind;
		}

		public String getText() {
			return text;
		}

		@Override
		public String toString() {
			return kind + ": " + text;
		}
	}

	/**
	 * The formulas sent to ProB, so that each one is inserted only once per
	 * loaded model
	 */
	private final Map<String, EvaluationElement> elements = new ConcurrentHashMap<String, EvaluationElement>();

	// set from the UI thread
	private volatile boolean showSolveTime = true;

	/** whether results of the constraint solver include its solve time */
	public void setShowSolveTime(final boolean showSolveTime) {
		this.showSolveTime = showSolveTime;
	}

	/**
	 * A new model has been loaded: the inserted formulas are no longer valid
	 */
	@Override
	public void reset() {
		elements.clear();
	}

	public Result evaluate(final String input) throws ProBException {
		final String formula = input.trim();
		final Animator animator = Animator.getAnimator();
		if (!animator.isMachineLoaded())
			return new Result(Kind.NOT_AVAILABLE, "No model is being animated.");
		final State state = animator.getCurrentState();
		if (state == null)
			return new Result(Kind.NOT_AVAILABLE, "There is no current state.");

		EvaluationElement element = elements.get(formula);
		if (element == null) {
			final LanguageDependendAnimationPart ldp = animator
					.getLanguageDependendPart();
			if (ldp == null)
				return new Result(Kind.NOT_AVAILABLE,
						"Formulas cannot be parsed for this model.");
			if (ldp instanceof EventBAnimatorPart) {
				final OpenFormula open;
				try {
					open = ((EventBAnimatorPart) ldp).parseOpenFormula(formula);
				} catch (ProBParseException e) {
					return new Result(Kind.ERROR, e.getMessage());
				}
				if (!open.getFreeIdentifiers().isEmpty())
					return solve(animator, state, open);
			}
			final PrologTerm parsed;
			try {
				parsed = parse(new ProBParserBaseAdapter(ldp), formula);
			} catch (ProBParseException e) {
				return new Result(Kind.ERROR, e.getMessage());
			}
			element = EvaluationInsertFormulaCommand.insertFormula(parsed);
			elements.put(formula, element);
		}

		final EvaluationGetValuesCommand cmd = new EvaluationGetValuesCommand(
				state.getId(), Collections.singleton(element));
		animator.execute(cmd);
		return toResult(cmd.getResult().get(element));
	}

	/**
	 * Like "Add new Formula" in the State view: a predicate if possible,
	 * otherwise an expression
	 */
	private static PrologTerm parse(final ProBParserBaseAdapter parser,
			final String formula) throws ProBParseException {
		try {
			return parser.parsePredicate(formula, false);
		} catch (ProBParseException predicateError) {
			try {
				return parser.parseExpression(formula, false);
			} catch (ProBParseException expressionError) {
				// expressions are entered more often than predicates, so the
				// message for the expression is usually the relevant one
				throw expressionError;
			}
		}
	}

	/**
	 * Lets the constraint solver find values for the free identifiers, the
	 * identifiers of the model have their values in the current state
	 */
	private Result solve(final Animator animator, final State state,
			final OpenFormula formula) throws ProBException {
		final CbcSolveCommand cmd = CbcSolveCommand.solve(animator,
				state.getId(), formula.getSolverPredicate());
		switch (cmd.getOutcome()) {
		case SOLUTION:
			final Map<String, String> solution = cmd.getSolution();
			final StringBuilder sb = new StringBuilder();
			if (formula.isPredicate()) {
				sb.append("TRUE");
			} else {
				sb.append(valueOf(solution, formula.getResultIdentifier()));
			}
			// predicate: one identifier per line; expression: the values the
			// result was computed for. The solution also contains the
			// identifiers of the model, they are not shown.
			boolean first = true;
			for (String id : formula.getFreeIdentifiers()) {
				if (formula.isPredicate())
					sb.append("\n  ");
				else
					sb.append(first ? "\n  for " : ", ");
				first = false;
				sb.append(id).append(" = ").append(valueOf(solution, id));
			}
			sb.append(time(cmd));
			return new Result(formula.isPredicate() ? Kind.TRUE : Kind.VALUE,
					sb.toString());
		case NO_SOLUTION:
			return formula.isPredicate() ? new Result(Kind.FALSE,
					"FALSE (no solution exists)" + time(cmd)) : new Result(
					Kind.ERROR, "The expression has no value." + time(cmd));
		case UNKNOWN:
			return new Result(Kind.UNKNOWN, "UNKNOWN: no solution found"
					+ reason(cmd) + time(cmd));
		default:
			return new Result(Kind.ERROR, "ProB could not solve the formula"
					+ reason(cmd) + time(cmd));
		}
	}

	private String time(final CbcSolveCommand cmd) {
		if (!showSolveTime)
			return "";
		return "\n  (solved in " + cmd.getMilliseconds() + " ms)";
	}

	private static String valueOf(final Map<String, String> solution,
			final String id) {
		final String value = solution.get(id);
		return value == null ? "?" : UnicodeTranslator.toUnicode(value);
	}

	private static String reason(final CbcSolveCommand cmd) {
		return cmd.getReason() == null ? "." : " (" + cmd.getReason() + ").";
	}

	private static Result toResult(final EvaluationResult result) {
		if (result == null)
			return new Result(Kind.ERROR, "ProB returned no value.");
		if (!result.isActive())
			return new Result(Kind.NOT_AVAILABLE,
					"Not available in the current state (e.g. the machine is not initialised yet).");
		if (result.hasError())
			return new Result(Kind.ERROR, result.getText());
		if (result.isPredicate())
			return new Result(result.isPredicateTrue() ? Kind.TRUE : Kind.FALSE,
					result.getText());
		return new Result(Kind.VALUE, result.getText());
	}

}
