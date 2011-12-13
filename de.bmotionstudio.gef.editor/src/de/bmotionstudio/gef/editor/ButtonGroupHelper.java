/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.bmotionstudio.gef.editor.model.BControl;

public class ButtonGroupHelper {

	private static HashMap<String, Collection<BControl>> map = new HashMap<String, Collection<BControl>>();

	public ButtonGroupHelper() {
	}

	public static Collection<BControl> getButtonGroup(String buttonGroupID) {
		return map.get(buttonGroupID);
	}

	public static void addToButtonGroup(String buttonGroupID, BControl control) {
		Collection<BControl> group;
		if (!map.containsKey(buttonGroupID)) {
			group = new ArrayList<BControl>();
			map.put(buttonGroupID, group);
		} else {
			group = map.get(buttonGroupID);
		}
		group.add(control);
	}

	public static void removeButtonGroup(String buttonGroupID) {
		map.remove(buttonGroupID);
	}

	public static void reset() {
		map.clear();
	}

}
