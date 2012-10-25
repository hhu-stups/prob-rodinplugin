/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;

public class BTableCell extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tablecell";

	public BTableCell(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR,
				ColorConstants.white.getRGB());
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR,
				ColorConstants.black.getRGB(), true, false);
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT_COLOR,
				ColorConstants.black.getRGB());
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT, "");
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 20, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}
