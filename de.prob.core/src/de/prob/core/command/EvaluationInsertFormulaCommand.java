/**
 * 
 */
package de.prob.core.command;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.domainobjects.EvaluationElement;
import de.prob.exceptions.ProBException;
import de.prob.parser.ISimplifiedROMap;
import de.prob.parserbase.ProBParseException;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.term.PrologTerm;

/**
 * This commands registers a formula (given as an already parsed
 * {@link PrologTerm}) in the Prolog core and returns an
 * {@link EvaluationElement} that can be used to evaluate the formula and
 * retrieve its subformulas.
 * 
 * @author plagge
 */
public class EvaluationInsertFormulaCommand implements IComposableCommand {

	public static enum FormulaType {
		PREDICATE, EXPRESSION
	};

	private static final String VARNAME_ID = "ID";
	private final PrologTerm rawExpression;
	private PrologTerm id;

	public static EvaluationElement insertFormula(final PrologTerm formula)
			throws ProBException {
		final EvaluationInsertFormulaCommand cmd = new EvaluationInsertFormulaCommand(
				formula);
		final Animator animator = Animator.getAnimator();
		animator.execute(cmd);
		return new EvaluationElement(animator, cmd.getId(), null);
	}

	public static EvaluationElement insertPredicate(final String formula)
			throws ProBException, UnsupportedOperationException,
			ProBParseException {
		return insertFormula(getParser().parsePredicate(formula, false));
	}

	public static EvaluationElement insertExpression(final String formula)
			throws ProBException, UnsupportedOperationException,
			ProBParseException {
		return insertFormula(getParser().parseExpression(formula, false));
	}

	public static EvaluationElement insertFormula(final Animator animator,
			final FormulaType type, final String formula) throws ProBException,
			UnsupportedOperationException, ProBParseException {
		final ProBParserBaseAdapter parser = getParser(animator);
		final PrologTerm parsed;
		switch (type) {
		case EXPRESSION:
			parsed = parser.parseExpression(formula, false);
			break;
		case PREDICATE:
			parsed = parser.parsePredicate(formula, false);
			break;
		default:
			throw new IllegalArgumentException("Unsupported formula type: "
					+ type);
		}
		return insertFormula(parsed);
	}

	private static ProBParserBaseAdapter getParser() {
		return getParser(Animator.getAnimator());
	}

	private static ProBParserBaseAdapter getParser(final Animator animator) {
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		if (ldp == null) {
			throw new UnsupportedOperationException(
					"The current formalism does not allow parsing of formulas");
		} else {
			return new ProBParserBaseAdapter(ldp);
		}
	}

	public EvaluationInsertFormulaCommand(final PrologTerm rawExpression) {
		this.rawExpression = rawExpression;
	}

	public void processResult(
			final ISimplifiedROMap<String, PrologTerm> bindings)
			throws CommandException {
		id = bindings.get(VARNAME_ID);
	}

	public void writeCommand(final IPrologTermOutput pto) {
		pto.openTerm("evaluation_insert_formula");
		rawExpression.toTermOutput(pto);
		pto.printAtom("user");
		pto.printVariable(VARNAME_ID);
		pto.closeTerm();
	}

	public PrologTerm getId() {
		return id;
	}
}
