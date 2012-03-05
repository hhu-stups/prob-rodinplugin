package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public final class CounterExampleUnaryFigure extends
		CounterExamplePropositionFigure implements MouseMotionListener {
	private Panel panel;

	protected final Hashtable<Ellipse, Integer> argumentEllipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> argumentEllipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExampleUnaryFigure(final CounterExampleProposition model) {
		super(model);

		bounds = new Rectangle(size, size, size
				* (model.getValues().size() * 2 + 1), (int) (5.0 / 2 * size));
	}

	@Override
	protected void drawProposition(final CounterExamplePropositionFigure parent) {
		final CounterExampleFigure counterExampleFigure = (CounterExampleFigure) getParent();
		final Insets insets = getInsets();

		if (parent != null) {
			Rectangle parentBounds = parent.getBounds();
			bounds.x = parentBounds.x - insets.left;
			bounds.y = parentBounds.y + parentBounds.height + size / 2;

		} else {
			final List<RectangleFigure> states = counterExampleFigure
					.getStates();

			for (int i = 0; i < states.size(); i++) {
				final RectangleFigure state = states.get(i);

				if (!counterExampleFigure.getChildren().contains(state)) {
					state.setBounds(new Rectangle((size * 2) * (i + 1)
							+ insets.left, size, size, size + 6 * insets.top));
					counterExampleFigure.add(state, 0);
				}
			}
		}

		counterExampleFigure.setConstraint(this, new Rectangle(bounds));

		removeAll();

		final int stateId = model.getStateId();

		final CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();
		final List<Integer> positions = ((CounterExampleUnaryOperator) model)
				.getHighlightedPositions().get(stateId);
		final Rectangle panelBounds = new Rectangle(bounds.x
				+ (int) (2.0 / 5 * size), bounds.y + (int) (2.0 / 5 * size),
				bounds.width, bounds.height);

		panel = drawPropositionFigure(parent, bounds, argument, positions,
				argumentEllipses1, argumentEllipses2, panelBounds, stateId,
				size);
	}

	@Override
	public void mouseDoubleClicked(final MouseEvent me) {
		super.mouseDoubleClicked(me);

		int stateId = argumentEllipses1.get(me.getSource());
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

	@Override
	public void mousePressed(final MouseEvent me) {
		if (me.button != 1)
			return;

		super.mousePressed(me);

		final int stateId = argumentEllipses1.get(me.getSource());

		final CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();

		List<CounterExampleProposition> children = argument.getChildren();
		children = children.subList(1, children.size());
		setTrasparent(children);

		final CounterExamplePropositionFigure argumentFigure = getFigure(argument);

		for (Connection connection : argumentFigure.getConnections().values()) {
			connection.setVisible(false);
		}

		final TitleBarBorder border = (TitleBarBorder) panel.getBorder();

		if (argument.getStateId() == stateId) {
			boolean visible = !argument.isVisible();

			if (argument.hasChildren()) {
				border.setFont(visible ? boldFont : normalFont);
			}

			argument.setVisible(visible);
		} else {
			if (argument.hasChildren()) {
				border.setFont(boldFont);
			}

			argument.setStateId(stateId);
			argument.setVisible(true);
		}

		repaint();
	}

	@Override
	public void mouseEntered(final MouseEvent me) {
		final Ellipse source = (Ellipse) me.getSource();

		final CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();

		if (!argument.hasChildren())
			return;

		final boolean painted = argument.isVisible();
		final int argumentStateId = argument.getStateId();
		final int stateId = argumentEllipses1.get(source);

		final Label label = new Label();
		label.setForegroundColor(foregroundColor);

		String text = "open ";

		if (stateId == argumentStateId)
			text = painted ? "close " : "open ";

		label.setText("Click to " + text + argument);
		source.setToolTip(label);
	}
}
