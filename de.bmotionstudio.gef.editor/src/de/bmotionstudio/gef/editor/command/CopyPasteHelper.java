/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.bmotionstudio.gef.editor.model.BControl;

public class CopyPasteHelper {

	private ArrayList<BControl> list = new ArrayList<BControl>();
	private Map<BControl, BControl> alreadyCloned = new HashMap<BControl, BControl>();
	private int distance = 10;

	public CopyPasteHelper(ArrayList<BControl> list, int distance) {
		this.list = list;
		this.setDistance(distance);
	}

	public void setList(ArrayList<BControl> list) {
		this.list = list;
	}

	public ArrayList<BControl> getList() {
		return list;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getDistance() {
		return distance;
	}

	public Map<BControl, BControl> getAlreadyClonedMap() {
		return alreadyCloned;
	}

}
