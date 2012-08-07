/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.attribute.BAttributeAlpha;
import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeDirection;
import de.bmotionstudio.gef.editor.attribute.BAttributeFillType;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeImage;
import de.bmotionstudio.gef.editor.attribute.BAttributeOrientation;
import de.bmotionstudio.gef.editor.attribute.BAttributeOutlineAlpha;
import de.bmotionstudio.gef.editor.attribute.BAttributeShape;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BShape extends BControl {

	public static transient String TYPE_RECTANGLE = "de.bmotionstudio.gef.editor.rectangle";
	public static transient String TYPE_OVAL = "de.bmotionstudio.gef.editor.ellipse";

	public BShape(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		if (getAttributeValue(AttributeConstants.ATTRIBUTE_SHAPE).equals(
				BAttributeShape.SHAPE_OVAL))
			return TYPE_OVAL;
		else
			return TYPE_RECTANGLE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(new BAttributeBackgroundColor(new RGB(255, 0, 0)));
		initAttribute(new BAttributeForegroundColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeImage(null));
		initAttribute(new BAttributeAlpha(255));
		initAttribute(new BAttributeOutlineAlpha(0));
		initAttribute(new BAttributeShape(BAttributeShape.SHAPE_RECTANGLE));
		initAttribute(new BAttributeOrientation(
				BAttributeOrientation.HORIZONTAL));
		initAttribute(new BAttributeDirection(BAttributeDirection.NORTH));
		initAttribute(new BAttributeFillType(BAttributeFillType.FILLED));
	}

	@Override
	public Image getIcon() {
		if (getAttributeValue(AttributeConstants.ATTRIBUTE_SHAPE).equals(
				BAttributeShape.SHAPE_OVAL))
			return BMotionStudioImage.getBControlImage(TYPE_OVAL);
		else
			return super.getIcon();
	}

}
