package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ltl.handler.CounterExampleHistoryHandler;

public final class CounterExampleBinaryFigure extends
		CounterExamplePropositionFigure {
	private Panel firstPanel;
	private Panel secondPanel;

	protected final Hashtable<Ellipse, Integer> firstArgumentEllipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> firstArgumentEllipses2 = new Hashtable<Integer, Ellipse>();
	private final Hashtable<Ellipse, Integer> secondArgumentEllipses1 = new Hashtable<Ellipse, Integer>();
	private final Hashtable<Integer, Ellipse> secondArgumentEllipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExampleBinaryFigure(final CounterExampleProposition model) {
		super(model);

		bounds = new Rectangle(size, size, size
				* (model.getValues().size() * 2 + 1), (int) (9.0 / 2 * size));
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
							+ insets.left, size, size, size * 3 + 6
							* insets.top));
					counterExampleFigure.add(state, 0);
				}
			}
		}

		counterExampleFigure.setConstraint(this, new Rectangle(bounds));

		removeAll();

		final int stateId = model.getStateId();

		final CounterExampleProposition firstArgument = ((CounterExampleBinaryOperator) model)
				.getFirstArgument();
		final List<Integer> firstPositions = ((CounterExampleBinaryOperator) model)
				.getFirstHighlightedPositions().get(stateId);
		final Rectangle firstPanelBounds = new Rectangle(bounds.x
				+ (int) (2.0 / 5 * size), bounds.y + (int) (2.0 / 5 * size),
				bounds.width, bounds.height / 2);
		firstPanel = drawPropositionFigure(parent, bounds, firstArgument,
				firstPositions, firstArgumentEllipses1, firstArgumentEllipses2,
				firstPanelBounds, stateId, size);

		final Rectangle secondPanelBounds = new Rectangle(bounds.x + 20,
				bounds.y + (int) (12.0 / 5 * size), bounds.width,
				bounds.height / 2);
		final CounterExampleProposition secondArgument = ((CounterExampleBinaryOperator) model)
				.getSecondArgument();
		final List<Integer> secondPositions = ((CounterExampleBinaryOperator) model)
				.getSecondHighlightedPositions().get(stateId);
		secondPanel = drawPropositionFigure(parent, bounds, secondArgument,
				secondPositions, secondArgumentEllipses1,
				secondArgumentEllipses2, secondPanelBounds, stateId, 3 * size);
	}

	@Override
	public void mouseDoubleClicked(final MouseEvent me) {
		super.mouseDoubleClicked(me);

		int stateId = -1;

		if (firstArgumentEllipses1.containsKey(me.getSource()))
			stateId = firstArgumentEllipses1.get(me.getSource());
		else if (secondArgumentEllipses1.containsKey(me.getSource()))
			stateId = secondArgumentEllipses1.get(me.getSource());

		Logger.assertProB("stateId >= 0", stateId >= 0);

		final CounterExample ce = ((CounterExampleFigure) getParent())
				.getModel();

		if (ce != null) {
			stateId += ce.getInitPath().size();

			final History history = Animator.getAnimator().getHistory();

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
		super.mousePressed(me);
		int stateId;

		final CounterExampleProposition firstArgument = ((CounterExampleBinaryOperator) model)
				.getFirstArgument();
		List<CounterExampleProposition> firstChildren = firstArgument
				.getChildren();
		firstChildren = firstChildren.subList(1, firstChildren.size());
		setTrasparent(firstChildren);

		final CounterExamplePropositionFigure firstArgumentFigure = getFigure(firstArgument);

		for (Connection connection : firstArgumentFigure.getConnections()
				.values()) {
			connection.setVisible(false);
		}

		final CounterExampleProposition secondArgument = ((CounterExampleBinaryOperator) model)
				.getSecondArgument();
		List<CounterExampleProposition> secondChildren = secondArgument
				.getChildren();
		secondChildren = secondChildren.subList(1, secondChildren.size());
		setTrasparent(secondChildren);

		final CounterExamplePropositionFigure secondArgumentFigure = getFigure(secondArgument);

		for (Connection connection : secondArgumentFigure.getConnections()
				.values()) {
			connection.setVisible(false);
		}

		final Object source = me.getSource();

		if (firstPanel == null || secondPanel == null)
			return;

		final TitleBarBorder firstBorder = (TitleBarBorder) firstPanel
				.getBorder();
		final TitleBarBorder secondBorder = (TitleBarBorder) secondPanel
				.getBorder();

		// final CounterExampleFigure counterExampleFigure =
		// (CounterExampleFigure) getParent();

		if (firstArgumentEllipses1.containsKey(source)) {
			secondBorder.setFont(normalFont);
			secondArgument.setVisible(false);

			stateId = firstArgumentEllipses1.get(source);

			// Rectangle argumentBounds = firstArgumentFigure.getBounds();
			// Insets insets = getInsets();

			if (firstArgument.getStateId() == stateId) {
				final boolean visible = !firstArgument.isVisible();

				if (firstArgument.hasChildren()) {
					firstBorder.setFont(visible ? boldFont : normalFont);
				}

				firstArgument.setVisible(visible);

				// if (visible)
				// counterExampleFigure.updateStates(argumentBounds.height + 2
				// * insets.top);
				// else
				// counterExampleFigure
				// .updateStates((argumentBounds.height + 2 * insets.top)
				// * -1);
			} else {
				if (firstArgument.hasChildren()) {
					firstBorder.setFont(boldFont);
				}

				firstArgument.setStateId(stateId);
				firstArgument.setVisible(true);
			}
		} else {
			firstBorder.setFont(normalFont);
			firstArgument.setVisible(false);

			stateId = secondArgumentEllipses1.get(source);

			// Rectangle argumentBounds = secondArgumentFigure.getBounds();

			if (secondArgument.getStateId() == stateId) {
				final boolean visible = !secondArgument.isVisible();

				if (secondArgument.hasChildren()) {
					secondBorder.setFont(visible ? boldFont : normalFont);
				}

				secondArgument.setVisible(visible);

				// if (visible)
				// counterExampleFigure.updateStates(argumentBounds.height);
				// else
				// counterExampleFigure.updateStates((argumentBounds.height)
				// * -1);
			} else {
				if (secondArgument.hasChildren()) {
					secondBorder.setFont(boldFont);
				}

				secondArgument.setStateId(stateId);
				secondArgument.setVisible(true);
			}
		}

		repaint();
	}

	@Override
	public void mouseEntered(final MouseEvent me) {
		final Ellipse source = (Ellipse) me.getSource();

		CounterExampleProposition argument = null;
		int stateId;

		if (firstArgumentEllipses1.containsKey(source)) {
			argument = ((CounterExampleBinaryOperator) model)
					.getFirstArgument();
			stateId = firstArgumentEllipses1.get(source);
		} else {
			argument = ((CounterExampleBinaryOperator) model)
					.getSecondArgument();
			stateId = secondArgumentEllipses1.get(source);
		}

		if (!argument.hasChildren())
			return;

		final boolean painted = argument.isVisible();
		final int argumentStateId = argument.getStateId();

		final Label label = new Label();
		label.setForegroundColor(foregroundColor);

		String text = "open ";

		if (stateId == argumentStateId)
			text = painted ? "close " : "open ";

		label.setText("Click to " + text + argument);
		source.setToolTip(label);
	}
}
