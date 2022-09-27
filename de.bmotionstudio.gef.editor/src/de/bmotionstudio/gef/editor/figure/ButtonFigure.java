/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ButtonBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class ButtonFigure extends AbstractBMotionFigure {

	private Label txtLabel;
	private Color backgroundColor;
	private Color foregroundColor;

	public ButtonFigure() {
		setLayoutManager(new StackLayout());
		txtLabel = new Label("Click");
		add(txtLabel);
		setOpaque(true);
		setBorder(new ButtonBorder());
	}

	public void setText(String text) {
		txtLabel.setText(text);
	}

	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null)
			backgroundColor.dispose();
		backgroundColor = new Color(Display.getDefault(), rgb);
		setBackgroundColor(backgroundColor);
	}

	public void setTextColor(RGB rgb) {
		if (foregroundColor != null)
			foregroundColor.dispose();
		foregroundColor = new Color(Display.getDefault(), rgb);
		setForegroundColor(foregroundColor);
	}

	public void setBtEnabled(Boolean bool) {
		txtLabel.setEnabled(bool);
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#deactivateFigure()
	 */
	@Override
	public void deactivateFigure() {
		if (backgroundColor != null)
			backgroundColor.dispose();
		if (foregroundColor != null)
			foregroundColor.dispose();
	}

	@Override
	protected void paintBorder(Graphics graphics) {
		super.paintBorder(graphics);
		if (hasFocus()) {
			graphics.setForegroundColor(ColorConstants.black);
			graphics.setBackgroundColor(ColorConstants.white);
			Rectangle area = getClientArea();
			graphics.drawFocus(area.x, area.y, area.width, area.height);
		}
	}

	@Override
	protected void paintClientArea(Graphics graphics) {
		graphics.translate(1, 1);
		graphics.pushState();
		super.paintClientArea(graphics);
		graphics.popState();
		graphics.translate(-1, -1);
	}

}
