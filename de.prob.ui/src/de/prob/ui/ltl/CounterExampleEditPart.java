package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public final class CounterExampleEditPart extends AbstractGraphicalEditPart {
	@Override
	protected IFigure createFigure() {
		CounterExample model = (CounterExample) getModel();
		return new CounterExampleFigure(model);
	}

	@Override
	public List<CounterExampleProposition> getModelChildren() {
		CounterExample model = (CounterExample) getModel();
		List<CounterExampleProposition> children = model.getPropositions();

		return children;
	}

	@Override
	protected void refreshVisuals() {
		CounterExampleFigure figure = (CounterExampleFigure) getFigure();
		figure.update();
	}

	@Override
	protected void createEditPolicies() {
	}
}
