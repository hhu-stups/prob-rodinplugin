package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.geometry.Rectangle;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

public final class CounterExampleUnaryFigure extends
		CounterExamplePropositionFigure implements MouseMotionListener {
	private Panel panel;

	protected final Hashtable<Ellipse, Integer> argumentEllipses1 = new Hashtable<Ellipse, Integer>();
	protected final Hashtable<Integer, Ellipse> argumentEllipses2 = new Hashtable<Integer, Ellipse>();

	public CounterExampleUnaryFigure(CounterExampleProposition model) {
		super(model);
	}

	@Override
	protected void drawProposition(final CounterExamplePropositionFigure parent) {
		removeAll();

		Rectangle bounds = new Rectangle(height, height, height
				* (model.getValues().size() * 2 + 1), (int) (5.0 / 2 * height));

		if (parent != null) {
			Rectangle parentBounds = parent.getBounds();
			bounds = new Rectangle(parentBounds.x - borderHeight,
					parentBounds.y + parentBounds.height + height / 2,
					parentBounds.width, (int) (5.0 / 2 * height));
		}

		getParent().setConstraint(this, bounds);
		setBounds(bounds);

		int stateId = model.getStateId();

		CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();
		List<Integer> positions = ((CounterExampleUnaryOperator) model)
				.getHighlightedPositions().get(stateId);
		Rectangle panelBounds = new Rectangle(bounds.x
				+ (int) (2.0 / 5 * height),
				bounds.y + (int) (2.0 / 5 * height), bounds.width,
				bounds.height);

		panel = drawPropositionFigure(parent, bounds, argument, positions,
				argumentEllipses1, argumentEllipses2, panelBounds, stateId,
				height);
	}

	@Override
	public void mousePressed(MouseEvent me) {
		super.mousePressed(me);

		int stateId = argumentEllipses1.get(me.getSource());

		CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();

		List<CounterExampleProposition> children = argument.getChildren();
		children = children.subList(1, children.size());
		setTrasparent(children);

		CounterExamplePropositionFigure argumentFigure = getFigure(argument);

		for (Connection connection : argumentFigure.getConnections().values()) {
			connection.setVisible(false);
		}

		TitleBarBorder border = (TitleBarBorder) panel.getBorder();

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
	}

	@Override
	public void mouseEntered(MouseEvent me) {
		Ellipse source = (Ellipse) me.getSource();

		CounterExampleProposition argument = ((CounterExampleUnaryOperator) model)
				.getArgument();

		if (!argument.hasChildren())
			return;

		boolean painted = argument.isVisible();
		int argumentStateId = argument.getStateId();
		int stateId = argumentEllipses1.get(source);

		Label label = new Label();
		label.setForegroundColor(foregroundColor);

		String text = "open ";

		if (stateId == argumentStateId)
			text = painted ? "close " : "open ";

		label.setText("Click to " + text + argument);
		source.setToolTip(label);
	}
}
