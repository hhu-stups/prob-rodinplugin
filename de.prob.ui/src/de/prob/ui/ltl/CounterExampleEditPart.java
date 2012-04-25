package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public final class CounterExampleEditPart extends AbstractGraphicalEditPart {
	@Override
	protected IFigure createFigure() {
		final CounterExample model = (CounterExample) getModel();
		return new CounterExampleFigure(model);
	}

	@Override
	public List<CounterExampleProposition> getModelChildren() {
		final CounterExample model = (CounterExample) getModel();
		final List<CounterExampleProposition> children = model
				.getPropositions();

		return children;
	}

	@Override
	protected void refreshVisuals() {
		final CounterExampleFigure figure = (CounterExampleFigure) getFigure();
		figure.update();
	}

	@Override
	protected void createEditPolicies() {
	}
}
