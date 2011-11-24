/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

public class BMSImageFigure extends AbstractBMotionFigure {

	private ImageFigure imageFigure;

	public BMSImageFigure() {
		setLayoutManager(new StackLayout());
		imageFigure = new ImageFigure() {
			public void paintFigure(Graphics g) {
				if (getImage() == null)
					return;
				Rectangle rectangle = getClientArea();
				g.drawImage(getImage(), new Rectangle(getImage().getBounds()),
						rectangle);
			}
		};
		add(imageFigure);
	}

	public void setLayout(Rectangle rect) {
		getParent().setConstraint(imageFigure, rect);
	}

	public void setImage(Image image) {
		if (imageFigure.getImage() != null)
			imageFigure.getImage().dispose();
		imageFigure.setImage(image);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#deactivateFigure()
	 */
	@Override
	public void deactivateFigure() {
		if (imageFigure.getImage() != null)
			imageFigure.getImage().dispose();
	}

}
