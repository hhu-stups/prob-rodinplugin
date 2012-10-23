/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;

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

		initAttribute(AttributeConstants.ATTRIBUTE_SHOWS_MEASURE, true);
		initAttribute(AttributeConstants.ATTRIBUTE_MEASURE_INTERVAL, 25);
		initAttribute(AttributeConstants.ATTRIBUTE_MEASURE_MAXPOS, 100);
		initAttribute(AttributeConstants.ATTRIBUTE_FILL_COLOR,
				new RGB(0, 0, 255));
		initAttribute(AttributeConstants.ATTRIBUTE_FILL_HEIGHT, 75);
		initAttribute(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR, new RGB(
				255, 255, 255));
		initAttribute(AttributeConstants.ATTRIBUTE_ALPHA, 0);

	}

}
