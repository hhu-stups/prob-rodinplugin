/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import java.util.Iterator;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.BMotionGuide;

public class MoveGuideCommand extends Command {

	private int pDelta;
	private BMotionGuide guide;

	public MoveGuideCommand(BMotionGuide guide, int positionDelta) {
		super("Move Guide");
		this.guide = guide;
		pDelta = positionDelta;
	}

	public void execute() {

		guide.setPosition(guide.getPosition() + pDelta);

		Iterator<BControl> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			BControl part = (BControl) iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y += pDelta;
			} else {
				location.x += pDelta;
			}
			part.setLocation(location);
		}

	}

	public void undo() {
		guide.setPosition(guide.getPosition() - pDelta);
		Iterator<BControl> iter = guide.getParts().iterator();
		while (iter.hasNext()) {
			BControl part = (BControl) iter.next();
			Point location = part.getLocation().getCopy();
			if (guide.isHorizontal()) {
				location.y -= pDelta;
			} else {
				location.x -= pDelta;
			}
			part.setLocation(location);
		}
	}

}
