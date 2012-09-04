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
public class BCheckbox extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.checkbox";

	public static transient String DEFAULT_TEXT = "Text...";

	public BCheckbox(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT, DEFAULT_TEXT);
		initAttribute(AttributeConstants.ATTRIBUTE_TEXT_COLOR, new RGB(0, 0, 0));
		initAttribute(AttributeConstants.ATTRIBUTE_CHECKED, true);
		initAttribute(AttributeConstants.ATTRIBUTE_ENABLED, true);
		initAttribute(AttributeConstants.ATTRIBUTE_TRUEVALUE, "");
		initAttribute(AttributeConstants.ATTRIBUTE_FALSEVALUE, "");
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 21, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
	}

	@Override
	public String getValueOfData() {
		if (Boolean.valueOf(getAttributeValue(
				AttributeConstants.ATTRIBUTE_CHECKED).toString())) {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_TRUEVALUE)
					.toString();
		} else {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_FALSEVALUE)
					.toString();
		}
	}

}
