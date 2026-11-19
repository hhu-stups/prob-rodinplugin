/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControlPropertyConstants;
import de.bmotionstudio.gef.editor.model.BControl;

public abstract class AbstractBringToCommand extends Command {

	private List<BControl> controlList = new ArrayList<BControl>();
	private Map<BControl, Integer> oldIndexMap = new HashMap<BControl, Integer>();
	// parent ==> order of its children before execute(); with several
	// selected controls, restoring single indices gives a different order
	private final Map<BControl, List<BControl>> oldOrder = new LinkedHashMap<BControl, List<BControl>>();

	public void setControlList(List<BControl> controlList) {
		this.controlList = controlList;
	}

	public List<BControl> getControlList() {
		return controlList;
	}

	@Override
	public boolean canExecute() {
		if (controlList.isEmpty())
			return false;
		return true;
	}

	@Override
	public boolean canUndo() {
		if (controlList.isEmpty() || oldOrder.isEmpty())
			return false;
		return true;
	}

	/**
	 * To be called by execute() before changing the order
	 */
	protected void saveOrder() {
		oldOrder.clear();
		for (BControl control : controlList) {
			BControl parent = control.getParent();
			if (!oldOrder.containsKey(parent)) {
				oldOrder.put(parent,
						new ArrayList<BControl>(parent.getChildrenArray()));
			}
		}
	}

	@Override
	public void undo() {
		for (Map.Entry<BControl, List<BControl>> e : oldOrder.entrySet()) {
			BControl parent = e.getKey();
			parent.getChildrenArray().clear();
			parent.getChildrenArray().addAll(e.getValue());
			parent.getListeners().firePropertyChange(
					BControlPropertyConstants.PROPERTY_ADD_CHILD, null, null);
		}
	}

	public Map<BControl, Integer> getOldIndexMap() {
		return oldIndexMap;
	}

	public void setOldIndexMap(HashMap<BControl, Integer> oldIndexMap) {
		this.oldIndexMap = oldIndexMap;
	}

}
