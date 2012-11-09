/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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
	private HashMap<BConnection, BConnection> mappingConnection = new HashMap<BConnection, BConnection>();

	private List<BControl> parentControls = new ArrayList<BControl>();

	@Override
	public boolean canExecute() {
		cHelper = (CopyPasteHelper) Clipboard.getDefault().getContents();
		if (cHelper == null)
			return false;
		ArrayList<BControl> myList = cHelper.getList();
		if (myList.isEmpty())
			return false;
		Iterator<?> it = myList.iterator();
		while (it.hasNext()) {
			BControl node = (BControl) it.next();
			if (isPastableControl(node)) {
				mappingControl.put(node, null);
			}
		}
		return true;
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

				// Copy/Paste controls
				Iterator<BControl> it = mappingControl.keySet().iterator();
				while (it.hasNext()) {
					BControl control = (BControl) it.next();
					control.setParent(parent);
					BControl clone = (BControl) control.clone();
					clone.setParent(parent);
					int x = Integer.valueOf(Integer.valueOf(clone
							.getAttributeValue(AttributeConstants.ATTRIBUTE_X)
							.toString()));
					int y = Integer.valueOf(Integer.valueOf(clone
							.getAttributeValue(AttributeConstants.ATTRIBUTE_Y)
							.toString()));
					clone.setAttributeValue(AttributeConstants.ATTRIBUTE_X, x
							+ cHelper.getDistance());
					clone.setAttributeValue(AttributeConstants.ATTRIBUTE_Y, y
							+ cHelper.getDistance());
					mappingControl.put(control, clone);
					cHelper.setDistance(cHelper.getDistance() + 10);
				}
				
				// Copy/Paste connections
				HashMap<BControl, BControl> helpMap = new HashMap<BControl, BControl>();
				helpMap.putAll(cHelper.getAlreadyClonedMap());
				helpMap.putAll(mappingControl);

				Iterator<BControl> it2 = helpMap.keySet().iterator();
				while (it2.hasNext()) {
					BControl control = it2.next();

					// Clone connections
					for (BConnection c : control.getSourceConnections()) {

						BConnection newConnection = mappingConnection.get(c);
						if (newConnection == null) {
							newConnection = (BConnection) c.clone();
							newConnection.disconnect();
							mappingConnection.put(c, newConnection);
						}

						BControl s = helpMap.get(newConnection
								.getSource());
						if (s == null)
							s = newConnection.getSource();
						BControl t = helpMap.get(newConnection
								.getTarget());
						if (t == null)
							t = newConnection.getTarget();

						newConnection.setTarget(t);
						newConnection.setSource(s);

					}

					for (BConnection c : control.getTargetConnections()) {

						BConnection newConnection = mappingConnection.get(c);
						if (newConnection == null) {
							newConnection = (BConnection) c.clone();
							newConnection.disconnect();
							mappingConnection.put(c, newConnection);
						}

						BControl t = helpMap.get(newConnection
								.getTarget());
						if (t == null)
							t = newConnection.getTarget();
						BControl s = helpMap.get(newConnection
								.getSource());
						if (s == null)
							s = newConnection.getSource();

						newConnection.setTarget(t);
						newConnection.setSource(s);

					}

				}

				redo();

			}

		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void redo() {

		Iterator<BControl> it = mappingControl.values().iterator();
		while (it.hasNext()) {
			BControl control = it.next();
			if (isPastableControl(control)) {
				control.getParent().addChild(control);
			}
		}

		Iterator<BConnection> it2 = mappingConnection.values().iterator();
		while (it2.hasNext()) {
			BConnection connection = it2.next();
			connection.reconnect();
		}

	}

	@Override
	public boolean canUndo() {
		return !(mappingControl.isEmpty());
	}

	@Override
	public void undo() {

		Iterator<BControl> it = mappingControl.values().iterator();
		while (it.hasNext()) {
			BControl bcontrol = it.next();
			if (isPastableControl(bcontrol)) {
				bcontrol.getParent().removeChild(bcontrol);
			}
		}

		Iterator<BConnection> it2 = mappingConnection.values().iterator();
		while (it2.hasNext()) {
			BConnection connection = it2.next();
			connection.disconnect();
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
