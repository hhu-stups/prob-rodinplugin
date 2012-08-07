/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class CompositeFigure extends AbstractBMotionFigure {

	protected Image layerImage;
	private ImageData imageData;
	private Dimension size = new Dimension();
	private boolean hasImage;

	public CompositeFigure() {
		XYLayout layout = new XYLayout();
		setLayoutManager(layout);
		PaletteData palette = new PaletteData(
				new RGB[] { ColorConstants.white.getRGB() });
		imageData = new ImageData(1, 1, 8, palette);
		imageData.alpha = 255;
		imageData.setPixel(0, 0, 0);
		layerImage = new Image(Display.getDefault(), imageData);
		hasImage = false;
		setOpaque(true);
	}

	// public void setAlpha(int alpha) {
	// imageData.alpha = alpha;
	// if (!hasImage) {
	// if (layerImage != null && !layerImage.isDisposed()) {
	// layerImage.dispose();
	// }
	// layerImage = new Image(Display.getDefault(), imageData);
	// repaint();
	// }
	// }

	public void setBackgroundColor(RGB rgb) {
		imageData.palette.colors[0] = rgb;
		if (!hasImage) {
			if (layerImage != null && !layerImage.isDisposed()) {
				layerImage.dispose();
			}
			layerImage = new Image(Display.getDefault(), imageData);
			repaint();
		}
	}

	public void paintFigure(Graphics g) {
		if (getImage() == null)
			return;
		Rectangle rectangle = getClientArea();
		if (hasImage) {
			int aWidth = rectangle.width;
			int aHeight = rectangle.height;
			int countX = aWidth / getImage().getBounds().width;
			int countY = aHeight / getImage().getBounds().height;
			for (int i = 0; i <= countX; i++) {
				for (int z = 0; z <= countY; z++) {
					g.drawImage(getImage(), getBounds().x + i
							* getImage().getBounds().width, getBounds().y + z
							* getImage().getBounds().height);
				}
			}
		} else {
			g.drawImage(getImage(), 0, 0, 1, 1, rectangle.x, rectangle.y,
					rectangle.width, rectangle.height);
		}
	}

	/**
	 * Sets the Image that this ImageFigure displays.
	 * <p>
	 * IMPORTANT: Note that it is the client's responsibility to dispose the
	 * given image.
	 * 
	 * @param image
	 *            The Image to be displayed. It can be <code>null</code>.
	 */
	public void setImage(Image image) {
		if (layerImage != null)
			layerImage.dispose();
		layerImage = image;
		if (layerImage != null) {
			size = new Rectangle(image.getBounds()).getSize();
			hasImage = true;
		} else {
			layerImage = new Image(null, imageData);
			size = new Dimension();
			hasImage = false;
		}
		revalidate();
		repaint();
	}

	/**
	 * @return The Image that this Figure displays
	 */
	public Image getImage() {
		return layerImage;
	}

	/**
	 * Calculates the necessary size to display the Image within the figure's
	 * client area.
	 * 
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		if (getInsets() == NO_INSETS)
			return size;
		Insets i = getInsets();
		return size.getExpanded(i.getWidth(), i.getHeight());
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
		if (layerImage != null)
			layerImage.dispose();
	}

}
