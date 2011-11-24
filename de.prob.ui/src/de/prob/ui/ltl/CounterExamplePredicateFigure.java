package de.prob.ui.ltl;

import java.util.Hashtable;

import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public final class CounterExamplePredicateFigure extends
		CounterExamplePropositionFigure {
	protected final Hashtable<Ellipse, Integer> ellipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> ellipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExamplePredicateFigure(CounterExampleProposition model) {
		super(model);
		setToolTip(new Label(model.toString()));
	}

	@Override
	protected void drawProposition(final CounterExamplePropositionFigure parent) {
		if (parent != null)
			return;

		removeAll();
		Rectangle bounds = new Rectangle(height, height, height
				* (model.getValues().size() * 2 + 1), 2 * height);

		getParent().setConstraint(this, bounds);
		setBounds(bounds);

		int stateId = model.getStateId();

		drawPropositionFigure(parent, bounds, null, null, ellipses1, ellipses2,
				null, stateId, 3 * height / 5);
	}

	@Override
	public void mousePressed(MouseEvent me) {
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}
}
