/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MidpointLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.XYAnchor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.AccessibleAnchorProvider;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.SelectEditPartTracker;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeConnectionSourceDecoration;
import de.bmotionstudio.gef.editor.command.ConnectionDeleteCommand;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;

public class BConnectionEditPart extends BMSAbstractEditPart implements
		ConnectionEditPart, LayerConstants {

	protected Color foregroundColor;
	private Label conLabel;

	private static final ConnectionAnchor DEFAULT_SOURCE_ANCHOR = new XYAnchor(
			new Point(10, 10));
	private static final ConnectionAnchor DEFAULT_TARGET_ANCHOR = new XYAnchor(
			new Point(100, 100));

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.part.AppAbstractEditPart#deactivate()
	 */
	@Override
	public void deactivate() {
		super.deactivate();
		if (isActive())
			foregroundColor.dispose();
	}

	@Override
	protected void prepareEditPolicies() {
		// Selection handle edit policy.
		// Makes the connection show a feedback, when selected by the user.
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy()); // Allows the removal of
														// the connection model
														// element
		installEditPolicy(EditPolicy.CONNECTION_ROLE,
				new ConnectionEditPolicy() {
					protected Command getDeleteCommand(GroupRequest request) {
						return new ConnectionDeleteCommand(
								(BConnection) getModel());
					}
				});
	}

	@Override
	protected IFigure createEditFigure() {
		PolylineConnection connection = new PolylineConnection();
		conLabel = new Label();
		MidpointLocator locator = new MidpointLocator(connection, 0);
		locator.setRelativePosition(PositionConstants.NORTH);
		connection.add(conLabel, locator);
		return connection;
	}

	@Override
	public void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent evt) {

		Object value = evt.getNewValue();
		String aID = evt.getPropertyName();

		if (aID.equals(AttributeConstants.ATTRIBUTE_LINEWIDTH))
			((PolylineConnection) getFigure()).setLineWidth(Integer
					.valueOf(value.toString()));

		if (aID.equals(AttributeConstants.ATTRIBUTE_LINESTYLE))
			((PolylineConnection) getFigure()).setLineStyle((Integer
					.valueOf(value.toString()) + 1));

		if (aID.equals(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR)) {
			if (foregroundColor != null)
				foregroundColor.dispose();
			foregroundColor = new Color(Display.getDefault(), (RGB) value);
			((PolylineConnection) getFigure())
					.setForegroundColor(foregroundColor);
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_CONNECTION_SOURCE_DECORATION)) {
			int decoration = Integer.valueOf(value.toString());
			if (decoration == BAttributeConnectionSourceDecoration.DECORATION_TRIANGLE) {
				((PolylineConnection) getFigure())
						.setSourceDecoration(new PolygonDecoration());
			} else {
				((PolylineConnection) getFigure()).setSourceDecoration(null);
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_CONNECTION_TARGET_DECORATION)) {
			int decoration = Integer.valueOf(value.toString());
			if (decoration == BAttributeConnectionSourceDecoration.DECORATION_TRIANGLE) {
				((PolylineConnection) getFigure())
						.setTargetDecoration(new PolygonDecoration());
			} else {
				((PolylineConnection) getFigure()).setTargetDecoration(null);
			}
		}

		if (aID.equals(AttributeConstants.ATTRIBUTE_LABEL)) {
			conLabel.setText(value.toString());
		}

	}

	/**
	 * Provides accessibility support for when connections are also themselves
	 * nodes. If a connection is the source or target of another connection,
	 * then its midpoint is used as the accessible anchor location.
	 * 
	 * @author hudsonr
	 * @since 2.0
	 */
	protected final class DefaultAccessibleAnchorProvider implements
			AccessibleAnchorProvider {
		/**
		 * This class is internal, but is made protected so that JavaDoc will
		 * see it.
		 */
		DefaultAccessibleAnchorProvider() {
		}

		/**
		 * @see AccessibleAnchorProvider#getSourceAnchorLocations()
		 */
		public List<Point> getSourceAnchorLocations() {
			List<Point> list = new ArrayList<Point>();
			if (getFigure() instanceof Connection) {
				Point p = ((Connection) getFigure()).getPoints().getMidpoint();
				getFigure().translateToAbsolute(p);
				list.add(p);
			}
			return list;
		}

		/**
		 * @see AccessibleAnchorProvider#getTargetAnchorLocations()
		 */
		public List<Point> getTargetAnchorLocations() {
			return getSourceAnchorLocations();
		}
	}

	private EditPart sourceEditPart, targetEditPart;

	/**
	 * Activates the Figure representing this, by setting up the start and end
	 * connections, and adding the figure to the Connection Layer.
	 * 
	 * @see #deactivate()
	 */
	protected void activateFigure() {
		getLayer(CONNECTION_LAYER).add(getFigure());
	}

	/**
	 * @see org.eclipse.gef.EditPart#addNotify()
	 */
	public void addNotify() {
		activateFigure();
		super.addNotify();
	}

	/**
	 * Deactivates the Figure representing this, by removing it from the
	 * connection layer, and resetting the source and target connections to
	 * <code>null</code>.
	 */
	protected void deactivateFigure() {
		getLayer(CONNECTION_LAYER).remove(getFigure());
		getConnectionFigure().setSourceAnchor(null);
		getConnectionFigure().setTargetAnchor(null);
	}

	/**
	 * <code>AbstractConnectionEditPart</code> extends getAdapter() to overrides
	 * the {@link AccessibleAnchorProvider} adapter returned by the superclass.
	 * When treating a connection as a node for other connections, it makes
	 * sense to target its midpoint, and not the edge of its bounds.
	 * 
	 * @see AbstractConnectionEditPart.DefaultAccessibleAnchorProvider
	 * @see AbstractGraphicalEditPart#getAdapter(Class)
	 * @param adapter
	 *            the adapter Class
	 * @return the adapter
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (adapter == AccessibleAnchorProvider.class)
			return new DefaultAccessibleAnchorProvider();
		return super.getAdapter(adapter);
	}

	/**
	 * Convenience method for casting this GraphicalEditPart's Figure to a
	 * {@link Connection}
	 * 
	 * @return the Figure as a Connection
	 */
	public Connection getConnectionFigure() {
		return (Connection) getFigure();
	}

	/**
	 * @see org.eclipse.gef.EditPart#getDragTracker(Request)
	 */
	public DragTracker getDragTracker(Request req) {
		return new SelectEditPartTracker(this);
	}

	/**
	 * @see org.eclipse.gef.ConnectionEditPart#getSource()
	 */
	public EditPart getSource() {
		return sourceEditPart;
	}

	/**
	 * @see org.eclipse.gef.ConnectionEditPart#getTarget()
	 */
	public EditPart getTarget() {
		return targetEditPart;
	}

	/**
	 * Returns the <code>ConnectionAnchor</code> for the <i>source</i> end of
	 * the connection. If the source is an instance of {@link NodeEditPart},
	 * that interface will be used to determine the proper ConnectionAnchor. If
	 * the source is not an instance of <code>NodeEditPart</code>, this method
	 * should be overridden to return the correct ConnectionAnchor. Failure to
	 * do this will cause a default anchor to be used so that the connection
	 * figure will be made visible to the developer.
	 * 
	 * @return ConnectionAnchor for the source end of the Connection
	 */
	protected ConnectionAnchor getSourceConnectionAnchor() {
		if (getSource() != null) {
			if (getSource() instanceof NodeEditPart) {
				NodeEditPart editPart = (NodeEditPart) getSource();
				return editPart.getSourceConnectionAnchor(this);
			}
			IFigure f = ((GraphicalEditPart) getSource()).getFigure();
			return new ChopboxAnchor(f);
		}
		return DEFAULT_SOURCE_ANCHOR;
	}

	/**
	 * Returns the <code>ConnectionAnchor</code> for the <i>target</i> end of
	 * the connection. If the target is an instance of {@link NodeEditPart},
	 * that interface will be used to determine the proper ConnectionAnchor. If
	 * the target is not an instance of <code>NodeEditPart</code>, this method
	 * should be overridden to return the correct ConnectionAnchor. Failure to
	 * do this will cause a default anchor to be used so that the connection
	 * figure will be made visible to the developer.
	 * 
	 * @return ConnectionAnchor for the target end of the Connection
	 */
	protected ConnectionAnchor getTargetConnectionAnchor() {
		if (getTarget() != null) {
			if (getTarget() instanceof NodeEditPart) {
				NodeEditPart editPart = (NodeEditPart) getTarget();
				return editPart.getTargetConnectionAnchor(this);
			}
			IFigure f = ((GraphicalEditPart) getTarget()).getFigure();
			return new ChopboxAnchor(f);
		}
		return DEFAULT_TARGET_ANCHOR;
	}

	/**
	 * Extended here to also refresh the ConnectionAnchors.
	 * 
	 * @see org.eclipse.gef.EditPart#refresh()
	 */
	public void refresh() {
		refreshSourceAnchor();
		refreshTargetAnchor();
		super.refresh();
	}

	/**
	 * Updates the source ConnectionAnchor. Subclasses should override
	 * {@link #getSourceConnectionAnchor()} if necessary, and not this method.
	 */
	protected void refreshSourceAnchor() {
		getConnectionFigure().setSourceAnchor(getSourceConnectionAnchor());
	}

	/**
	 * Updates the target ConnectionAnchor. Subclasses should override
	 * {@link #getTargetConnectionAnchor()} if necessary, and not this method.
	 */
	protected void refreshTargetAnchor() {
		getConnectionFigure().setTargetAnchor(getTargetConnectionAnchor());
	}

	/**
	 * Extended here to remove the ConnectionEditPart's connection figure from
	 * the connection layer.
	 * 
	 * @see org.eclipse.gef.EditPart#removeNotify()
	 */
	public void removeNotify() {
		deactivateFigure();
		super.removeNotify();
	}

	/**
	 * Extended to implement automatic addNotify and removeNotify handling.
	 * 
	 * @see org.eclipse.gef.EditPart#setParent(EditPart)
	 */
	public void setParent(EditPart parent) {
		boolean wasNull = getParent() == null;
		boolean becomingNull = parent == null;
		if (becomingNull && !wasNull)
			removeNotify();
		super.setParent(parent);
		if (wasNull && !becomingNull)
			addNotify();
	}

	/**
	 * Sets the source EditPart of this connection.
	 * 
	 * @param editPart
	 *            EditPart which is the source.
	 */
	public void setSource(EditPart editPart) {
		if (sourceEditPart == editPart)
			return;
		sourceEditPart = editPart;
		if (sourceEditPart != null)
			setParent(sourceEditPart.getRoot());
		else if (getTarget() == null)
			setParent(null);
		if (sourceEditPart != null && targetEditPart != null)
			refresh();
	}

	/**
	 * Sets the target EditPart of this connection.
	 * 
	 * @param editPart
	 *            EditPart which is the target.
	 */
	public void setTarget(EditPart editPart) {
		if (targetEditPart == editPart)
			return;
		targetEditPart = editPart;
		if (editPart != null)
			setParent(editPart.getRoot());
		else if (getSource() == null)
			setParent(null);
		if (sourceEditPart != null && targetEditPart != null)
			refresh();
	}

	@Override
	protected void prepareRunPolicies() {
		// TODO Auto-generated method stub

	}

}