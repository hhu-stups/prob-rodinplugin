/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Lukas Ladenberger
 * 
 */
public class AbstractBMotionFigure extends Clickable {

	private boolean visible = true;
	private boolean isRunning = false;

	public void deactivateFigure() {
	}

	public void activateFigure() {
	}

	@Override
	public void setVisible(boolean visible) {
		if (!isRunning()) {
			this.visible = visible;
			repaint();
		} else {
			super.setVisible(visible);
		}
	}

	@Override
	public void paint(Graphics g) {

		if (!this.visible && !isRunning) {

			Rectangle r = getClientArea();

			g.setForegroundColor(ColorConstants.lightGray);
			g.setLineStyle(Graphics.LINE_DOT);
			g.drawRectangle(r.x, r.y, r.width - 1, r.height - 1);

			g.setAlpha(15);

		}

		super.paint(g);

	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

}
