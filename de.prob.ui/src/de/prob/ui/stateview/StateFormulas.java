/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.stateview;

import org.eclipse.core.expressions.PropertyTester;

import de.prob.core.command.EvaluationFormulaTextCommand;
import de.prob.ui.stateview.statetree.StateTreeExpression;
import de.prob.ui.stateview.statetree.StateTreeVariable;

/**
 * The formulas of the entries of the State view: a variable, or the formula of
 * an entry of the formula tree (e.g. an invariant or the guard of an event),
 * but not a section such as "invariants".
 * 
 * Also a property tester (namespace de.prob.ui.stateview, property isFormula)
 * for the commands that need a formula, e.g. the projection of the state
 * space.
 */
public final class StateFormulas extends PropertyTester {

	/**
	 * @return the entry's formula as text, which can be parsed, or null if it
	 *         has none
	 */
	public static String formulaOf(final Object element) {
		if (element instanceof StateTreeVariable)
			return ((StateTreeVariable) element).getLabel();
		if (!(element instanceof StateTreeExpression))
			return null;
		final StateTreeExpression expression = (StateTreeExpression) element;
		final EvaluationFormulaTextCommand formula = expression
				.getStaticElement().getFormula();
		if (formula == null)
			// an older probcli: the label, as before
			return expression.getLabel();
		if (formula.getKind() == EvaluationFormulaTextCommand.Kind.NONE
				|| formula.getText().isEmpty())
			return null;
		return formula.getText();
	}

	@Override
	public boolean test(final Object receiver, final String property,
			final Object[] args, final Object expectedValue) {
		if ("isFormula".equals(property))
			return formulaOf(receiver) != null;
		return false;
	}
}
