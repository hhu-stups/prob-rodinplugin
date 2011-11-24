/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BMotionGuide;
import de.bmotionstudio.gef.editor.model.BMotionRuler;

public class CreateGuideCommand extends Command {

	private BMotionGuide guide;
	private BMotionRuler parent;
	private int position;

	public CreateGuideCommand(BMotionRuler parent, int position) {
		super("Create Guide");
		this.parent = parent;
		this.position = position;
	}

	public boolean canUndo() {
		return true;
	}

	public void execute() {
		if (guide == null)
			guide = new BMotionGuide(!parent.isHorizontal());
		guide.setPosition(position);
		parent.addGuide(guide);
	}

	public void undo() {
		parent.removeGuide(guide);
	}

}
