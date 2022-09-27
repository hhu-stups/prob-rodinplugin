/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.Request;


public class AttributeRequest extends Request {

	private AttributeTransferObject attributeTransferObject;
	private Point dropLocation;

	public Point getDropLocation() {
		return this.dropLocation;
	}

	public void setDropLocation(Point dropLocation) {
		this.dropLocation = dropLocation;
	}

	public AttributeRequest() {
		super("change attribute");
	}

	public void setAttributeTransferObject(
			AttributeTransferObject attributeTransferObject) {
		this.attributeTransferObject = attributeTransferObject;
	}

	public AttributeTransferObject getAttributeTransferObject() {
		return attributeTransferObject;
	}

}
