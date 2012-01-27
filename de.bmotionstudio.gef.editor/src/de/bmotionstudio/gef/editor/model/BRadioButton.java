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
import de.bmotionstudio.gef.editor.attribute.BAttributeButtonGroup;
import de.bmotionstudio.gef.editor.attribute.BAttributeChecked;
import de.bmotionstudio.gef.editor.attribute.BAttributeEnabled;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;
import de.bmotionstudio.gef.editor.attribute.BAttributeTextColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeValue;

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
		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeChecked(true));
		initAttribute(new BAttributeValue(""));
		initAttribute(new BAttributeButtonGroup(""));
		initAttribute(new BAttributeEnabled(true));
		getAttribute(AttributeConstants.ATTRIBUTE_HEIGHT).setValue(21);
		getAttribute(AttributeConstants.ATTRIBUTE_HEIGHT).setEditable(false);
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
