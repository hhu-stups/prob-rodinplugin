/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

public class UnknownBControl extends AbstractBMotionFigure {

	public static String ID = "de.bmotionstudio.gef.editor.unknown";

	private final TextFlow textFlow;

	public UnknownBControl() {
		setBorder(new MarginBorder(1));
		FlowPage flowPage = new FlowPage();
		textFlow = new TextFlow();
		textFlow.setLayoutManager(new ParagraphTextLayout(textFlow,
				ParagraphTextLayout.WORD_WRAP_SOFT));
		flowPage.add(textFlow);
		setLayoutManager(new StackLayout());
		add(flowPage);
		setBackgroundColor(ColorConstants.red);
		setOpaque(true);
	}

	public void prepareForEditing() {
	}

	public void setMessage(final String type) {
		textFlow.setText("Unknown part: " + type);
	}

	public void setLayout(final Rectangle rect) {

	}

}
