/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.editpolicy;

import java.util.List;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.geometry.Translatable;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;

import de.bmotionstudio.gef.editor.command.AddCommand;
import de.bmotionstudio.gef.editor.command.BControlChangeLayoutCommand;
import de.bmotionstudio.gef.editor.command.ReorderPartCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Light;
import de.bmotionstudio.gef.editor.model.Signal;

public class SignalLayoutEditPolicy extends FlowLayoutEditPolicy {

	private static final Dimension PREFERRED_SIZE = new Dimension(-1, -1);

	/**
	 * Constant being used to indicate that upon creation (or during move) a
	 * size was not specified.
	 * 
	 * @since 3.7
	 */
	protected static final Dimension UNSPECIFIED_SIZE = new Dimension();

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {

		BControl childModel = (BControl) child.getModel();
		BControl parentModel = (BControl) getHost().getModel();
		if ((childModel instanceof Light && parentModel instanceof Signal)) {
			AddCommand command = new AddCommand();
			command.setChild(childModel);
			command.setParent(parentModel);
			int index = getHost().getChildren().indexOf(after);
			command.setIndex(index);
			return command;
		}
		return null;

	}

	@Override
	public Command getCommand(Request request) {
		if (REQ_RESIZE_CHILDREN.equals(request.getType()))
			return getResizeChildrenCommand((ChangeBoundsRequest) request);
		return super.getCommand(request);
	}

	protected Command getResizeChildrenCommand(ChangeBoundsRequest request) {
		CompoundCommand resize = new CompoundCommand();
		Command c;
		GraphicalEditPart child;
		List<?> children = request.getEditParts();
		for (int i = 0; i < children.size(); i++) {
			child = (GraphicalEditPart) children.get(i);
			c = createChangeConstraintCommand(
					request,
					child,
					translateToModelConstraint(getConstraintFor(request, child)));
			resize.add(c);
		}
		return resize.unwrap();
	}

	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {
		BControlChangeLayoutCommand cmd = new BControlChangeLayoutCommand();
		cmd.setModel(child.getModel());
		cmd.setConstraint((Rectangle) constraint);
		return cmd;
	}

	/**
	 * Generates a draw2d constraint object for the given
	 * <code>ChangeBoundsRequest</code> and child EditPart by delegating to
	 * {@link #getConstraintFor(Request, GraphicalEditPart, Rectangle)}.
	 * 
	 * The rectangle being passed over to
	 * {@link #getConstraintFor(Request, GraphicalEditPart, Rectangle)} is
	 * calculated based on the child figure's current bounds and the
	 * ChangeBoundsRequest's move and resize deltas. It is made layout-relative
	 * by using {@link #translateFromAbsoluteToLayoutRelative(Translatable)}
	 * before calling
	 * {@link #getConstraintFor(Request, GraphicalEditPart, Rectangle)}.
	 * 
	 * @param request
	 *            the ChangeBoundsRequest
	 * @param child
	 *            the child EditPart for which the constraint should be
	 *            generated
	 * @return the draw2d constraint
	 */
	protected Object getConstraintFor(ChangeBoundsRequest request,
			GraphicalEditPart child) {
		Rectangle locationAndSize = new PrecisionRectangle(child.getFigure()
				.getBounds());
		child.getFigure().translateToAbsolute(locationAndSize);
		locationAndSize = request.getTransformedRectangle(locationAndSize);
		translateFromAbsoluteToLayoutRelative(locationAndSize);
		return getConstraintFor(request, child, locationAndSize);
	}

	/**
	 * Responsible of generating a draw2d constraint for the given Rectangle,
	 * which represents the already transformed (layout-relative) position and
	 * size of the given Request.
	 * 
	 * By default, this method delegates to {@link #getConstraintFor(Point)} or
	 * {@link #getConstraintFor(Rectangle)}, dependent on whether the size of
	 * the rectangle is an {@link #UNSPECIFIED_SIZE} or not.
	 * 
	 * Subclasses may overwrite this method in case they need the request or the
	 * edit part (which will of course not be set during creation) to calculate
	 * a layout constraint for the request.
	 * 
	 * @param rectangle
	 *            the Rectangle relative to the {@link #getLayoutOrigin() layout
	 *            origin}
	 * @return the constraint
	 * @since 3.7
	 */
	protected Object getConstraintFor(Request request, GraphicalEditPart child,
			Rectangle rectangle) {
		if (UNSPECIFIED_SIZE.equals(rectangle.getSize())) {
			return getConstraintFor(rectangle.getLocation());
		}
		return getConstraintFor(rectangle);
	}

	/**
	 * Generates a draw2d constraint for the given <code>CreateRequest</code> by
	 * delegating to
	 * {@link #getConstraintFor(Request, GraphicalEditPart, Rectangle)}.
	 * 
	 * If the CreateRequest has a size, is used during size-on-drop creation, a
	 * Rectangle of the request's location and size is passed with the
	 * delegation. Otherwise, a rectangle with the request's location and an
	 * empty size (0,0) is passed over.
	 * <P>
	 * The CreateRequest's location is relative to the Viewer. The location is
	 * made layout-relative by using
	 * {@link #translateFromAbsoluteToLayoutRelative(Translatable)} before
	 * calling {@link #getConstraintFor(Request, GraphicalEditPart, Rectangle)}.
	 * 
	 * @param request
	 *            the CreateRequest
	 * @return a draw2d constraint
	 */
	protected Object getConstraintFor(CreateRequest request) {
		Rectangle locationAndSize = null;
		if (request.getSize() == null || request.getSize().isEmpty()) {
			locationAndSize = new PrecisionRectangle(request.getLocation(),
					UNSPECIFIED_SIZE);
		} else {
			locationAndSize = new PrecisionRectangle(request.getLocation(),
					request.getSize());
		}
		translateFromAbsoluteToLayoutRelative(locationAndSize);
		return getConstraintFor(request, null, locationAndSize);
	}

	/**
	 * Generates a draw2d constraint given a <code>Point</code>. This method is
	 * called during creation, when only a mouse location is available, as well
	 * as during move, in case no resizing is involved.
	 * 
	 * @param point
	 *            the Point relative to the {@link #getLayoutOrigin() layout
	 *            origin}
	 * @return the constraint
	 */
	protected Object getConstraintFor(Point p) {
		return new Rectangle(p, PREFERRED_SIZE);
	}

	/**
	 * Generates a draw2d constraint given a <code>Rectangle</code>. This method
	 * is called during most operations.
	 * 
	 * @param rect
	 *            the Rectangle relative to the {@link #getLayoutOrigin() layout
	 *            origin}
	 * @return the constraint
	 */
	protected Object getConstraintFor(Rectangle r) {
		return new Rectangle(r);
	}

	/**
	 * Converts a constraint from the format used by LayoutManagers, to the form
	 * stored in the model.
	 * 
	 * @param figureConstraint
	 *            the draw2d constraint
	 * @return the model constraint
	 */
	protected Object translateToModelConstraint(Object figureConstraint) {
		return figureConstraint;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		BMSResizableEditPolicy policy = new BMSResizableEditPolicy();
		policy.setResizeDirections(PositionConstants.EAST
				| PositionConstants.WEST);
		return policy;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {

		BControl childModel = (BControl) child.getModel();
		BControl parentModel = (BControl) getHost().getModel();

		if ((childModel instanceof Light && parentModel instanceof Signal)) {
			int oldIndex = getHost().getChildren().indexOf(child);
			int newIndex = getHost().getChildren().indexOf(after);
			if (newIndex > oldIndex)
				newIndex--;
			ReorderPartCommand command = new ReorderPartCommand(childModel,
					parentModel, newIndex);
			return command;
		}

		return null;

	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {
		return null;
	}

	@Override
	protected boolean isHorizontal() {
		IFigure figure = getLayoutContainer();
		if (figure.getLayoutManager() instanceof ToolbarLayout) {
			return ((ToolbarLayout) figure.getLayoutManager()).isHorizontal();
		} else if (figure.getLayoutManager() instanceof FlowLayout) {
			return ((FlowLayout) figure.getLayoutManager()).isHorizontal();
		}
		return false;
	}

}
