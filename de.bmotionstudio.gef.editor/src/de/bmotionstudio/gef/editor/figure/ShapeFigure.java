/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.figure;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Orientable;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Shape;
import org.eclipse.draw2d.StackLayout;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;

public class ShapeFigure extends AbstractBMotionFigure {

	private int alpha;
	private int outlineAlpha;
	private int shape;
	private Integer fillType;
	private Image img;
	private Color foregroundColor;
	private Color backgroundColor;

	private Image patternImage;
	private Image shadedImage;
	private Pattern pattern;
	private Pattern shadedPattern;
	private Pattern gradientPattern;

	private final Color white = Display.getDefault().getSystemColor(
			SWT.COLOR_WHITE);

	private static final int FILL_TYPE_FILLED = 0;
	private static final int FILL_TYPE_EMPTY = 1;
	private static final int FILL_TYPE_SHADED = 2;
	private static final int FILL_TYPE_GRADIENT = 3;

	private static final int SHAPE_TYPE_RECTANGLE = 0;
	private static final int SHAPE_TYPE_OVAL = 1;
	private static final int SHAPE_TYPE_TRIANGLE = 2;
	private static final int SHAPE_TYPE_DIAMOND = 3;

	/**
	 * The direction this triangle will face. Possible values are
	 * {@link PositionConstants#NORTH}, {@link PositionConstants#SOUTH},
	 * {@link PositionConstants#EAST} and {@link PositionConstants#WEST}.
	 */
	protected int direction = PositionConstants.NORTH;
	/**
	 * The orientation of this triangle. Possible values are
	 * {@link Orientable#VERTICAL} and {@link Orientable#HORIZONTAL}.
	 */
	protected int orientation = PositionConstants.VERTICAL;

	/** The points of the triangle. */
	protected PointList triangle = new PointList(3);

	/** The points of the diamond. */
	protected PointList diamond = new PointList(4);

	private Shape shapeFigure;

	public ShapeFigure() {
		setLayoutManager(new StackLayout());
		shapeFigure = new Shape() {

			@Override
			protected void fillShape(Graphics g) {

				if (!visible && !isRunning)
					g.setAlpha(AbstractBMotionFigure.HIDDEN_ALPHA_VALUE);
				else
					g.setAlpha(alpha);
				g.setAntialias(SWT.ON);

				if (fillType == FILL_TYPE_GRADIENT) { // Gradient fill type

					if (gradientPattern != null)
						gradientPattern.dispose();
					gradientPattern = new Pattern(Display.getDefault(),
							this.getBounds().x, this.getBounds().y,
							this.getBounds().x + this.getBounds().width,
							this.getBounds().y + this.getBounds().height,
							this.getBackgroundColor(),
							this.getForegroundColor());
					g.setBackgroundPattern(gradientPattern);

				} else if (fillType == FILL_TYPE_SHADED) { // Shaded fill type

					Color black = this.getForegroundColor();
					PaletteData palette = new PaletteData(new RGB[] {
							white.getRGB(), black.getRGB() });
					ImageData sourceData = new ImageData(11, 11, 1, palette);
					for (int i = 0; i < 11; i++) {
						sourceData.setPixel(6, i, 1);
					}
					if (shadedImage != null)
						shadedImage.dispose();
					shadedImage = new Image(Display.getDefault(), sourceData);

					if (shadedPattern != null)
						shadedPattern.dispose();
					shadedPattern = new Pattern(Display.getDefault(),
							shadedImage);
					g.setBackgroundPattern(shadedPattern);

				} else if (fillType == FILL_TYPE_FILLED && img != null) {

					double zoom = 1;
					if (BMotionEditorPlugin.getActiveEditor() != null)
						zoom = BMotionEditorPlugin.getActiveEditor()
								.getZoomFactor();

					ImageData d = img.getImageData().scaledTo(
							(int) (img.getBounds().width * zoom),
							(int) (img.getBounds().height * zoom));

					if (patternImage != null)
						patternImage.dispose();

					patternImage = new Image(Display.getDefault(), d);

					if (pattern != null)
						pattern.dispose();

					pattern = new Pattern(Display.getDefault(), patternImage);

					g.setBackgroundPattern(pattern);

				} else if (fillType == FILL_TYPE_FILLED) {
					g.setBackgroundColor(this.getBackgroundColor());
				}

				switch (shape) {
				case SHAPE_TYPE_RECTANGLE:
					g.fillRectangle(this.getBounds());
					break;
				case SHAPE_TYPE_OVAL:
					g.fillOval(this.getBounds().x, this.getBounds().y,
							this.getBounds().width - 3,
							this.getBounds().height - 3);
					break;
				case SHAPE_TYPE_TRIANGLE:
					g.fillPolygon(triangle);
					break;
				case SHAPE_TYPE_DIAMOND:
					g.fillPolygon(diamond);
					break;
				default:
					break;
				}

			}

			@Override
			protected void outlineShape(Graphics g) {

				if (!visible && !isRunning)
					g.setAlpha(AbstractBMotionFigure.HIDDEN_ALPHA_VALUE);
				else
					g.setAlpha(outlineAlpha);
				g.setAntialias(SWT.ON);
				g.setForegroundColor(this.getForegroundColor());

				float lineInset = Math.max(1.0f, getLineWidthFloat()) / 2.0f;

				int inset1 = (int) Math.floor(lineInset);
				int inset2 = (int) Math.ceil(lineInset);

				Rectangle r = Rectangle.SINGLETON.setBounds(this.getBounds());
				r.x += inset1;
				r.y += inset1;
				r.width -= inset1 + inset2;
				r.height -= inset1 + inset2;

				switch (shape) {
				case SHAPE_TYPE_RECTANGLE:
					g.drawRectangle(r);
					break;
				case SHAPE_TYPE_OVAL:
					r.width -= 2;
					r.height -= 2;
					g.drawOval(r);
					break;
				case SHAPE_TYPE_TRIANGLE:
					g.drawPolygon(triangle);
					break;
				case SHAPE_TYPE_DIAMOND:
					g.drawPolygon(diamond);
					break;
				default:
					break;
				}

			}

		};
		// shapeFigure.setForegroundColor(ColorConstants.blue);
		// setOpaque(true);
		add(shapeFigure);
	}

	/**
	 * @see Orientable#setDirection(int)
	 */
	public void setDirection(int value) {
		if ((value & (PositionConstants.NORTH | PositionConstants.SOUTH)) != 0)
			orientation = PositionConstants.VERTICAL;
		else
			orientation = PositionConstants.HORIZONTAL;
		direction = value;
		revalidate();
		repaint();
	}

	/**
	 * @see Orientable#setOrientation(int)
	 */
	public void setOrientation(int value) {
		if (orientation == PositionConstants.VERTICAL
				&& value == PositionConstants.HORIZONTAL) {
			if (direction == PositionConstants.NORTH)
				setDirection(PositionConstants.WEST);
			else
				setDirection(PositionConstants.EAST);
		}
		if (orientation == PositionConstants.HORIZONTAL
				&& value == PositionConstants.VERTICAL) {
			if (direction == PositionConstants.WEST)
				setDirection(PositionConstants.NORTH);
			else
				setDirection(PositionConstants.SOUTH);
		}
	}

	/**
	 * @see IFigure#validate()
	 */
	public void validate() {

		super.validate();

		Rectangle r = new Rectangle();
		r.setBounds(getBounds());
		r.crop(getInsets());
		r.resize(-1, -1);

		switch (shape) {

		case SHAPE_TYPE_TRIANGLE:

			int size;
			switch (direction
					& (PositionConstants.NORTH | PositionConstants.SOUTH)) {
			case 0: // East or west.
				size = Math.min(r.height / 2, r.width);
				r.x += (r.width - size) / 2;
				break;
			default: // North or south
				size = Math.min(r.height, r.width / 2);
				r.y += (r.height - size) / 2;
				break;
			}

			size = Math.max(size, 1); // Size cannot be negative

			Point head,
			p2,
			p3;

			switch (direction) {
			case PositionConstants.NORTH:
				head = new Point(r.x + r.width / 2, r.y);
				p2 = new Point(head.x - size, head.y + size);
				p3 = new Point(head.x + size, head.y + size);
				break;
			case PositionConstants.SOUTH:
				head = new Point(r.x + r.width / 2, r.y + size);
				p2 = new Point(head.x - size, head.y - size);
				p3 = new Point(head.x + size, head.y - size);
				break;
			case PositionConstants.WEST:
				head = new Point(r.x, r.y + r.height / 2);
				p2 = new Point(head.x + size, head.y - size);
				p3 = new Point(head.x + size, head.y + size);
				break;
			default:
				head = new Point(r.x + size, r.y + r.height / 2);
				p2 = new Point(head.x - size, head.y - size);
				p3 = new Point(head.x - size, head.y + size);

			}
			triangle.removeAllPoints();
			triangle.addPoint(head);
			triangle.addPoint(p2);
			triangle.addPoint(p3);

			break;

		case SHAPE_TYPE_DIAMOND:

			Point pt1 = new Point(r.x + r.width / 2, r.y);
			Point pt2 = new Point(r.x + r.width, r.y + r.height / 2);
			Point pt3 = new Point(r.x + r.width / 2, r.y + r.height);
			Point pt4 = new Point(r.x, r.y + r.height / 2);

			diamond.removeAllPoints();
			diamond.addPoint(pt1);
			diamond.addPoint(pt2);
			diamond.addPoint(pt3);
			diamond.addPoint(pt4);

			break;

		default:
			break;
		}

	}

	/**
	 * @see Figure#primTranslate(int, int)
	 */
	public void primTranslate(int dx, int dy) {
		super.primTranslate(dx, dy);
		switch (shape) {
		case SHAPE_TYPE_TRIANGLE:
			triangle.translate(dx, dy);
			break;
		case SHAPE_TYPE_DIAMOND:
			diamond.translate(dx, dy);
			break;
		default:
			break;
		}
	}

	public void setBackgroundColor(RGB rgb) {
		if (backgroundColor != null)
			backgroundColor.dispose();
		backgroundColor = new Color(Display.getDefault(), rgb);
		shapeFigure.setBackgroundColor(backgroundColor);
	}

	public void setForegroundColor(RGB rgb) {
		if (foregroundColor != null)
			foregroundColor.dispose();
		foregroundColor = new Color(Display.getDefault(), rgb);
		shapeFigure.setForegroundColor(foregroundColor);
		shapeFigure.repaint();
	}

	public void setLayout(Rectangle rect) {
		getParent().setConstraint(this, rect);
	}

	public void setShape(int shape) {
		this.shape = shape;
		revalidate();
		shapeFigure.repaint();
	}

	public int getShape() {
		return shape;
	}

	public Integer getAlpha() {
		return alpha;
	}

	public void setAlpha(Integer alpha) {
		this.alpha = alpha;
		repaint();
	}

	public Integer getOutlineAlpha() {
		return outlineAlpha;
	}

	public void setOutlineAlpha(Integer outlineAlpha) {
		this.outlineAlpha = outlineAlpha;
		repaint();
	}

	public void setFillType(Integer fillType) {
		if (fillType == FILL_TYPE_EMPTY)
			shapeFigure.setFill(false);
		else
			shapeFigure.setFill(true);
		this.fillType = fillType;
		repaint();
	}

	public void setImage(Image img) {
		this.img = img;
		repaint();
	}

	@Override
	public void deactivateFigure() {
		if (img != null)
			img.dispose();
		if (foregroundColor != null)
			foregroundColor.dispose();
		if (backgroundColor != null)
			backgroundColor.dispose();
		if (patternImage != null)
			patternImage.dispose();
		if (shadedImage != null)
			shadedImage.dispose();
		if (pattern != null)
			pattern.dispose();
		if (shadedPattern != null)
			shadedPattern.dispose();
		if (gradientPattern != null)
			gradientPattern.dispose();
	}

}
