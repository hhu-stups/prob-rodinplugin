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

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.model.BControl;

public class FitImageCommand extends Command {

	private List<BControl> modelList = new ArrayList<BControl>();
	private Map<BControl, Rectangle> oldSizeMap = new HashMap<BControl, Rectangle>();
	private Map<BControl, Rectangle> newSizeMap = new HashMap<BControl, Rectangle>();

	@Override
	public boolean canExecute() {
		return check();
	}

	@Override
	public void execute() {
		for (BControl control : modelList) {
			oldSizeMap.put(control, control.getLayout());
			control.setLayout(newSizeMap.get(control));
		}
	}

	@Override
	public boolean canUndo() {
		if (oldSizeMap.isEmpty())
			return false;
		return check();
	}

	@Override
	public void undo() {
		for (BControl control : this.modelList) {
			control.setLayout(oldSizeMap.get(control));
		}
	}

	public void setModelList(List<BControl> modelList) {
		this.modelList = modelList;
	}

	public void setNewSizeMap(Map<BControl, Rectangle> newSizeMap) {
		this.newSizeMap = newSizeMap;
	}

	private boolean check() {
		if (modelList.isEmpty() || newSizeMap.isEmpty())
			return false;
		for (BControl control : modelList) {
			if (!control.hasAttribute(AttributeConstants.ATTRIBUTE_IMAGE))
				return false;
		}
		return true;
	}

}
