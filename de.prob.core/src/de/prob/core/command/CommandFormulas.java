package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.parserbase.ProBParseException;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * Parses the formulas for ProB's table and graph commands (see
 * {@link GetCommandsCommand}). They are passed as raw ASTs, which ProB accepts
 * instead of the text, so that Rodin's parser and type checker are used.
 */
public final class CommandFormulas {

	private CommandFormulas() {
	}

	/**
	 * Parses an expression of the model.
	 *
	 * @throws ProBParseException
	 *             if the expression has syntax or type errors
	 */
	public static PrologTerm parseExpression(final Animator animator,
			final String expression) throws ProBParseException {
		final StructuredPrologOutput out = new StructuredPrologOutput();
		animator.getLanguageDependendPart().parseExpression(out, expression,
				false);
		out.fullstop();
		return out.getSentences().get(0);
	}

	/**
	 * Parses an expression or a predicate of the model, e.g. for a formula
	 * tree.
	 *
	 * @throws ProBParseException
	 *             if it is neither (with the error of the expression)
	 */
	public static PrologTerm parseFormula(final Animator animator,
			final String formula) throws ProBParseException {
		try {
			return parseExpression(animator, formula);
		} catch (ProBParseException expressionError) {
			final LanguageDependendAnimationPart ldp = animator
					.getLanguageDependendPart();
			final StructuredPrologOutput out = new StructuredPrologOutput();
			try {
				ldp.parsePredicate(out, formula, false);
			} catch (ProBParseException predicateError) {
				throw expressionError;
			}
			out.fullstop();
			return out.getSentences().get(0);
		}
	}
}
