/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineStyle;
import de.bmotionstudio.gef.editor.attribute.BAttributeLineWidth;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeWidth;

/**
 * @author Lukas Ladenberger
 * 
 */
public class TrackNode extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tracknode";

	public TrackNode(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		aSize.setShow(false);
		aSize.setEditable(false);
		initAttribute(aSize);

		BAttributeHeight aHeight = new BAttributeHeight(20);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(20);
		aWidth.setGroup(BAttributeSize.ID);
		aWidth.setShow(false);
		aWidth.setEditable(false);
		initAttribute(aWidth);

		initAttribute(new BAttributeForegroundColor(
				ColorConstants.black.getRGB()));
		initAttribute(new BAttributeLineStyle(
				BAttributeLineStyle.SOLID_CONNECTION));
		initAttribute(new BAttributeLineWidth(1));

	}

}
