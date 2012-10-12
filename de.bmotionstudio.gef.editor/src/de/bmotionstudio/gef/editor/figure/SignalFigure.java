/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


/**
 * @author Lukas Ladenberger
 * 
 */
public class SignalFigure extends AbstractBMotionFigure {

	private Label lb;
	private PointList arrow = new PointList();
	private boolean isEast;
	private Figure panel;
	private Ellipse light1;
	private Ellipse light2;
	private final Color green = Display.getDefault().getSystemColor(
			SWT.COLOR_GREEN);
	private final Color red = Display.getDefault()
			.getSystemColor(SWT.COLOR_RED);
	private final Color gray = Display.getDefault().getSystemColor(
			SWT.COLOR_GRAY);

	public SignalFigure() {

		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);

		setLayoutManager(layout);

		lb = new Label();

		Figure node = new RectangleFigure();
		node.setBackgroundColor(ColorConstants.lightGray);
		node.setLayoutManager(new ToolbarLayout());
		node.setOpaque(true);
		light1 = new Ellipse();
		light1.setBackgroundColor(red);
		light1.setSize(10, 10);
		node.add(light1);
		light2 = new Ellipse();
		light2.setBackgroundColor(green);
		light2.setSize(10, 10);
		node.add(light2);
		node.setMaximumSize(new Dimension(10, 20));

		panel = new Figure() {
			@Override
			protected void paintFigure(Graphics g) {

				super.paintFigure(g);
				Rectangle r = getClientArea();
				arrow.removeAllPoints();

				g.setAlpha(0);
				g.setBackgroundColor(ColorConstants.white);
				g.setForegroundColor(ColorConstants.lightGray);
				g.fillRectangle(r); // Fill background with color

				// Draw track lines
				g.setAlpha(255);
				g.setLineWidth(1);

				// Draw horizontal line
				Point pt1 = r.getTopLeft();
				Point pt2 = r.getTopRight();
				pt1.y = pt1.y + 4;
				pt2.y = pt2.y + 4;
				g.drawLine(pt1, pt2);

				g.setAlpha(255);
				g.setBackgroundColor(ColorConstants.lightGray);

				// Draw arrow
				Point p1;
				Point p2;
				Point p3;

				if (isEast) {
					p1 = r.getTopRight();
					p2 = r.getTopRight();
					p3 = r.getTopRight();
					p2.x = p2.x - 8;
					p3.x = p3.x - 8;
				} else {
					p1 = r.getTopLeft();
					p2 = r.getTopLeft();
					p3 = r.getTopLeft();
					p2.x = p2.x + 8;
					p3.x = p3.x + 8;
				}

				p1.y = p1.y + 4;
				p2.y = p2.y + 8;
				p3.y = p3.y;

				arrow.addPoint(p1);
				arrow.addPoint(p2);
				arrow.addPoint(p3);
				g.fillPolygon(arrow);

			}

		};
		panel.setPreferredSize(60, 10);

		add(lb);
		add(node);
		add(panel);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#deactivateFigure()
	 */
	@Override
	public void deactivateFigure() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.figure.IBMotionFigure#activateFigure()
	 */
	@Override
	public void activateFigure() {
	}

	public void setTrackDirection(boolean isEast) {
		this.isEast = isEast;
		this.panel.repaint();
	}

	public void setLabel(String lb) {
		this.lb.setText(lb);
	}

	public void setSignalColor(int color) {

		switch (color) {
		case 0: // Green
			light1.setBackgroundColor(red);
			light2.setBackgroundColor(gray);
			break;
		case 1: // Red
			light1.setBackgroundColor(gray);
			light2.setBackgroundColor(green);
			break;
		default: // No color
			light1.setBackgroundColor(gray);
			light2.setBackgroundColor(gray);
			break;
		}
	}

}
