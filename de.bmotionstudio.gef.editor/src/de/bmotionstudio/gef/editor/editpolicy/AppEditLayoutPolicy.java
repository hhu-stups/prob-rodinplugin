/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.ChangeBoundsRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeWidth;
import de.bmotionstudio.gef.editor.command.BControlChangeLayoutCommand;
import de.bmotionstudio.gef.editor.command.ChangeGuideCommand;
import de.bmotionstudio.gef.editor.command.CreateCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BMotionGuide;
import de.bmotionstudio.gef.editor.part.AppAbstractEditPart;

public class AppEditLayoutPolicy extends XYLayoutEditPolicy {

	@Override
	protected Command createChangeConstraintCommand(EditPart child,
			Object constraint) {
		return null;
	}

	@Override
	protected Command createChangeConstraintCommand(
			ChangeBoundsRequest request, EditPart child, Object constraint) {

		BControlChangeLayoutCommand cmd = new BControlChangeLayoutCommand();
		BControl part = (BControl) child.getModel();
		cmd.setModel(child.getModel());
		cmd.setConstraint((Rectangle) constraint);
		Command result = cmd;

		if ((request.getResizeDirection() & PositionConstants.NORTH_SOUTH) != 0) {
			Integer guidePos = (Integer) request.getExtendedData().get(
					SnapToGuides.KEY_HORIZONTAL_GUIDE);
			if (guidePos != null) {
				result = chainGuideAttachmentCommand(request, part, result,
						true);
			} else if (part.getHorizontalGuide() != null) {
				// SnapToGuides didn't provide a horizontal guide, but this part
				// is attached
				// to a horizontal guide. Now we check to see if the part is
				// attached to
				// the guide along the edge being resized. If that is the case,
				// we need to
				// detach the part from the guide; otherwise, we leave it alone.
				int alignment = part.getHorizontalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.NORTH) != 0)
					edgeBeingResized = -1;
				else
					edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
					result = result.chain(new ChangeGuideCommand(part, true));
			}
		}

		if ((request.getResizeDirection() & PositionConstants.EAST_WEST) != 0) {
			Integer guidePos = (Integer) request.getExtendedData().get(
					SnapToGuides.KEY_VERTICAL_GUIDE);
			if (guidePos != null) {
				result = chainGuideAttachmentCommand(request, part, result,
						false);
			} else if (part.getVerticalGuide() != null) {
				int alignment = part.getVerticalGuide().getAlignment(part);
				int edgeBeingResized = 0;
				if ((request.getResizeDirection() & PositionConstants.WEST) != 0)
					edgeBeingResized = -1;
				else
					edgeBeingResized = 1;
				if (alignment == edgeBeingResized)
					result = result.chain(new ChangeGuideCommand(part, false));
			}
		}

		if (request.getType().equals(REQ_MOVE_CHILDREN)
				|| request.getType().equals(REQ_ALIGN_CHILDREN)) {
			result = chainGuideAttachmentCommand(request, part, result, true);
			result = chainGuideAttachmentCommand(request, part, result, false);
			result = chainGuideDetachmentCommand(request, part, result, true);
			result = chainGuideDetachmentCommand(request, part, result, false);
		}

		return result;
	}

	@Override
	protected EditPolicy createChildEditPolicy(EditPart child) {

		BControl control = (BControl) child.getModel();

		ResizableEditPolicy policy = new ResizableEditPolicy();

		BAttributeWidth atrWidth = (BAttributeWidth) control.getAttributes()
				.get(AttributeConstants.ATTRIBUTE_WIDTH);
		BAttributeHeight atrHeight = (BAttributeHeight) control.getAttributes()
				.get(AttributeConstants.ATTRIBUTE_HEIGHT);

		if (atrWidth.isEditable() && atrHeight.isEditable())
			return policy;

		if (atrWidth.isEditable()) {
			policy.setResizeDirections(PositionConstants.EAST_WEST);
			return policy;
		}

		if (atrHeight.isEditable()) {
			policy.setResizeDirections(PositionConstants.NORTH_SOUTH);
			return policy;
		}

		policy.setResizeDirections(0);

		return policy;

	}

	@Override
	protected Command getCreateCommand(CreateRequest request) {

		if (request.getType() == REQ_CREATE
				&& getHost() instanceof AppAbstractEditPart) {

			if (((BControl) ((AppAbstractEditPart) getHost()).getModel())
					.canHaveChildren()) {

				BControl newObj = (BControl) request.getNewObject();

				CreateCommand createCmd = new CreateCommand(newObj,
						(BControl) getHost().getModel());

				Rectangle constraint = (Rectangle) getConstraintFor(request);
				constraint.x = (constraint.x < 0) ? 0 : constraint.x;
				constraint.y = (constraint.y < 0) ? 0 : constraint.y;

				BAttributeWidth atrWidth = (BAttributeWidth) newObj
						.getAttributes()
						.get(AttributeConstants.ATTRIBUTE_WIDTH);
				BAttributeHeight atrHeight = (BAttributeHeight) newObj
						.getAttributes().get(
								AttributeConstants.ATTRIBUTE_HEIGHT);

				if (atrWidth != null && !atrWidth.isEditable()) {
					constraint.width = Integer.valueOf(atrWidth.getValue()
							.toString());
				} else {
					constraint.width = (constraint.width <= 0) ? 100
							: constraint.width;
				}

				if (atrHeight != null && !atrHeight.isEditable()) {
					constraint.height = Integer.valueOf(atrHeight.getValue()
							.toString());
				} else {
					constraint.height = (constraint.height <= 0) ? 100
							: constraint.height;
				}

				createCmd.setLayout(constraint);

				Command cmd = chainGuideAttachmentCommand(request, newObj,
						createCmd, true);
				return chainGuideAttachmentCommand(request, newObj, cmd, false);

			}

		}

		return null;

	}

	protected Command chainGuideAttachmentCommand(Request request,
			BControl part, Command cmd, boolean horizontal) {
		Command result = cmd;

		// Attach to guide, if one is given
		Integer guidePos = (Integer) request.getExtendedData().get(
				horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
						: SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos != null) {
			int alignment = ((Integer) request.getExtendedData().get(
					horizontal ? SnapToGuides.KEY_HORIZONTAL_ANCHOR
							: SnapToGuides.KEY_VERTICAL_ANCHOR)).intValue();
			ChangeGuideCommand cgm = new ChangeGuideCommand(part, horizontal);
			cgm.setNewGuide(findGuideAt(guidePos.intValue(), horizontal),
					alignment);
			result = result.chain(cgm);
		}

		return result;
	}

	protected Command chainGuideDetachmentCommand(Request request,
			BControl part, Command cmd, boolean horizontal) {
		Command result = cmd;

		// Detach from guide, if none is given
		Integer guidePos = (Integer) request.getExtendedData().get(
				horizontal ? SnapToGuides.KEY_HORIZONTAL_GUIDE
						: SnapToGuides.KEY_VERTICAL_GUIDE);
		if (guidePos == null)
			result = result.chain(new ChangeGuideCommand(part, horizontal));

		return result;
	}

	protected BMotionGuide findGuideAt(int pos, boolean horizontal) {
		RulerProvider provider = ((RulerProvider) getHost().getViewer()
				.getProperty(
						horizontal ? RulerProvider.PROPERTY_VERTICAL_RULER
								: RulerProvider.PROPERTY_HORIZONTAL_RULER));
		return (BMotionGuide) provider.getGuideAt(pos);
	}

}
