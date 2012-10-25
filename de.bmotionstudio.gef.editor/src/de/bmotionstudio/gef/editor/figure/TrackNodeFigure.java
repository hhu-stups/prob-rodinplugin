package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;


public class TrackNodeFigure extends AbstractBMotionFigure {

	private Color foregroundColor;
	private int lineWidth;
	private int lineStyle;

	public TrackNodeFigure() {
		setOpaque(false);
	}

	@Override
	protected void paintFigure(Graphics g) {

		Rectangle r = getClientArea();
		g.setBackgroundColor(getBackgroundColor());
		g.fillRectangle(r);
		g.setForegroundColor(foregroundColor);
		g.setLineStyle(lineStyle);
		g.setLineWidth(lineWidth);
		g.drawLine(r.getTop().x, r.getTop().y + 5, r.getBottom().x,
				r.getBottom().y - 5);
		super.paintFigure(g);

	}

	@Override
	public void setForegroundColor(Color fg) {
		this.foregroundColor = fg;
		repaint();
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
		repaint();
	}

	public void setLineStyle(int lineStyle) {
		this.lineStyle = lineStyle;
		repaint();
	}

	@Override
	public void deactivateFigure() {
		if (foregroundColor != null)
			foregroundColor.dispose();
	}

}
