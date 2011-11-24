package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;

public final class CounterExampleBinaryFigure extends
		CounterExamplePropositionFigure {
	private Panel firstPanel;
	private Panel secondPanel;

	protected final Hashtable<Ellipse, Integer> firstArgumentEllipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> firstArgumentEllipses2 = new Hashtable<Integer, Ellipse>();
	private final Hashtable<Ellipse, Integer> secondArgumentEllipses1 = new Hashtable<Ellipse, Integer>();
	private final Hashtable<Integer, Ellipse> secondArgumentEllipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExampleBinaryFigure(CounterExampleProposition model) {
		super(model);
	}

	@Override
	protected void drawProposition(final CounterExamplePropositionFigure parent) {
		removeAll();

		Rectangle bounds = new Rectangle(height, height, height
				* (model.getValues().size() * 2 + 1), (int) (9.0 / 2 * height));

		if (parent != null) {
			Rectangle parentBounds = parent.getBounds();
			bounds = new Rectangle(parentBounds.x - borderHeight,
					parentBounds.y + parentBounds.height + height / 2,
					parentBounds.width, (int) (9.0 / 2 * height));
		}

		getParent().setConstraint(this, bounds);
		setBounds(bounds);

		int stateId = model.getStateId();

		CounterExampleProposition firstArgument = ((CounterExampleBinaryOperator) model)
				.getFirstArgument();
		List<Integer> firstPositions = ((CounterExampleBinaryOperator) model)
				.getFirstHighlightedPositions().get(stateId);
		Rectangle firstPanelBounds = new Rectangle(bounds.x
				+ (int) (2.0 / 5 * height),
				bounds.y + (int) (2.0 / 5 * height), bounds.width,
				bounds.height / 2);
		firstPanel = drawPropositionFigure(parent, bounds, firstArgument,
				firstPositions, firstArgumentEllipses1, firstArgumentEllipses2,
				firstPanelBounds, stateId, height);

		Rectangle secondPanelBounds = new Rectangle(bounds.x + 20, bounds.y
				+ (int) (12.0 / 5 * height), bounds.width, bounds.height / 2);
		CounterExampleProposition secondArgument = ((CounterExampleBinaryOperator) model)
				.getSecondArgument();

		List<Integer> secondPositions = ((CounterExampleBinaryOperator) model)
				.getSecondHighlightedPositions().get(stateId);
		secondPanel = drawPropositionFigure(parent, bounds, secondArgument,
				secondPositions, secondArgumentEllipses1,
				secondArgumentEllipses2, secondPanelBounds, stateId, 3 * height);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		super.mousePressed(me);
		int stateId;

		CounterExampleProposition firstArgument = ((CounterExampleBinaryOperator) model)
				.getFirstArgument();
		List<CounterExampleProposition> firstChildren = firstArgument
				.getChildren();
		firstChildren = firstChildren.subList(1, firstChildren.size());
		setTrasparent(firstChildren);

		CounterExamplePropositionFigure firstArgumentFigure = getFigure(firstArgument);

		for (Connection connection : firstArgumentFigure.getConnections()
				.values()) {
			connection.setVisible(false);
		}

		CounterExampleProposition secondArgument = ((CounterExampleBinaryOperator) model)
				.getSecondArgument();
		List<CounterExampleProposition> secondChildren = secondArgument
				.getChildren();
		secondChildren = secondChildren.subList(1, secondChildren.size());
		setTrasparent(secondChildren);

		CounterExamplePropositionFigure secondArgumentFigure = getFigure(secondArgument);

		for (Connection connection : secondArgumentFigure.getConnections()
				.values()) {
			connection.setVisible(false);
		}

		Object source = me.getSource();

		if (firstPanel == null || secondPanel == null)
			return;

		TitleBarBorder firstBorder = (TitleBarBorder) firstPanel.getBorder();
		TitleBarBorder secondBorder = (TitleBarBorder) secondPanel.getBorder();

		if (firstArgumentEllipses1.containsKey(source)) {
			secondBorder.setFont(normalFont);
			secondArgument.setVisible(false);

			stateId = firstArgumentEllipses1.get(source);

			if (firstArgument.getStateId() == stateId) {
				boolean visible = !firstArgument.isVisible();

				if (firstArgument.hasChildren()) {
					firstBorder.setFont(visible ? boldFont : normalFont);
				}

				firstArgument.setVisible(visible);
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

			if (secondArgument.getStateId() == stateId) {
				boolean visible = !secondArgument.isVisible();

				if (secondArgument.hasChildren()) {
					secondBorder.setFont(visible ? boldFont : normalFont);
				}

				secondArgument.setVisible(visible);
			} else {
				if (secondArgument.hasChildren()) {
					secondBorder.setFont(boldFont);
				}

				secondArgument.setStateId(stateId);
				secondArgument.setVisible(true);
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		Ellipse source = (Ellipse) me.getSource();

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

		boolean painted = argument.isVisible();
		int argumentStateId = argument.getStateId();

		Label label = new Label();
		label.setForegroundColor(foregroundColor);

		String text = "open ";

		if (stateId == argumentStateId)
			text = painted ? "close " : "open ";

		label.setText("Click to " + text + argument);
		source.setToolTip(label);
	}
}
