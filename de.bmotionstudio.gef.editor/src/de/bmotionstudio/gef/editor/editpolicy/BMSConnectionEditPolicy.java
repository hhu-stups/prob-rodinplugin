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

public class BMSConnectionEditPolicy extends GraphicalNodeEditPolicy {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getConnectionCompleteCommand
	 * (org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		ConnectionCreateCommand cmd = null;
		Object newObject = request.getNewObject();
		if (newObject instanceof BConnection) {
			cmd = (ConnectionCreateCommand) request.getStartCommand();
			cmd.setTarget((BControl) getHost().getModel());
		}
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
		ConnectionCreateCommand cmd = null;
		Object newObject = request.getNewObject();
		if (newObject instanceof BConnection) {
			BControl source = (BControl) getHost().getModel();
			cmd = new ConnectionCreateCommand(source);
			BConnection con = (BConnection) newObject;
			con.setVisualization(source.getVisualization());
			cmd.setConnection(con);
			request.setStartCommand(cmd);
		}
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getReconnectSourceCommand (org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		ConnectionReconnectCommand cmd = null;
		Object newObject = request.getConnectionEditPart().getModel();
		if (newObject instanceof BConnection) {
			BConnection conn = (BConnection) newObject;
			BControl newSource = (BControl) getHost().getModel();
			cmd = new ConnectionReconnectCommand();
			cmd.setNewSource(newSource);
			cmd.setConnection(conn);
		}
		return cmd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#
	 * getReconnectTargetCommand (org.eclipse.gef.requests.ReconnectRequest)
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		ConnectionReconnectCommand cmd = null;
		Object newObject = request.getConnectionEditPart().getModel();
		if (newObject instanceof BConnection) {
			BConnection conn = (BConnection) newObject;
			BControl newTarget = (BControl) getHost().getModel();
			cmd = new ConnectionReconnectCommand();
			cmd.setNewTarget(newTarget);
			cmd.setConnection(conn);
		}
		return cmd;
	}

}
