/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BText extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.text";

	public static transient String DEFAULT_TEXT = "Text...";

	public BText(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT, DEFAULT_TEXT);
		initAttribute(AttributeConstants.ATTRIBUTE_BACKGROUND_COLOR, new RGB(
				255, 255, 255));
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT_COLOR, DEFAULT_TEXT);
		initAttribute(AttributeConstants.ATTRIBUTE_BACKGROUND_VISIBLE, true);
		initAttribute(AttributeConstants.ATTRIBUTE_FONT,
				"1||9.75|0|WINDOWS|1|-13|0|0|0|400|0|0|0|0|0|0|0|0|");
	}

}
