/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BMotionGuide;

public class ChangeGuideCommand extends Command {

	private BControl part;
	private BMotionGuide oldGuide, newGuide;
	private int oldAlign, newAlign;
	private boolean horizontal;

	public ChangeGuideCommand(BControl part, boolean horizontalGuide) {
		super();
		this.part = part;
		horizontal = horizontalGuide;
	}

	protected void changeGuide(BMotionGuide oldGuide, BMotionGuide newGuide,
			int newAlignment) {

		if (oldGuide != null && oldGuide != newGuide) {
			oldGuide.detachPart(part);
		}
		// You need to re-attach the part even if the oldGuide and the
		// newGuide
		// are the same
		// because the alignment could have changed
		if (newGuide != null) {
			newGuide.attachPart(part, newAlignment);
		}

	}

	public void execute() {
		// Cache the old values
		oldGuide = horizontal ? part.getHorizontalGuide() : part
				.getVerticalGuide();
		if (oldGuide != null)
			oldAlign = oldGuide.getAlignment(part);

		redo();
	}

	public void redo() {
		changeGuide(oldGuide, newGuide, newAlign);
	}

	public void setNewGuide(BMotionGuide guide, int alignment) {
		newGuide = guide;
		newAlign = alignment;
	}

	public void undo() {
		changeGuide(newGuide, oldGuide, oldAlign);
	}

}
