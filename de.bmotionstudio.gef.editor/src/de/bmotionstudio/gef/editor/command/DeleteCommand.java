/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;

public class DeleteCommand extends Command {

	private BControl control;
	private BControl parent;
	private int index = -1;
	private final List<BConnection> connections = new ArrayList<BConnection>();

	public DeleteCommand(BControl control, BControl parent) {
		this.control = control;
		this.parent = parent;
	}

	@Override
	public boolean canExecute() {
		if (parent == null || control == null || !parent.contains(control))
			return false;
		return true;
	}

	@Override
	public boolean canUndo() {
		if (parent == null || control == null)
			return false;
		return !parent.contains(control);
	}

	@Override
	public void execute() {
		// restore the position in the stacking order on undo
		index = parent.getChildrenArray().indexOf(control);
		// connections of the control and of its children would otherwise
		// still be shown and saved, attached to a deleted control
		connections.clear();
		collectConnections(control);
		for (BConnection connection : connections) {
			connection.disconnect();
		}
		this.parent.removeChild(control);
	}

	private void collectConnections(final BControl c) {
		for (BConnection connection : c.getSourceConnections()) {
			if (!connections.contains(connection))
				connections.add(connection);
		}
		for (BConnection connection : c.getTargetConnections()) {
			if (!connections.contains(connection))
				connections.add(connection);
		}
		for (BControl child : c.getChildrenArray()) {
			collectConnections(child);
		}
	}

	@Override
	public void undo() {
		this.parent.addChild(control, index);
		for (BConnection connection : connections) {
			connection.reconnect();
		}
	}

}
