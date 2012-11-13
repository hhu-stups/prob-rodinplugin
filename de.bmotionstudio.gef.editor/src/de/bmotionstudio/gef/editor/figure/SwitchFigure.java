/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class SwitchFigure extends AbstractBMotionFigure {

	Color borderColor = new Color(Display.getDefault(), new RGB(235, 235, 235));

	public SwitchFigure() {
		setLayoutManager(new XYLayout());
		setOpaque(false);
	}

	@Override
	protected void paintBorder(Graphics g) {
		Rectangle r = getClientArea();
		if (!this.visible && !isRunning())
			g.setAlpha(255);
		g.setForegroundColor(borderColor);
		g.setLineStyle(SWT.LINE_DASH);
		r.height += -1;
		r.width += -1;
		g.drawRectangle(r);
		super.paintBorder(g);
	}

	@Override
	public void deactivateFigure() {
		borderColor.dispose();
	}

}
