/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class TextFigure extends AbstractBMotionFigure {

	private TextFlow textFlow;
	protected Image layerImage;
	private Color foregroundColor;
	private Font font;

	public TextFigure() {
		setBorder(new MarginBorder(1));
		FlowPage flowPage = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
		setLayoutManager(new StackLayout());
		add(flowPage);
	}

	/**
	 * Returns the text inside the TextFlow.
	 * 
	 * @return the text flow inside the text.
	 */
	public String getText() {
		return textFlow.getText();
	}

	/**
	 * Sets the text of the TextFlow to the given value.
	 * 
	 * @param newText
	 *            the new text value.
	 */
	public void setText(String newText) {
		textFlow.setText(newText);
	}

	public void setTextColor(RGB rgb) {
		if (foregroundColor != null)
			foregroundColor.dispose();
		foregroundColor = new Color(Display.getDefault(), rgb);
		textFlow.setForegroundColor(foregroundColor);
	}

	public void setBackgroundVisible(Boolean bol) {
		setOpaque(bol);
	}

	public void setFont(String fontData) {
		if (font != null)
			font.dispose();
		font = new Font(Display.getDefault(), new FontData(fontData));
		textFlow.setFont(font);
	}

	// TODO: CHECK STACK OVERFLOW ERROR!!!!
	public Font getFont() {
		return textFlow.getFont();
	}

	public void setLayout(Rectangle rect) {
		getParent().setConstraint(this, rect);
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
		if (font != null)
			font.dispose();
		if (layerImage != null)
			layerImage.dispose();
	}

}
