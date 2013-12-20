package de.prob.ui.operationview;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.ast.*;

import de.prob.unicode.UnicodeTranslator;

class EventBInputValidator implements IInputValidator {
	@Override
	public String isValid(String newText) {
		String formula = newText.equals("") ? newText : UnicodeTranslator
				.toUnicode(newText);

		IParseResult result = FormulaFactory.getDefault().parsePredicate(
				formula);
		if (result.hasProblem()) {
			return "No Event-B Predicate";
		}
		// TODO Auto-generated method stub
		return null;
	}
}