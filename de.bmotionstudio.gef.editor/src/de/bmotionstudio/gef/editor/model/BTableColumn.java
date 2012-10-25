/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.AttributeConstants;

public class BTableColumn extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tablecolumn";

	public BTableColumn(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_FOREGROUND_COLOR,
				ColorConstants.black.getRGB(), true, false);
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 0, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
	}

	@Override
	public String getType() {
		return TYPE;
	}

}