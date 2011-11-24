package de.prob.ui.ltl;

import java.util.Collections;
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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
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

	protected final Font normalFont = new Font(null, "Arial", 10, SWT.NORMAL);
	protected final Font boldFont = new Font(null, "Arial", 10, SWT.BOLD);

	protected final int height = 50;
	protected final int borderHeight = 19;

	protected final Hashtable<Integer, Connection> connections = new Hashtable<Integer, Connection>();

	public CounterExamplePropositionFigure(CounterExampleProposition model) {
		this.model = model;
		setLayoutManager(new XYLayout());
		AbstractLabeledBorder border = new GroupBoxBorder();
		border.setTextColor(foregroundColor);
		border.setLabel(model.getFullName());
		border.setFont(boldFont);

		setBorder(border);
		setBackgroundColor(backgroundColor);
		setOpaque(true);
	}

	public void update() {
		Label label = new Label(model.toString());
		label.setForegroundColor(foregroundColor);
		setToolTip(label);
		setVisible(true);

		if (model.isVisible()) {
			CounterExampleProposition parentModel = model.getParent();
			CounterExamplePropositionFigure parent = getFigure(parentModel);
			drawProposition(parent);
		} else {
			setVisible(false);
		}
	}

	protected CounterExamplePropositionFigure getFigure(
			final CounterExampleProposition proposition) {
		if (proposition != null) {
			// We know that each element is of type
			// CounterExamplePropositionFigure,
			// but IFigure.getParent() returns just a list
			@SuppressWarnings("unchecked")
			List<CounterExamplePropositionFigure> figures = getParent()
					.getChildren();

			for (CounterExamplePropositionFigure figure : figures) {
				if (figure.getModel().equals(proposition)) {
					return figure;
				}
			}
		}

		return null;
	}

	protected void setTrasparent(List<CounterExampleProposition> children) {
		for (CounterExampleProposition child : children) {
			CounterExamplePropositionFigure childFigure = getFigure(child);

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

	protected Color getEllipseColor(CounterExampleValueType value) {
		Color color = ColorConstants.gray;

		if (value.equals(CounterExampleValueType.TRUE))
			color = ColorConstants.green;
		else if (value.equals(CounterExampleValueType.FALSE))
			color = ColorConstants.red;

		return color;
	}

	protected String getOperationName(int index) {
		CounterExampleFigure parentFigure = (CounterExampleFigure) getParent();
		CounterExample parentModel = parentFigure.getModel();
		String operationName = parentModel.getStates().get(index)
				.getOperation().getName();
		return operationName;
	}

	protected Hashtable<Integer, Connection> getConnections() {
		return connections;
	}

	protected void drawLoop(Figure parent, Ellipse source, Ellipse target,
			int alpha, String operationName, Color loopColor) {
		PolylineConnection connection = new PolylineConnection();
		connection.setAlpha(alpha);
		connection.setAntialias(SWT.ON);
		connection.setLineWidth(2);

		Label label = new Label(operationName);
		label.setForegroundColor(foregroundColor);
		connection.setToolTip(label);
		connection.setForegroundColor(loopColor);

		PointList points = new PointList();
		Rectangle sourceBounds = source.getBounds();
		Rectangle targetBounds = target.getBounds();
		points.addPoint(new Point(sourceBounds.x + height + borderHeight,
				sourceBounds.y + height / 2 + borderHeight));
		points.addPoint(new Point(sourceBounds.x + height + 3.0 / 10 * height
				+ borderHeight, sourceBounds.y + height / 2 + borderHeight));
		points.addPoint(new Point(sourceBounds.x + height + 3.0 / 10 * height
				+ borderHeight, sourceBounds.y - height / 5 + borderHeight));
		points.addPoint(new Point(targetBounds.x + height / 2 + borderHeight,
				targetBounds.y - height / 5 + borderHeight));
		points.addPoint(new Point(targetBounds.x + height / 2 + borderHeight,
				targetBounds.y + borderHeight));
		connection.setPoints(points);

		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setAlpha(alpha);
		decoration.setAntialias(SWT.ON);
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint(0, 0);
		decorationPointList.addPoint(-1, 1);
		decorationPointList.addPoint(-1, 0);
		decorationPointList.addPoint(-1, -1);
		decoration.setTemplate(decorationPointList);

		connection.setTargetDecoration(decoration);

		parent.add(connection);
	}

	protected void drawFinite(Figure parent, Ellipse source, int alpha) {
		Polyline polyline = new Polyline();
		polyline.setAlpha(alpha);
		polyline.setAntialias(SWT.ON);
		polyline.setLineWidth(2);
		polyline.setToolTip(new Label("Finite"));

		PointList points = new PointList();
		Rectangle sourceBounds = source.getBounds();
		points.addPoint(new Point(sourceBounds.x + height + borderHeight,
				sourceBounds.y + height / 2 + borderHeight));
		points.addPoint(new Point(sourceBounds.x + height + 3.0 / 10 * height
				+ borderHeight, sourceBounds.y + height / 2 + borderHeight));
		points.addPoint(new Point(sourceBounds.x + height + 3.0 / 10 * height
				+ borderHeight, sourceBounds.y + borderHeight + height / 4));
		points.addPoint(new Point(sourceBounds.x + height + 3.0 / 10 * height
				+ borderHeight, sourceBounds.y + borderHeight
				+ (int) (3.0 / 4 * height)));
		polyline.setPoints(points);

		parent.add(polyline);
	}

	protected void drawChildParentConnection(final Ellipse ellipse,
			int stateId, final CounterExamplePropositionFigure parent) {
		if (connections.containsKey(stateId)) {
			connections.get(stateId).setVisible(true);
		} else {
			PolylineConnection connection = new PolylineConnection();
			connection.setAntialias(SWT.ON);
			connection.setLineStyle(SWT.LINE_SOLID);
			connection.setLineWidth(2);

			Rectangle sourceBounds = parent.getBounds();
			Rectangle targetBounds = getBounds();

			PointList points = new PointList();
			points.addPoint(ellipse.getBounds().x + borderHeight + height / 2,
					sourceBounds.y + sourceBounds.height);
			points.addPoint(ellipse.getBounds().x + borderHeight + height / 2,
					targetBounds.y + borderHeight);
			connection.setPoints(points);

			PolygonDecoration decoration = new PolygonDecoration();
			decoration.setAntialias(SWT.ON);
			PointList decorationPointList = new PointList();
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
			final Rectangle panelBounds, int stateId, int argumentHeight) {
		ellipses1.clear();
		ellipses2.clear();

		PathType pathType = model.getPathType();

		List<CounterExampleValueType> values = argument != null ? argument
				.getValues() : model.getValues();

		Panel panel = null;

		if (panelBounds != null) {
			panel = new Panel();
			panel.setBounds(panelBounds);
			final TitleBarBorder border = new TitleBarBorder();
			border.setBackgroundColor(backgroundColor);
			border.setTextColor(foregroundColor);
			border.setLabel(argument.toString());
			border.setFont(normalFont);
			panel.setBorder(border);
			add(panel);
		}

		for (int i = 0; i < values.size(); i++) {
			CounterExampleValueType value = values.get(i);
			final Ellipse ellipse = new Ellipse();

			if (positions != null && !positions.contains(i)) {
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
			Label label = new Label(value.toString());
			label.setOpaque(false);
			ellipse.setLayoutManager(new BorderLayout());
			ellipse.add(label, BorderLayout.CENTER);

			if (panel != null)
				panel.add(ellipse);
			else
				add(ellipse);

			int x = (bounds.x + height) * (i + 1);
			int y = bounds.y + argumentHeight
					+ (pathType == PathType.INFINITE ? height / 10 : 0);
			ellipse.setBounds(new Rectangle(x, y, height, height));

			if (i > 0) {
				ChopboxAnchor source = new ChopboxAnchor(ellipse);
				Ellipse targetEllipse = ellipses2.get(i - 1);

				if (targetEllipse == null)
					continue;

				ChopboxAnchor target = new ChopboxAnchor(targetEllipse);

				PolylineConnection connection = new PolylineConnection();
				connection.setAntialias(SWT.ON);
				connection.setLineStyle(SWT.LINE_SOLID);
				connection.setLineWidth(2);
				connection.setToolTip(new Label(getOperationName(i - 1)));
				connection.setSourceAnchor(source);
				connection.setTargetAnchor(target);

				if (argument != null && argument.isTransition()
						|| model.isTransition())
					connection.setForegroundColor(getEllipseColor(values
							.get(i - 1)));

				PolygonDecoration decoration = new PolygonDecoration();
				decoration.setAntialias(SWT.ON);
				PointList decorationPointList = new PointList();
				decorationPointList.addPoint(0, 0);
				decorationPointList.addPoint(-1, 1);
				decorationPointList.addPoint(-1, 0);
				decorationPointList.addPoint(-1, -1);
				decoration.setTemplate(decorationPointList);

				if (positions == null
						|| (positions.contains(i) && (stateId >= Collections
								.min(positions) ? i != Collections
								.min(positions) : true))) {
					decoration.setAlpha(Alpha.HIGHLIGHED);
					connection.setAlpha(Alpha.HIGHLIGHED);
				} else {
					decoration.setAlpha(Alpha.MASKED);
					connection.setAlpha(Alpha.MASKED);
				}

				connection.setSourceDecoration(decoration);

				if (panel != null)
					panel.add(connection);
				else
					add(connection);
			}

			if (i == values.size() - 1) {
				if (pathType.equals(PathType.INFINITE)) {
					int loopEntry = model.getLoopEntry();

					String operationName = getOperationName(ellipses1
							.get(ellipse));
					Ellipse target = ellipses2.get(model.getLoopEntry());

					Color loopColor = ColorConstants.black;

					if (argument != null && argument.isTransition()
							|| model.isTransition())
						loopColor = getEllipseColor(values.get(i));

					drawLoop(
							panel != null ? panel : this,
							ellipse,
							target,
							(positions == null || positions.contains(loopEntry)
									&& (positions.contains(i) || i == stateId)) ? Alpha.HIGHLIGHED
									: Alpha.MASKED, operationName, loopColor);
				} else if (pathType.equals(PathType.REDUCED)) {
					drawFinite(
							panel != null ? panel : this,
							ellipse,
							(positions == null || positions.contains(i)) ? Alpha.HIGHLIGHED
									: Alpha.MASKED);
				}
			}
		}

		if (parent != null) {
			Ellipse ellipse = ellipses2.get(stateId);
			drawChildParentConnection(ellipse, stateId, parent);
		}

		return panel;
	}

	@Override
	public void mouseReleased(MouseEvent me) {
	}

	@Override
	public void mousePressed(MouseEvent me) {
		System.out.println(me.x + "," + me.y);
	}

	@Override
	public void mouseDoubleClicked(MouseEvent me) {
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
}
