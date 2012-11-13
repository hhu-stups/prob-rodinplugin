/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BTextfield extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.textfield";

	public static transient String DEFAULT_TEXT = "Text...";

	public BTextfield(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));

		BAttributeHeight aHeight = new BAttributeHeight(21);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

	}

	@Override
	public String getValueOfData() {
		return getAttributeValue(AttributeConstants.ATTRIBUTE_TEXT).toString();
	}

}
