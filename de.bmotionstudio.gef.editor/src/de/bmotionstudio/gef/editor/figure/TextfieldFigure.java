/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GridLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class TextfieldFigure extends AbstractBMotionFigure {

	private Label lb;
	private Color foregroundColor;

	public TextfieldFigure() {
		GridLayout fl = new GridLayout();
		fl.marginHeight = 2;
		fl.marginWidth = 3;
		setLayoutManager(fl);
		foregroundColor = new Color(Display.getDefault(), 171, 173, 179);
		setBorder(new LineBorder(
				new Color(Display.getDefault(), 226, 227, 234), 1));
		this.lb = new Label();
		add(lb);
		setOpaque(true);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Rectangle r = Rectangle.SINGLETON;
		r.setBounds(getBounds());
		g.setForegroundColor(foregroundColor);
		g.drawLine(r.getTopLeft(), r.getTopRight());
	}

	public void setText(String text) {
		lb.setText(text);
	}

	public String getText() {
		return lb.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#deactivateFigure()
	 */
	@Override
	public void deactivateFigure() {
		if (foregroundColor != null)
			foregroundColor.dispose();
	}

}
