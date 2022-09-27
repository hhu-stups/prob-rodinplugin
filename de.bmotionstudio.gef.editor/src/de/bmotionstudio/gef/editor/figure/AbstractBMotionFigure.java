/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Clickable;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;

/**
 * @author Lukas Ladenberger
 * 
 */
public class AbstractBMotionFigure extends Clickable {

	protected boolean visible;
	protected boolean isRunning;
	public static final int HIDDEN_ALPHA_VALUE = 35;

	public AbstractBMotionFigure() {
		this.visible = true;
		this.isRunning = false;
	}

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
		Rectangle clientArea = getClientArea();
		if (!this.visible && !isRunning()) {
			g.drawImage(BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_CONTROL_HIDDEN),
					clientArea.x, clientArea.y);
			g.setAlpha(HIDDEN_ALPHA_VALUE);
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
