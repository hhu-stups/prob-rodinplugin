/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeEnabled;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;
import de.bmotionstudio.gef.editor.attribute.BAttributeTextColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BButton extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.button";

	public static transient String DEFAULT_TEXT = "Click!";

	public BButton(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeBackgroundColor(new RGB(192, 192, 192)));
		initAttribute(new BAttributeTextColor(new RGB(0, 0, 0)));
		initAttribute(new BAttributeEnabled(true));

		BAttributeHeight aHeight = new BAttributeHeight(25);
		aHeight.setGroup(BAttributeSize.ID);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(100);
		aWidth.setGroup(BAttributeSize.ID);
		initAttribute(aWidth);

	}

}
