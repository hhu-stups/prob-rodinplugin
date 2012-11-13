/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;

public class TableFigure extends AbstractTableFigure {

	public TableFigure() {
		ToolbarLayout toolbarLayout = new ToolbarLayout();
		toolbarLayout.setHorizontal(true);
		setLayoutManager(toolbarLayout);
		setOpaque(false);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle r = getClientArea();
		g.setForegroundColor(getForegroundColor());
		// Right table border line
		g.drawLine(r.x + r.width - 21, r.y, r.x + r.width - 21, r.y + r.height
				- 15);
	}

}
