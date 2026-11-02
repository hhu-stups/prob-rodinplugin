package de.prob.ui.console;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import de.prob.core.Animator;
import de.prob.core.ILifecycleListener;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.command.EvaluationGetValuesCommand;
import de.prob.core.command.EvaluationGetValuesCommand.EvaluationResult;
import de.prob.core.command.EvaluationInsertFormulaCommand;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;

/**
 * Evaluates the predicates and expressions entered in the ProB console in
 * the current state of the animator, using the same commands as the formulas
 * of the State view. Free identifiers are not supported yet.
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
				// the message for the predicate is usually more helpful
				throw predicateError;
			}
		}
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
