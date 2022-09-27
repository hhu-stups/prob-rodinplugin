/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.LayoutAnimator;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.geometry.Rectangle;

public class VisualizationFigure extends FreeformLayer {

	private LayoutListener layoutListener = LayoutAnimator.getDefault();

	public VisualizationFigure() {
		setLayoutManager(new FreeformLayout());
		addLayoutListener(layoutListener);
	}

	public void prepareForEditing() {
		removeLayoutListener(layoutListener);
	}

	public void setLayout(Rectangle rect) {
	}

}
