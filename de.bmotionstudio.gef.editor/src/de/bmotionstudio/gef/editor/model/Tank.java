/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.attribute.AttributeFillColor;
import de.bmotionstudio.gef.editor.attribute.AttributeFillHeight;
import de.bmotionstudio.gef.editor.attribute.AttributeMeasureInterval;
import de.bmotionstudio.gef.editor.attribute.AttributeMeasureMaxPos;
import de.bmotionstudio.gef.editor.attribute.AttributeShowMeasure;
import de.bmotionstudio.gef.editor.attribute.BAttributeAlpha;
import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;

public class Tank extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tank";

	public Tank(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.black.getRGB()));
		initAttribute(new AttributeShowMeasure(true));
		initAttribute(new AttributeMeasureInterval(25));
		initAttribute(new AttributeMeasureMaxPos(100));
		initAttribute(new AttributeFillColor(ColorConstants.blue.getRGB()));
		initAttribute(new AttributeFillHeight(75));
		initAttribute(new BAttributeAlpha(0));

	}

}
