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

import de.bmotionstudio.gef.editor.model.BControl;

public abstract class AbstractBringToCommand extends Command {

	private List<BControl> controlList = new ArrayList<BControl>();
	private Map<BControl, Integer> oldIndexMap = new HashMap<BControl, Integer>();

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
		if (controlList.isEmpty() || oldIndexMap.isEmpty())
			return false;
		return true;
	}

	public Map<BControl, Integer> getOldIndexMap() {
		return oldIndexMap;
	}

	public void setOldIndexMap(HashMap<BControl, Integer> oldIndexMap) {
		this.oldIndexMap = oldIndexMap;
	}

}
