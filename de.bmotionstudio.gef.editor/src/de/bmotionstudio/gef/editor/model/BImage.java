/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import de.bmotionstudio.gef.editor.attribute.BAttributeAlpha;
import de.bmotionstudio.gef.editor.attribute.BAttributeImage;

/**
 * @author Lukas Ladenberger
 * 
 */
public class BImage extends BControl {

	public static transient String TYPE = "de.bmotionstudio.gef.editor.image";

	public BImage(Visualization visualization) {
		super(visualization);
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void initAttributes() {
		initAttribute(new BAttributeImage(null));
		initAttribute(new BAttributeAlpha(255));
	}

}
