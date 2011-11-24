/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BMotionGuide;
import de.bmotionstudio.gef.editor.model.BMotionRuler;

public class DeleteGuideCommand extends Command {

	private BMotionRuler parent;
	private BMotionGuide guide;
	private HashMap<BControl, Integer> oldParts;

	public DeleteGuideCommand(BMotionGuide guide, BMotionRuler parent) {
		super("Delete Guide");
		this.guide = guide;
		this.parent = parent;
	}

	public boolean canUndo() {
		return true;
	}

	public void execute() {
		oldParts = new HashMap<BControl, Integer>(guide.getMap());
		Iterator<BControl> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			guide.detachPart((BControl) iter.next());
		}
		parent.removeGuide(guide);
	}

	public void undo() {
		parent.addGuide(guide);
		Iterator<BControl> iter = oldParts.keySet().iterator();
		while (iter.hasNext()) {
			BControl part = (BControl) iter.next();
			guide.attachPart(part, ((Integer) oldParts.get(part)).intValue());
		}
	}

}
