/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.bmotionstudio.gef.editor.command.ConnectionDeleteCommand;
import de.bmotionstudio.gef.editor.model.Track;

public class TrackPart extends BConnectionEditPart {

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
						return new ConnectionDeleteCommand((Track) getModel());
					}
				});
	}

	@Override
	protected ConnectionAnchor getSourceConnectionAnchor() {
		if (getSource() != null) {
			IFigure f = ((GraphicalEditPart) getSource()).getFigure();
			return new ChopboxAnchor(f) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle r = Rectangle.SINGLETON;
					r.setBounds(getBox());
					r.translate(-1, -1);
					r.resize(1, 1);
					getOwner().translateToAbsolute(r);
					float centerX = r.x + 0.5f * r.width;
					float centerY = r.y + 0.5f * r.height;
					if (r.isEmpty()
							|| (reference.x == (int) centerX && reference.y == (int) centerY))
						return new Point((int) centerX, (int) centerY); // This
																		// avoids
																		// divide-by-zero
					return new Point(Math.round(centerX), Math.round(centerY));
				}
			};
		}
		return DEFAULT_SOURCE_ANCHOR;
	}

	@Override
	protected ConnectionAnchor getTargetConnectionAnchor() {
		if (getTarget() != null) {
			IFigure f = ((GraphicalEditPart) getTarget()).getFigure();
			return new ChopboxAnchor(f) {
				@Override
				public Point getLocation(Point reference) {
					Rectangle r = Rectangle.SINGLETON;
					r.setBounds(getBox());
					r.translate(-1, -1);
					r.resize(1, 1);
					getOwner().translateToAbsolute(r);
					float centerX = r.x + 0.5f * r.width;
					float centerY = r.y + 0.5f * r.height;
					if (r.isEmpty()
							|| (reference.x == (int) centerX && reference.y == (int) centerY))
						return new Point((int) centerX, (int) centerY); // This
																		// avoids
																		// divide-by-zero
					return new Point(Math.round(centerX), Math.round(centerY));
				}
			};
		}
		return DEFAULT_TARGET_ANCHOR;
	}

}
