/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.FlowLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import de.bmotionstudio.gef.editor.command.AddCommand;
import de.bmotionstudio.gef.editor.command.ReorderPartCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BTable;
import de.bmotionstudio.gef.editor.model.BTableCell;
import de.bmotionstudio.gef.editor.model.BTableColumn;

public class BMotionStudioFlowEditPolicy extends FlowLayoutEditPolicy {

	@Override
	protected Command createAddCommand(EditPart child, EditPart after) {

		BControl childModel = (BControl) child.getModel();
		BControl parentModel = (BControl) getHost().getModel();
		if ((childModel instanceof BTableColumn && parentModel instanceof BTable)
				|| (childModel instanceof BTableCell
						&& parentModel instanceof BTableColumn && childModel
						.getParent().equals(parentModel))) {
			AddCommand command = new AddCommand();
			command.setChild(childModel);
			command.setParent(parentModel);
			int index = getHost().getChildren().indexOf(after);
			command.setIndex(index);
			return command;
		}
		return null;
	}

	/**
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#createChildEditPolicy(org.eclipse.gef.EditPart)
	 */
	protected EditPolicy createChildEditPolicy(EditPart child) {
		BMotionStudioResizableEditPolicy policy = new BMotionStudioResizableEditPolicy();
		policy.setResizeDirections(PositionConstants.EAST
				| PositionConstants.WEST);
		return policy;
	}

	@Override
	protected Command createMoveChildCommand(EditPart child, EditPart after) {


		BControl childModel = (BControl) child.getModel();
		BControl parentModel = (BControl) getHost().getModel();

		if ((childModel instanceof BTableColumn && parentModel instanceof BTable)
				|| (childModel instanceof BTableCell && parentModel instanceof BTableColumn)) {
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
		// CreateCommand command = new CreateCommand(
		// (BControl) request.getNewObject(), (BControl) getHost()
		// .getModel());
		// EditPart after = getInsertionReference(request);
		// command.setLayout(new Rectangle(0, 0, 100, 25));
		// int index = getHost().getChildren().indexOf(after);
		// command.setIndex(index);
		// return command;
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

	@Override
	public void showLayoutTargetFeedback(Request request) {
		if (request instanceof CreateRequest) {
			Object newObject = ((CreateRequest) request).getNewObject();
			if (!(newObject instanceof BTableColumn || newObject instanceof BTableCell))
				return;
		}
		super.showLayoutTargetFeedback(request);
	}

}
