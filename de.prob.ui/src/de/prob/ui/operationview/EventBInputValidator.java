package de.prob.ui.operationview;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eventb.core.ast.*;

class EventBInputValidator implements IInputValidator {
	@Override
	public String isValid(String newText) {
		IParseResult result = FormulaFactory.getDefault().parsePredicate(
				newText);
		if (result.hasProblem()) {
			return "No Event-B Predicate";
		}
		// TODO Auto-generated method stub
		return null;
	}
}