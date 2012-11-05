/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundVisible;
import de.bmotionstudio.gef.editor.attribute.BAttributeFont;
import de.bmotionstudio.gef.editor.attribute.BAttributeText;
import de.bmotionstudio.gef.editor.attribute.BAttributeTextColor;

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

		initAttribute(new BAttributeText(DEFAULT_TEXT));
		initAttribute(new BAttributeTextColor(ColorConstants.black.getRGB()));
		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.white.getRGB()));
		initAttribute(new BAttributeBackgroundVisible(true));
		initAttribute(new BAttributeFont(
				"1||9.75|0|WINDOWS|1|-13|0|0|0|400|0|0|0|0|0|0|0|0|"));

	}

}
