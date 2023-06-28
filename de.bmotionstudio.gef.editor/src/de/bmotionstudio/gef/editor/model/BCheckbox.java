/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeChecked;
import de.bmotionstudio.gef.editor.attribute.BAttributeEnabled;
import de.bmotionstudio.gef.editor.attribute.BAttributeFalseValue;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;
import de.bmotionstudio.gef.editor.attribute.BAttributeTextColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeTrueValue;

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

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeEnabled(true));
		initAttribute(new BAttributeChecked(true));
		initAttribute(new BAttributeTrueValue(""));
		initAttribute(new BAttributeFalseValue(""));

		BAttributeHeight aHeight = new BAttributeHeight(21);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

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
