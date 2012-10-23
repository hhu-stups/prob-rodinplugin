/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;

public class Light extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.light";

	public Light(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {

		initAttribute(AttributeConstants.ATTRIBUTE_WIDTH, 12, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 12, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
		initAttribute(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR,
				ColorConstants.lightGray.getRGB());

	}

	@Override
	public String getType() {
		return TYPE;
	}

}
