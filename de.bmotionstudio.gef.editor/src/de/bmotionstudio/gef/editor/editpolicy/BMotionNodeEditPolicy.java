/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.editpolicy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.bmotionstudio.gef.editor.command.ConnectionCreateCommand;
import de.bmotionstudio.gef.editor.command.ConnectionReconnectCommand;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;

public class BMotionNodeEditPolicy extends GraphicalNodeEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getConnectionCompleteCommand
	 * (org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		ConnectionCreateCommand cmd = (ConnectionCreateCommand) request
				.getStartCommand();
		cmd.setTarget((BControl) getHost().getModel());
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getConnectionCreateCommand
	 * (org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		BControl source = (BControl) getHost().getModel();
		ConnectionCreateCommand cmd = new ConnectionCreateCommand(source);
		BConnection con = (BConnection) request.getNewObject();
		con.setVisualization(source.getVisualization());
		cmd.setConnection(con);
		request.setStartCommand(cmd);
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getReconnectSourceCommand (org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		BConnection conn = (BConnection) request.getConnectionEditPart()
				.getModel();
		BControl newSource = (BControl) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand();
		cmd.setNewSource(newSource);
		cmd.setConnection(conn);
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getReconnectTargetCommand (org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		BConnection conn = (BConnection) request.getConnectionEditPart()
				.getModel();
		BControl newTarget = (BControl) getHost().getModel();
		ConnectionReconnectCommand cmd = new ConnectionReconnectCommand();
		cmd.setNewTarget(newTarget);
		cmd.setConnection(conn);
		return cmd;
	}

}
