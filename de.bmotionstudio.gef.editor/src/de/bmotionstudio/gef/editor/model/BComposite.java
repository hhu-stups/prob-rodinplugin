/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import org.eclipse.swt.graphics.RGB;

import de.bmotionstudio.gef.editor.attribute.BAttributeBackgroundColor;
import de.bmotionstudio.gef.editor.attribute.BAttributeImage;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BComposite extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.composite";

	public BComposite(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {

		initAttribute(new BAttributeBackgroundColor(new RGB(192, 192, 192)));
		initAttribute(new BAttributeImage(null));

	}

	@Override
	public boolean canHaveChildren() {
		return true;
	}

}
