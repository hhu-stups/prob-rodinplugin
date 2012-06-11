/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class CanisterFigure extends AbstractBMotionFigure {

	private RectangleFigure axisRect;
	private Figure axisFigure;
	private RectangleFigure fillRect;
	private Figure fillFigure;

	private Image layerImage;
	private ImageData imageData;

	private int fill_height;
	private double positions;
	private int show_pos;

	private Boolean showMeasure;

	public CanisterFigure() {

		XYLayout layout = new XYLayout();
		setLayoutManager(layout);

		PaletteData palette = new PaletteData(
				new RGB[] { ColorConstants.white.getRGB() });
		imageData = new ImageData(1, 1, 8, palette);
		imageData.alpha = 255;
		imageData.setPixel(0, 0, 0);
		layerImage = new Image(null, imageData);

		axisFigure = new Figure();
		axisFigure.setLocation(new Point(0, 0));
		axisFigure.setLayoutManager(new XYLayout());
		add(axisFigure);

		fillFigure = new Figure();
		fillFigure.setLayoutManager(new BorderLayout());
		add(fillFigure);

		fillRect = new RectangleFigure();
		fillRect.setOutline(false);
		fillFigure.add(fillRect);
		fillFigure.setConstraint(fillRect, BorderLayout.BOTTOM);

		setOpaque(true);

	}

	public void setLayout(Rectangle rect) {

		getParent().setConstraint(this, rect);

		// Set the right size and position of the y-axis
		axisFigure.removeAll();

		int fillPos = 0;

		double one_pos = Double.valueOf(rect.height) / positions;

		if (showMeasure) {

			axisFigure.setSize(100, rect.height);

			axisRect = new RectangleFigure();
			axisFigure.add(axisRect);
			axisRect.setBackgroundColor(ColorConstants.black);
			axisFigure.setConstraint(axisRect, new Rectangle(14, 0, 1,
					rect.height));

			RectangleFigure line;
			Label lb;

			for (int i = 0; i <= positions; i = i + show_pos) {

				lb = new Label();
				axisFigure.add(lb);
				lb.setText(String.valueOf((int) (positions - i)));
				lb.setBackgroundColor(ColorConstants.red);
				lb.setTextAlignment(PositionConstants.LEFT);

				if (i == 0) {
					axisFigure.setConstraint(lb, new Rectangle(18,
							(int) (i * one_pos), 30, 10));
				} else if (i == positions) {
					axisFigure.setConstraint(lb, new Rectangle(18,
							(int) (i * one_pos) - 10, 30, 10));
				} else {
					axisFigure.setConstraint(lb, new Rectangle(18,
							(int) (i * one_pos) - 5, 30, 10));
				}

				line = new RectangleFigure();
				line.setBackgroundColor(ColorConstants.black);
				line.setOutline(false);
				axisFigure.add(line);

				if (i == positions) {
					axisFigure.setConstraint(line, new Rectangle(10,
							(int) (i * one_pos) - 1, 10, 1));
				} else {
					axisFigure.setConstraint(line, new Rectangle(10,
							(int) (i * one_pos), 10, 1));
				}

			}

			fillPos = 60;

		}

		// Set right size of the fill figure
		setConstraint(fillFigure, new Rectangle(fillPos, 0, rect.width
				- fillPos, rect.height));

		double tmp = one_pos * (positions - Double.valueOf(fill_height));
		int f_fill_height = (int) tmp;
		fillRect.setSize(rect.width - fillPos, rect.height - f_fill_height);
		fillFigure.setConstraint(fillRect, BorderLayout.BOTTOM);
		fillFigure.repaint();

	}

	public void setAlpha(int alpha) {
		imageData.alpha = alpha;
		if (layerImage != null && !layerImage.isDisposed()) {
			layerImage.dispose();
		}
		layerImage = new Image(null, imageData);
		repaint();
	}

	public void paintFigure(Graphics g) {
		Rectangle rectangle = getClientArea();
		g.drawImage(layerImage, new Rectangle(layerImage.getBounds()),
				rectangle);
	}

	public void setFillColor(RGB rgb) {
		fillRect.setBackgroundColor(new Color(Display.getDefault(), rgb));
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

}
