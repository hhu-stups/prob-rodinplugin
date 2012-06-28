package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.draw2d.AbstractLabeledBorder;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.MouseMotionListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.TitleBarBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleState;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

public abstract class CounterExamplePropositionFigure extends Figure implements
		MouseListener, MouseMotionListener {
	public static class Alpha {
		public static final int MASKED = 30;
		public static final int HIGHLIGHED = 250;
	};

	protected final CounterExampleProposition model;

	protected final Color foregroundColor = ColorConstants.lightBlue;
	protected final Color backgroundColor = new Color(null, 255, 255, 206);

	protected final Font normalFont = new Font(Display.getDefault(), "Arial",
			10, SWT.NORMAL);
	protected final Font boldFont = new Font(Display.getDefault(), "Arial", 10,
			SWT.BOLD);

	protected final int size = 50;

	protected final Hashtable<Integer, Connection> connections = new Hashtable<Integer, Connection>();

	public CounterExamplePropositionFigure(final CounterExampleProposition model) {
		this.model = model;

		setLayoutManager(new XYLayout());

		final AbstractLabeledBorder border = new GroupBoxBorder();
		border.setTextColor(foregroundColor);
		border.setLabel(model.getFullName());
		border.setFont(boldFont);

		setBorder(border);
		setBackgroundColor(backgroundColor);
		setOpaque(true);
	}

	public void update() {
		final Label label = new Label(model.toString());
		label.setForegroundColor(foregroundColor);
		setToolTip(label);
		setVisible(true);

		if (model.isVisible()) {
			final CounterExampleProposition parentModel = model.getParent();
			final CounterExamplePropositionFigure parent = getFigure(parentModel);
			drawProposition(parent);
		} else {
			setVisible(false);
		}
	}

	protected CounterExamplePropositionFigure getFigure(
			final CounterExampleProposition proposition) {
		if (proposition != null) {
			final IFigure parent = getParent();

			if (parent != null) {
				// We know that each element is of type
				// IFigure, but IFigure.getParent() returns just a list
				@SuppressWarnings("unchecked")
				final List<IFigure> figures = parent.getChildren();

				for (IFigure figure : figures) {
					if (figure instanceof CounterExamplePropositionFigure) {
						if (((CounterExamplePropositionFigure) figure)
								.getModel().equals(proposition)) {
							return (CounterExamplePropositionFigure) figure;
						}
					}
				}
			}
		}

		return null;
	}

	protected void setTrasparent(final List<CounterExampleProposition> children) {
		for (CounterExampleProposition child : children) {
			final CounterExamplePropositionFigure childFigure = getFigure(child);

			for (Connection connection : childFigure.getConnections().values()) {
				connection.setVisible(false);
			}

			child.setVisible(false);
		}
	}

	protected abstract void drawProposition(
			final CounterExamplePropositionFigure parent);

	protected CounterExampleProposition getModel() {
		return model;
	}

	protected Color getEllipseColor(final CounterExampleValueType value) {
		Color color = ColorConstants.gray;

		if (value.equals(CounterExampleValueType.TRUE))
			color = ColorConstants.green;
		else if (value.equals(CounterExampleValueType.FALSE))
			color = ColorConstants.red;

		return color;
	}

	protected String getOperationName(final int index) {
		final CounterExampleFigure parentFigure = (CounterExampleFigure) getParent();
		final CounterExample parentModel = parentFigure.getModel();
		final List<CounterExampleState> states = parentModel.getStates();
		final CounterExampleState state = states.get(index);
		final Operation operation = state.getOperation();
		final String operationName = operation.getName();
		return operationName;
	}

	protected Hashtable<Integer, Connection> getConnections() {
		return connections;
	}

	protected PolylineConnection createLoop(final Insets insets,
			final Ellipse source, final Ellipse target, final int alpha,
			final String operationName, final Color loopColor) {
		final PolylineConnection connection = new PolylineConnection();
		connection.setAlpha(alpha);
		connection.setAntialias(SWT.ON);
		connection.setLineWidth(2);

		final Label label = new Label(operationName);
		label.setForegroundColor(foregroundColor);
		connection.setToolTip(label);
		connection.setForegroundColor(loopColor);

		final PointList points = new PointList();
		final Rectangle sourceBounds = source.getBounds();
		final Rectangle targetBounds = target.getBounds();
		points.addPoint(new Point(sourceBounds.x + size + insets.left,
				sourceBounds.y + size / 2 + insets.top));
		points.addPoint(new Point(sourceBounds.x + size + (3 * size) / 10
				+ insets.left, sourceBounds.y + size / 2 + insets.top));
		points.addPoint(new Point(sourceBounds.x + size + (3 * size) / 10
				+ insets.left, sourceBounds.y - size / 5 + insets.top));
		points.addPoint(new Point(targetBounds.x + size / 2 + insets.left,
				targetBounds.y - size / 5 + insets.top));
		points.addPoint(new Point(targetBounds.x + size / 2 + insets.left,
				targetBounds.y + insets.top));
		connection.setPoints(points);

		final PolygonDecoration decoration = new PolygonDecoration();
		decoration.setForegroundColor(loopColor);
		decoration.setAlpha(alpha);
		decoration.setAntialias(SWT.ON);

		final PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-1, 1);
		decorationPointList.addPoint(-1, 0);
		decorationPointList.addPoint(-1, -1);
		decoration.setTemplate(decorationPointList);

		connection.setTargetDecoration(decoration);

		return connection;
	}

	protected Polyline createReduced(final Insets insets, final Ellipse source,
			final int alpha) {
		final Polyline polyline = new Polyline();
		polyline.setAlpha(alpha);
		polyline.setAntialias(SWT.ON);
		polyline.setLineWidth(2);
		polyline.setToolTip(new Label("Reduced"));

		final PointList points = new PointList();
		final Rectangle sourceBounds = source.getBounds();
		points.addPoint(new Point(sourceBounds.x + size + insets.left,
				sourceBounds.y + size / 2 + insets.top));
		points.addPoint(new Point(sourceBounds.x + size + (3 * size) / 10
				+ insets.left, sourceBounds.y + size / 2 + insets.top));
		points.addPoint(new Point(sourceBounds.x + size + (3 * size) / 10
				+ insets.left, sourceBounds.y + insets.top + size / 4));
		points.addPoint(new Point(sourceBounds.x + size + (3 * size) / 10
				+ insets.left, sourceBounds.y + insets.top + (3 * size) / 4));
		polyline.setPoints(points);

		return polyline;
	}

	protected void drawChildParentConnection(final Ellipse ellipse,
			final int stateId, final CounterExamplePropositionFigure parent) {
		if (connections.containsKey(stateId)) {
			connections.get(stateId).setVisible(true);
		} else {
			final Insets insets = parent.getInsets();

			final PolylineConnection connection = new PolylineConnection();
			connection.setAntialias(SWT.ON);
			connection.setLineStyle(SWT.LINE_SOLID);
			connection.setLineWidth(2);

			final Rectangle sourceBounds = parent.getBounds();
			final Rectangle targetBounds = getBounds();

			final PointList points = new PointList();
			points.addPoint(ellipse.getBounds().x + insets.left + size / 2,
					sourceBounds.y + sourceBounds.height);
			points.addPoint(ellipse.getBounds().x + insets.left + size / 2,
					targetBounds.y + insets.top);
			connection.setPoints(points);

			final PolygonDecoration decoration = new PolygonDecoration();
			decoration.setAntialias(SWT.ON);

			final PointList decorationPointList = new PointList();
			decorationPointList.addPoint(0, 0);
			decorationPointList.addPoint(-1, 1);
			decorationPointList.addPoint(-1, 0);
			decorationPointList.addPoint(-1, -1);
			decoration.setTemplate(decorationPointList);

			connection.setSourceDecoration(decoration);
			getParent().add(connection);
			connections.put(stateId, connection);
		}
	}

	protected Panel drawPropositionFigure(
			final CounterExamplePropositionFigure parent,
			final Rectangle bounds, final CounterExampleProposition argument,
			final List<Integer> positions,
			final Hashtable<Ellipse, Integer> ellipses1,
			final Hashtable<Integer, Ellipse> ellipses2,
			final Rectangle panelBounds, final int stateId,
			final int argumentHeight) {
		ellipses1.clear();
		ellipses2.clear();

		final PathType pathType = model.getPathType();

		final List<CounterExampleValueType> values = argument.getValues();

		Panel panel = new Panel();
		panel.setBounds(panelBounds);

		final TitleBarBorder border = new TitleBarBorder();
		border.setBackgroundColor(backgroundColor);
		border.setTextColor(foregroundColor);
		border.setLabel(argument.toString());
		border.setFont(normalFont);

		panel.setBorder(border);
		add(panel);

		for (int i = 0; i < values.size(); i++) {
			createColumn(bounds, argument, positions, ellipses1, ellipses2,
					argumentHeight, pathType, values, panel, i);
		}

		if (parent != null) {
			final Ellipse ellipse = ellipses2.get(stateId);
			drawChildParentConnection(ellipse, stateId, parent);
		}

		return panel;
	}

	private void createColumn(final Rectangle bounds,
			final CounterExampleProposition argument,
			final List<Integer> positions,
			final Hashtable<Ellipse, Integer> ellipses1,
			final Hashtable<Integer, Ellipse> ellipses2,
			final int argumentHeight, final PathType pathType,
			final List<CounterExampleValueType> values, Panel panel, int i) {
		final CounterExampleValueType value = values.get(i);
		final Ellipse ellipse = new Ellipse();

		if (!positions.contains(i)) {
			ellipse.setAlpha(Alpha.MASKED);
		}

		ellipse.setAntialias(SWT.ON);
		ellipse.setLineWidth(2);
		ellipse.setOpaque(true);
		ellipse.addMouseListener(this);
		ellipse.addMouseMotionListener(this);
		ellipse.setBackgroundColor(getEllipseColor(value));

		ellipses1.put(ellipse, i);
		ellipses2.put(i, ellipse);

		final Label label = new Label(value.toString());
		label.setOpaque(false);

		ellipse.setLayoutManager(new BorderLayout());
		ellipse.add(label, BorderLayout.CENTER);

		panel.add(ellipse);

		final int x = (bounds.x + size) * (i + 1);
		final int y = bounds.y + argumentHeight
				+ (pathType == PathType.INFINITE ? size / 10 : 0);
		ellipse.setBounds(new Rectangle(x, y, size, size));

		if (i > 0) {
			final ChopboxAnchor source = new ChopboxAnchor(ellipse);
			final Ellipse targetEllipse = ellipses2.get(i - 1);

			if (targetEllipse == null)
				return;

			final ChopboxAnchor target = new ChopboxAnchor(targetEllipse);

			final PolylineConnection connection = new PolylineConnection();
			connection.setAlpha(Alpha.MASKED);
			connection.setAntialias(SWT.ON);
			connection.setLineStyle(SWT.LINE_SOLID);
			connection.setLineWidth(2);
			connection.setToolTip(new Label(getOperationName(i - 1)));
			connection.setSourceAnchor(source);
			connection.setTargetAnchor(target);

			final PolygonDecoration decoration = new PolygonDecoration();
			decoration.setAlpha(Alpha.MASKED);
			decoration.setAntialias(SWT.ON);

			final PointList decorationPointList = new PointList();
			decorationPointList.addPoint(0, 0);
			decorationPointList.addPoint(-1, 1);
			decorationPointList.addPoint(-1, 0);
			decorationPointList.addPoint(-1, -1);
			decoration.setTemplate(decorationPointList);

			// highlight the transition
			if (positions.contains(i) && positions.contains(i - 1)) {
				connection.setAlpha(Alpha.HIGHLIGHED);
				decoration.setAlpha(Alpha.HIGHLIGHED);
			}

			// highlight and color the transition
			if (model.isTransition() || argument.isTransition()) {
				if (positions.contains(i - 1)) {
					connection.setAlpha(Alpha.HIGHLIGHED);
					decoration.setAlpha(Alpha.HIGHLIGHED);
					Color transitionColor = getEllipseColor(values.get(i - 1));
					connection.setForegroundColor(transitionColor);
					decoration.setForegroundColor(transitionColor);
				}
			}

			connection.setSourceDecoration(decoration);

			panel.add(connection);
		}

		boolean isLastElement = i == values.size() - 1;
		if (isLastElement) {
			createEnd(argument, positions, ellipses1, ellipses2, pathType,
					values, panel, i, ellipse);
		}
	}

	private void createEnd(final CounterExampleProposition argument,
			final List<Integer> positions,
			final Hashtable<Ellipse, Integer> ellipses1,
			final Hashtable<Integer, Ellipse> ellipses2,
			final PathType pathType,
			final List<CounterExampleValueType> values, Panel panel, int i,
			final Ellipse ellipse) {
		final IFigure figure;
		switch (pathType) {
		case INFINITE:
			final String operationName = getOperationName(ellipses1
					.get(ellipse));
			final Ellipse target = ellipses2.get(model.getLoopEntry());

			int alpha = Alpha.MASKED;
			Color loopTransitionColor = ColorConstants.black;

			final boolean highlightLoop = positions.contains(i)
					&& positions.contains(i - 1);
			if (highlightLoop) {
				alpha = Alpha.HIGHLIGHED;
			}

			if (model.isTransition() || argument.isTransition()) {
				if (positions.contains(i)) {
					alpha = Alpha.HIGHLIGHED;
					loopTransitionColor = getEllipseColor(values.get(i));
				}
			}
			figure = createLoop(getInsets(), ellipse, target, alpha,
					operationName, loopTransitionColor);
			break;

		case REDUCED:
			figure = createReduced(getInsets(), ellipse,
					positions.contains(i) ? Alpha.HIGHLIGHED : Alpha.MASKED);
			break;

		default:
			figure = null;
			break;
		}
		if (figure != null) {
			panel.add(figure);
		}
	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mousePressed(MouseEvent me) {
		me.consume();
		me.consume();
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
		System.out.println("");
	}

	@Override
	public void mouseDragged(MouseEvent me) {
	}

	@Override
	public void mouseExited(MouseEvent me) {
	}

	@Override
	public void mouseHover(MouseEvent me) {
	}

	@Override
	public void mouseMoved(MouseEvent me) {
	}

	@Override
	public void mouseEntered(MouseEvent me) {
	}
}
