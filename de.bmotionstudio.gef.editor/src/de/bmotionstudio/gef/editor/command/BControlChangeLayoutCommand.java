/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.model.BControl;

public class BControlChangeLayoutCommand extends AbstractLayoutCommand {

	private BControl control;
	private Rectangle newLayout;
	private Rectangle oldLayout;

	@Override
	public void execute() {
		control.setLayout(newLayout);
	}

	@Override
	public void setConstraint(Rectangle rect) {
		newLayout = rect;
	}

	@Override
	public void setModel(Object model) {
		control = (BControl) model;
		oldLayout = control.getLayout();
	}

	@Override
	public void undo() {
		control.setLayout(oldLayout);
	}

}
