package de.prob.ui.ltl;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.Polyline;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

import de.prob.core.Animator;
import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ltl.handler.CounterExampleHistoryHandler;

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

			drawPropositionFigure(bounds, ellipses1, ellipses2,
					model.getStateId(), 3 * size / 5);
		}
	}

	private void drawPropositionFigure(final Rectangle bounds,
			final Hashtable<Ellipse, Integer> ellipses1,
			final Hashtable<Integer, Ellipse> ellipses2, final int stateId,
			final int argumentHeight) {
		ellipses1.clear();
		ellipses2.clear();

		final PathType pathType = model.getPathType();

		final List<CounterExampleValueType> values = model.getValues();

		for (int i = 0; i < values.size(); i++) {
			final CounterExampleValueType value = values.get(i);
			final Ellipse ellipse = new Ellipse();

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

			add(ellipse);

			final int x = (bounds.x + size) * (i + 1);
			final int y = bounds.y + argumentHeight
					+ (pathType == PathType.INFINITE ? size / 10 : 0);
			ellipse.setBounds(new Rectangle(x, y, size, size));

			if (i > 0) {
				final ChopboxAnchor source = new ChopboxAnchor(ellipse);
				final Ellipse targetEllipse = ellipses2.get(i - 1);

				if (targetEllipse == null)
					continue;

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
				connection.setAlpha(Alpha.HIGHLIGHED);
				decoration.setAlpha(Alpha.HIGHLIGHED);

				connection.setSourceDecoration(decoration);

				add(connection);
			}

			if (i == values.size() - 1) {
				if (pathType.equals(PathType.INFINITE)) {
					final String operationName = getOperationName(ellipses1
							.get(ellipse));
					final Ellipse target = ellipses2.get(model.getLoopEntry());

					int alpha = Alpha.MASKED;
					Color loopTransitionColor = ColorConstants.black;

					alpha = Alpha.HIGHLIGHED;

					final PolylineConnection loop = createLoop(getInsets(),
							ellipse, target, alpha, operationName,
							loopTransitionColor);

					add(loop);
				} else if (pathType.equals(PathType.REDUCED)) {
					final Polyline reduced = createReduced(getInsets(),
							ellipse, Alpha.HIGHLIGHED);

					this.add(reduced);
				}
			}
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
