/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

public class TableColumnFigure extends AbstractTableFigure {

	public TableColumnFigure() {
		ToolbarLayout toolbarLayout = new ToolbarLayout();
		setLayoutManager(toolbarLayout);
		setOpaque(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle r = getClientArea();
		Color foregroundColor = getForegroundColor();
		if (foregroundColor != null)
			g.setForegroundColor(foregroundColor);
		// g.drawLine(r.x, r.y, r.x + r.width, r.y);
		// Left line
		g.drawLine(r.x, r.y, r.x, r.y + r.height - 15);
		// Bottom line
		g.drawLine(r.x, r.y + r.height - 15, r.x + r.width, r.y + r.height - 15);
	}

}
