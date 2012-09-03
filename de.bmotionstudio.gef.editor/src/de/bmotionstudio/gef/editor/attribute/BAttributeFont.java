/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.property.FontPropertyDescriptor;

public class BAttributeFont extends AbstractAttribute {

	public PropertyDescriptor preparePropertyDescriptor() {
		return new FontPropertyDescriptor(getID(), getName());
	}

	@Override
	public String getName() {
		return "Font";
	}

}
