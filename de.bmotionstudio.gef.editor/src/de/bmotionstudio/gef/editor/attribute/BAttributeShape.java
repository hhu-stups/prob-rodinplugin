/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeShape extends AbstractAttribute {

	public BAttributeShape(Object value) {
		super(value);
	}

	public static final int SHAPE_RECTANGLE = 0;
	public static final int SHAPE_OVAL = 1;
	public static final int SHAPE_TRIANGLE = 2;
	public static final int SHAPE_DIAMOND = 3;

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"Rectangle", "Oval", "Triangle", "Diamond" });
	}

	@Override
	public String getName() {
		return "Shape";
	}

}
