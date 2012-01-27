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

import de.bmotionstudio.gef.editor.BMotionStudioSWTConstants;

public class CheckboxFigure extends AbstractBMotionFigure {

	private Label textLb;

	private ImageFigure checkBox;

	public CheckboxFigure() {
		setLayoutManager(new FlowLayout(true));
		checkBox = new ImageFigure();
		add(checkBox);
		textLb = new Label();
		textLb.setFont(BMotionStudioSWTConstants.fontArial10);
		add(textLb);
	}

	public void setImage(Image img) {
		checkBox.setImage(img);
	}

	public int setText(String text) {
		textLb.setText(text);
		return textLb.getPreferredSize().width;
	}

	public void setTextColor(Color color) {
		textLb.setForegroundColor(color);
	}
	
	public void setBtEnabled(Boolean bool) {
		textLb.setEnabled(bool);
		repaint();
	}

}
