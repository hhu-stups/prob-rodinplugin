package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public final class CounterExamplePredicateFigure extends
		CounterExamplePropositionFigure {
	protected final Hashtable<Ellipse, Integer> ellipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> ellipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExamplePredicateFigure(final CounterExampleProposition model) {
		super(model);

	}

	@Override
	protected void drawProposition(final CounterExamplePropositionFigure parent) {
		if (parent == null) {
			bounds = new Rectangle(size, size, size
					* (model.getValues().size() * 2 + 1), 2 * size);

			final CounterExampleFigure counterExampleFigure = (CounterExampleFigure) getParent();
			final Insets insets = getInsets();
			final List<RectangleFigure> states = counterExampleFigure
					.getStates();

			for (int i = 0; i < states.size(); i++) {
				final RectangleFigure state = states.get(i);

				if (!counterExampleFigure.getChildren().contains(state)) {
					state.setBounds(new Rectangle((size * 2) * (i + 1)
							+ insets.left, size, size, 2 * size + 2
							* insets.top));
					counterExampleFigure.add(state, 0);
				}
			}

			getParent().setConstraint(this, new Rectangle(bounds));

			removeAll();

			drawPropositionFigure(parent, bounds, null, null, ellipses1,
					ellipses2, null, model.getStateId(), 3 * size / 5);
		}
	}

	@Override
	public void mouseDoubleClicked(final MouseEvent me) {
		super.mouseDoubleClicked(me);

		int stateId = ellipses1.get(me.getSource());
		Logger.assertProB("stateId >= 0", stateId >= 0);

		final CounterExample ce = ((CounterExampleFigure) getParent())
				.getModel();

		if (ce != null) {
			stateId += ce.getInitPath().size();

			final Animator animator = Animator.getAnimator();
			final History history = animator.getHistory();

			try {
				CounterExampleHistoryHandler.showCounterExampleInAnimator();
				history.gotoPos(stateId);
			} catch (ExecutionException e) {
				Logger.notifyUser("Internal Error. Please submit a bugreport",
						e);
			} catch (ProBException e) {
				Logger.notifyUser("Internal Error. Please submit a bugreport",
						e);
			}
		}
	}
}
