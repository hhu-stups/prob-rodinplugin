/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class PasteCommand extends Command {

	private CopyPasteHelper cHelper;

	// List with mapping original BControl ==> cloned BControl
	private HashMap<BControl, BControl> mappingControl = new HashMap<BControl, BControl>();
	// the pasted clones, for all selected parents
	private final List<BControl> pastedControls = new ArrayList<BControl>();
	private final List<BConnection> pastedConnections = new ArrayList<BConnection>();

	private List<BControl> parentControls = new ArrayList<BControl>();

	@Override
	public boolean canExecute() {
		cHelper = (CopyPasteHelper) Clipboard.getDefault().getContents();
		if (cHelper == null)
			return false;
		// nothing to paste into, e.g. only controls that cannot have
		// children are selected
		if (parentControls.isEmpty())
			return false;
		for (BControl node : cHelper.getList()) {
			if (isPastableControl(node))
				return true;
		}
		return false;
	}

	public boolean addElement(BControl control) {
		if (!parentControls.contains(control)) {
			return parentControls.add(control);
		}
		return false;
	}

	public boolean isContainer(BControl control) {
		return control.canHaveChildren();
	}

	@Override
	public void execute() {

		if (!canExecute())
			return;

		try {

			for (BControl parent : parentControls) {

				// may be another editor than the one the controls were
				// copied from
				final Visualization visualization = parent.getVisualization();
				// original ==> clone, including the children of the pasted
				// controls
				final Map<BControl, BControl> cloned = new HashMap<BControl, BControl>();
				final List<BControl> pastedHere = new ArrayList<BControl>();

				// Copy/Paste controls; the originals are not changed, they
				// may be pasted again
				for (BControl control : cHelper.getList()) {
					if (!isPastableControl(control))
						continue;
					BControl clone = control.clone(visualization, cloned);
					clone.setParent(parent);
					int x = Integer.valueOf(clone.getAttributeValue(
							AttributeConstants.ATTRIBUTE_X).toString());
					int y = Integer.valueOf(clone.getAttributeValue(
							AttributeConstants.ATTRIBUTE_Y).toString());
					clone.setAttributeValue(AttributeConstants.ATTRIBUTE_X, x
							+ cHelper.getDistance());
					clone.setAttributeValue(AttributeConstants.ATTRIBUTE_Y, y
							+ cHelper.getDistance());
					cloned.put(control, clone);
					pastedHere.add(clone);
					mappingControl.put(control, clone);
					cHelper.setDistance(cHelper.getDistance() + 10);
				}
				pastedControls.addAll(pastedHere);

				// Copy/Paste connections
				final Map<BConnection, BConnection> connections = new HashMap<BConnection, BConnection>();
				for (BControl original : cloned.keySet()) {
					for (BConnection c : original.getSourceConnections())
						copyPasteConnection(c, cloned, visualization,
								connections);
					for (BConnection c : original.getTargetConnections())
						copyPasteConnection(c, cloned, visualization,
								connections);
				}
				pastedConnections.addAll(connections.values());

			}

			redo();

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	private void copyPasteConnection(BConnection c,
			Map<BControl, BControl> controlMap, Visualization visualization,
			Map<BConnection, BConnection> connections)
			throws CloneNotSupportedException {
		if (connections.containsKey(c))
			return;
		// a connection to a control that has not been copied stays attached
		// to that control, unless it belongs to another visualization
		BControl s = controlMap.get(c.getSource());
		if (s == null)
			s = c.getSource();
		BControl t = controlMap.get(c.getTarget());
		if (t == null)
			t = c.getTarget();
		if (s.getVisualization() != visualization
				|| t.getVisualization() != visualization)
			return;
		BConnection newConnection = (BConnection) c.clone(visualization, null);
		newConnection.setSource(s);
		newConnection.setTarget(t);
		connections.put(c, newConnection);
	}

	@Override
	public void redo() {

		for (BControl control : pastedControls) {
			control.getParent().addChild(control);
		}

		for (BConnection connection : pastedConnections) {
			connection.reconnect();
		}

	}

	@Override
	public boolean canUndo() {
		// only if something has been pasted
		return !pastedControls.isEmpty();
	}

	@Override
	public void undo() {

		for (BConnection connection : pastedConnections) {
			connection.disconnect();
		}

		for (BControl control : pastedControls) {
			control.getParent().removeChild(control);
		}

	}

	public boolean isPastableControl(BControl control) {
		if (control instanceof Visualization)
			return false;
		return true;
	}

	public HashMap<BControl, BControl> getList() {
		return this.mappingControl;
	}

}
