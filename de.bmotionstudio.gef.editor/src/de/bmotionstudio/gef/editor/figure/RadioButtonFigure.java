/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionStudioSWTConstants;

public class RadioButtonFigure extends AbstractBMotionFigure {

	private Label textLb;
	private ImageFigure radioBt;
	private Color foregroundColor;

	public RadioButtonFigure() {
		setLayoutManager(new FlowLayout(true));
		radioBt = new ImageFigure();
		add(radioBt);
		textLb = new Label();
		textLb.setFont(BMotionStudioSWTConstants.fontArial10);
		add(textLb);
	}

	public void setImage(Image img) {
		radioBt.setImage(img);
	}

	public int setText(String text) {
		textLb.setText(text);
		return textLb.getPreferredSize().width;
	}

	public void setTextColor(RGB rgb) {
		if (foregroundColor != null)
			foregroundColor.dispose();
		foregroundColor = new Color(Display.getDefault(), rgb);
		textLb.setForegroundColor(foregroundColor);
	}
	
	public void setBtEnabled(Boolean bool) {
		textLb.setEnabled(bool);
		repaint();
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
