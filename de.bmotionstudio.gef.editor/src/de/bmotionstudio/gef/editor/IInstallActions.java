/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.HashMap;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.part.WorkbenchPart;

public interface IInstallActions {

	public void installActions(WorkbenchPart part);

	public HashMap<String, Action> getActionMap();

}
