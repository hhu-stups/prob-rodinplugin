package de.prob.ui.ltl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public abstract class CounterExamplePropositionEditPart extends
		AbstractGraphicalEditPart implements PropertyChangeListener {
	@Override
	public void activate() {
		if (!isActive()) {
			CounterExampleProposition model = (CounterExampleProposition) getModel();
			model.addPropertyChangeListener(this);
		}

		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			CounterExampleProposition model = (CounterExampleProposition) getModel();
			model.removePropertyChangeListener(this);
		}

		super.deactivate();
	}

	@Override
	protected void refreshVisuals() {
		CounterExamplePropositionFigure figure = (CounterExamplePropositionFigure) getFigure();
		figure.update();
	}

	@Override
	protected void createEditPolicies() {
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		refresh();
	}
}
