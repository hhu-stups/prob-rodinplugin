/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.bmotionstudio.gef.editor.command.ConnectionCreateCommand;
import de.bmotionstudio.gef.editor.command.ConnectionReconnectCommand;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.Track;
import de.bmotionstudio.gef.editor.model.TrackNode;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;

public class TrackEditPolicy extends BMSConnectionEditPolicy {

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {

		Command cmd = null;

		Object newObject = request.getNewObject();
		if (newObject instanceof Track) {

			Object model = getHost().getModel();
			if (model instanceof TrackNode) {

				Track track = (Track) newObject;
				TrackNode trackNode = (TrackNode) model;
				cmd = new ConnectionCreateCommand(trackNode);
				track.setVisualization(trackNode.getVisualization());
				((ConnectionCreateCommand) cmd).setConnection(track);
				request.setStartCommand(cmd);

			}

		} else if (newObject instanceof BConnection) {
			cmd = super.getConnectionCreateCommand(request);
		}

		return cmd;

	}

	@Override
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {

		Command cmd = null;

		Object newObject = request.getNewObject();
		if (newObject instanceof Track) {

			cmd = request.getStartCommand();
			((ConnectionCreateCommand) cmd).setTarget((TrackNode) getHost()
					.getModel());

		} else if (newObject instanceof BConnection) {
			cmd = super.getConnectionCompleteCommand(request);
		}

		return cmd;

	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {

		Command cmd = null;

		Object newObject = request.getConnectionEditPart().getModel();
		if (newObject instanceof Track) {

			Track track = (Track) newObject;
			TrackNode newSource = (TrackNode) getHost().getModel();
			cmd = new ConnectionReconnectCommand();
			((ConnectionReconnectCommand) cmd).setNewSource(newSource);
			((ConnectionReconnectCommand) cmd).setConnection(track);

		} else if (newObject instanceof BConnection) {
			cmd = super.getReconnectSourceCommand(request);
		}

		return cmd;

	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {

		Command cmd = null;

		Object newObject = request.getConnectionEditPart().getModel();
		if (newObject instanceof Track) {

			Track track = (Track) newObject;
			TrackNode newTarget = (TrackNode) getHost().getModel();
			cmd = new ConnectionReconnectCommand();
			((ConnectionReconnectCommand) cmd).setNewTarget(newTarget);
			((ConnectionReconnectCommand) cmd).setConnection(track);

		} else if (newObject instanceof BConnection) {
			cmd = super.getReconnectTargetCommand(request);
		}

		return cmd;

	}

	@Override
	protected void showTargetConnectionFeedback(DropRequest request) {
		if (getHost() instanceof BMSAbstractEditPart) {
			BMSAbstractEditPart host = (BMSAbstractEditPart) getHost();
			host.getFigure().setBackgroundColor(ColorConstants.lightGray);
		}
	}

	@Override
	protected void eraseTargetConnectionFeedback(DropRequest request) {
		if (getHost() instanceof BMSAbstractEditPart) {
			BMSAbstractEditPart host = (BMSAbstractEditPart) getHost();
			host.getFigure().setBackgroundColor(ColorConstants.white);
		}
	}

}
