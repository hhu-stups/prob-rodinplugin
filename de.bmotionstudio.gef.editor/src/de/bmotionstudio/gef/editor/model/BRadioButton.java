/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.util.Collection;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.ButtonGroupHelper;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BRadioButton extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.radiobutton";

	public static transient String DEFAULT_TEXT = "Text...";

	public BRadioButton(Visualization visualization) {
		super(visualization);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.model.BControl#getType()
	 */
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
		initAttribute(AttributeConstants.ATTRIBUTE_VALUE, "");
		initAttribute(AttributeConstants.ATTRIBUTE_BUTTONGROUP, "");
		initAttribute(AttributeConstants.ATTRIBUTE_HEIGHT, 21, false, false,
				AttributeConstants.ATTRIBUTE_SIZE);
	}

	@Override
	public String getValueOfData() {
		String btgroupid = getAttributeValue(
				AttributeConstants.ATTRIBUTE_BUTTONGROUP).toString();
		if (!btgroupid.trim().equals("")) {
			Collection<BControl> btGroup = ButtonGroupHelper
					.getButtonGroup(btgroupid);
			return getValueFromButtonGroup(btGroup);
		} else {
			return getAttributeValue(AttributeConstants.ATTRIBUTE_VALUE)
					.toString();
		}
	}

	private String getValueFromButtonGroup(Collection<BControl> group) {
		for (BControl control : group) {
			if (Boolean.valueOf(control.getAttributeValue(
					AttributeConstants.ATTRIBUTE_CHECKED).toString())) {
				return control.getAttributeValue(
						AttributeConstants.ATTRIBUTE_VALUE).toString();
			}
		}
		return "";
	}

}
