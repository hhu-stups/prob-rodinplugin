/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.HashMap;

import org.eclipse.jface.action.Action;

public class AbstractInstallActions {

	HashMap<String, Action> map;

	public AbstractInstallActions() {
		this.map = new HashMap<String, Action>();
	}

	public void installAction(String actionID, Action action) {
		this.map.put(actionID, action);
	}

	public HashMap<String, Action> getActionMap() {
		return this.map;
	}

}
