package de.prob.ui.ltl;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExamplePredicate;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

public final class CounterExampleEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart editPart = null;

		if (model instanceof CounterExample)
			editPart = new CounterExampleEditPart();
		else if (model instanceof CounterExamplePredicate)
			editPart = new CounterExamplePredicateEditPart();
		else if (model instanceof CounterExampleUnaryOperator)
			editPart = new CounterExampleUnaryEditPart();
		else if (model instanceof CounterExampleBinaryOperator)
			editPart = new CounterExampleBinaryEditPart();

		if (editPart != null)
			editPart.setModel(model);

		return editPart;
	}
}
