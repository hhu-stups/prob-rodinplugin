/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class TableCellFigure extends AbstractTableFigure {

	private String text;
	private Color backgroundColor;
	private Color textColor;

	public TableCellFigure() {
		setOpaque(true);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle r = getClientArea();
		g.setBackgroundColor(backgroundColor);
		g.fillRectangle(r.x, r.y, r.x + r.width, r.y + r.height);
		g.setForegroundColor(textColor);
		g.drawText(text, r.x + 3, r.y + 3);
		Color foregroundColor = getForegroundColor();
		if (foregroundColor != null)
			g.setForegroundColor(foregroundColor);
		// Bottom cell line
		g.drawLine(r.x, r.y, r.x + r.width, r.y);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		repaint();
	}

	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null)
			backgroundColor.dispose();
		backgroundColor = new Color(Display.getDefault(), rgb);
		repaint();
	}

	public void setTextColor(RGB rgb) {
		if (textColor != null)
			textColor.dispose();
		textColor = new Color(Display.getDefault(), rgb);
		repaint();
	}

	@Override
	public void deactivateFigure() {
		if (backgroundColor != null)
			backgroundColor.dispose();
		if (textColor != null)
			textColor.dispose();
		super.deactivateFigure();
	}

	public void setFont(String string) {
		// TODO Auto-generated method stub
	}

}
