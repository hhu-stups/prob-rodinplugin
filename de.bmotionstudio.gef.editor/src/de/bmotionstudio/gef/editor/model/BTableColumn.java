/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.draw2d.ColorConstants;

import de.bmotionstudio.gef.editor.attribute.BAttributeForegroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeHeight;
import de.bmotionstudio.gef.editor.attribute.BAttributeSize;
import de.bmotionstudio.gef.editor.attribute.BAttributeVisible;

public class BTableColumn extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.tablecolumn";

	public BTableColumn(Visualization visualization) {
		super(visualization);
	}

	@Override
	protected void initAttributes() {

		BAttributeForegroundColor aForegroundColor = new BAttributeForegroundColor(
				ColorConstants.black.getRGB());
		aForegroundColor.setEditable(true);
		aForegroundColor.setShow(false);
		initAttribute(aForegroundColor);

		BAttributeHeight aHeight = new BAttributeHeight(0);
		aHeight.setGroup(BAttributeSize.ID);
		aHeight.setShow(false);
		aHeight.setEditable(false);
		initAttribute(aHeight);

		BAttributeVisible aVisible = new BAttributeVisible(true);
		aVisible.setShow(false);
		aVisible.setEditable(false);
		initAttribute(aVisible);

	}

	@Override
	public String getType() {
		return TYPE;
	}

}