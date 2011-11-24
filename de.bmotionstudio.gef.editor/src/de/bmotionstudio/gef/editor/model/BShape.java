/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

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

	public static transient String TYPE = "de.bmotionstudio.gef.editor.shape";

	public BShape(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(new BAttributeBackgroundColor(new RGB(255, 0, 0)));
		initAttribute(new BAttributeForegroundColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeImage(null));
		initAttribute(new BAttributeAlpha(255));
		initAttribute(new BAttributeOutlineAlpha(255));
		initAttribute(new BAttributeShape(BAttributeShape.SHAPE_RECTANGLE));
		initAttribute(new BAttributeOrientation(
				BAttributeOrientation.HORIZONTAL));
		initAttribute(new BAttributeDirection(BAttributeDirection.NORTH));
		initAttribute(new BAttributeFillType(BAttributeFillType.FILLED));
	}

}
