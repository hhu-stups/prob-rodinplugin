/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeDirection extends AbstractAttribute {

	public static final int NORTH = 0;
	public static final int SOUTH = 1;
	public static final int WEST = 2;
	public static final int EAST = 3;

	public BAttributeDirection(Object value) {
		super(value);
	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"NORTH", "SOUTH", "WEST", "EAST" });
	}

	@Override
	public String getName() {
		return "Direction";
	}

}
