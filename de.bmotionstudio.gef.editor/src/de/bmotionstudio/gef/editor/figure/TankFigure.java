/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;


public class TankFigure extends AbstractBMotionFigure {

	private Image layerImage;
	private ImageData imageData;

	private int fill_height;
	private double positions;
	private int show_pos;

	private Color fillColor;

	private Boolean showMeasure;

	public TankFigure() {
		PaletteData palette = new PaletteData(
				new RGB[] { ColorConstants.white.getRGB() });
		imageData = new ImageData(1, 1, 8, palette);
		imageData.alpha = 255;
		imageData.setPixel(0, 0, 0);
		layerImage = new Image(null, imageData);
	}

	public void setAlpha(int alpha) {
		imageData.alpha = alpha;
		if (layerImage != null && !layerImage.isDisposed()) {
			layerImage.dispose();
		}
		layerImage = new Image(null, imageData);
		repaint();
	}

	@Override
	protected void paintFigure(Graphics g) {

		Rectangle rect = getClientArea();
		g.drawImage(layerImage, new Rectangle(layerImage.getBounds()), rect);

		// Set the right size and position of the y-axis

		int fillPos = 0;

		double one_pos = Double.valueOf(rect.height) / positions;

		if (showMeasure) {

			g.setForegroundColor(ColorConstants.black);
			g.drawLine(rect.x + 5, rect.y, rect.x + 5, rect.y + rect.height);

			for (int i = 0; i <= positions; i = i + show_pos) {

				if (i == 0) {
					// First position
					// Draw label
					g.drawText(String.valueOf((int) (positions - i)),
							rect.x + 18, rect.y + (int) (i * one_pos) - 3);

					// Draw line
					g.drawLine(rect.x + 10, rect.y + (int) (i * one_pos),
							rect.x, rect.y + (int) (i * one_pos));
				} else if (i == positions) {
					// Last position

					// Draw label
					g.drawText(String.valueOf((int) (positions - i)),
							rect.x + 18, rect.y + (int) (i * one_pos) - 12);

					// Draw line
					g.drawLine(rect.x + 10, rect.y + (int) (i * one_pos) - 1,
							rect.x, rect.y + (int) (i * one_pos) - 1);
				} else {
					// All other positions

					// Draw label
					g.drawText(String.valueOf((int) (positions - i)),
							rect.x + 18, rect.y + (int) (i * one_pos) - 5);

					// // Draw line
					g.drawLine(rect.x + 10, rect.y + (int) (i * one_pos),
							rect.x, rect.y + (int) (i * one_pos));
				}

			}

			fillPos = 60;

		}

		// Set right size of the fill figure
		g.setBackgroundColor(fillColor);

		double tmp = one_pos * (positions - Double.valueOf(fill_height));
		int f_fill_height = (int) tmp;

		g.fillRectangle(rect.x + fillPos, rect.y + f_fill_height, rect.width
				- fillPos, rect.height);

		super.paintFigure(g);

	}

	public void setFillColor(RGB rgb) {
		if (fillColor != null)
			fillColor.dispose();
		fillColor = new Color(Display.getDefault(), rgb);
	}

	public void setFillHeight(Integer height) {
		this.fill_height = height;
	}

	public void setMaxPos(Integer maxPos) {
		this.positions = maxPos;
	}

	public void setInterval(Integer interval) {
		this.show_pos = interval;
	}

	public void setMeasure(Boolean bol) {
		this.showMeasure = bol;
	}

	public void setBackgroundColor(RGB rgb) {
		imageData.palette.colors[0] = rgb;
		if (layerImage != null && !layerImage.isDisposed()) {
			layerImage.dispose();
		}
		layerImage = new Image(null, imageData);
		repaint();
	}

	@Override
	public void deactivateFigure() {
		if (fillColor != null)
			fillColor.dispose();
		if (layerImage != null)
			layerImage.dispose();
	}

}
