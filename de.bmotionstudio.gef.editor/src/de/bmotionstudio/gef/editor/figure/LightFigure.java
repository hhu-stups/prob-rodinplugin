/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class LightFigure extends AbstractBMotionFigure {

	private Color lightColor;

	public LightFigure() {
		setOpaque(false);
	}

	@Override
	public void paint(Graphics g) {

		Rectangle r = getClientArea();

		g.setBackgroundColor(ColorConstants.lightGray);
		g.fillRectangle(r.x - 5 + r.width / 2, r.y, 11, 14);

		g.setBackgroundColor(lightColor);
		g.fillOval(r.x - 5 + r.width / 2, r.y + 1, 11, 11);

		g.setForegroundColor(ColorConstants.black);
		g.drawOval(r.x - 5 + r.width / 2, r.y + 1, 10, 10);

		super.paint(g);

	}

	public void setBackgroundColor(RGB rgb) {
		if (lightColor != null)
			lightColor.dispose();
		lightColor = new Color(Display.getDefault(), rgb);
		repaint();
	}

	@Override
	public void deactivateFigure() {
		if (lightColor != null)
			lightColor.dispose();
	}

}
