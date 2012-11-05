/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeCoordinates;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeWidth;

public class Light extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.light";

	public Light(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {

		BAttributeCoordinates aCoordinates = new BAttributeCoordinates(null);
		aCoordinates.setGroup(AbstractAttribute.ROOT);
		aCoordinates.setShow(false);
		aCoordinates.setEditable(false);
		initAttribute(aCoordinates);

		BAttributeSize aSize = new BAttributeSize(null);
		aSize.setGroup(AbstractAttribute.ROOT);
		aSize.setShow(false);
		aSize.setEditable(false);
		initAttribute(aSize);

		BAttributeHeight aHeight = new BAttributeHeight(12);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeWidth aWidth = new BAttributeWidth(12);
		aWidth.setGroup(BAttributeSize.ID);
		aWidth.setShow(false);
		aWidth.setEditable(false);
		initAttribute(aWidth);

		initAttribute(new BAttributeBackgroundColor(
				ColorConstants.lightGray.getRGB()));

	}

	@Override
	public String getType() {
		return TYPE;
	}

}
