/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.ui.actions.Clipboard;

import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class CopyCommand extends Command {

	private ArrayList<BControl> list = new ArrayList<BControl>();

	public boolean addElement(BControl control) {
		if (!list.contains(control)) {
			return list.add(control);
		}
		return false;
	}

	@Override
	public boolean canExecute() {
		if (list == null || list.isEmpty())
			return false;
		Iterator<BControl> it = list.iterator();
		while (it.hasNext()) {
			if (!isCopyableControl(it.next()))
				return false;
		}
		return true;
	}

	@Override
	public void execute() {
		if (canExecute()) {
			Clipboard.getDefault().setContents(new CopyPasteHelper(list, 10));
		}
	}

	@Override
	public boolean canUndo() {
		return false;
	}

	public boolean isCopyableControl(BControl control) {
		if (!(control instanceof Visualization)
				&& !(control instanceof BConnection))
			return true;
		return false;
	}

}
