/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeConnectionSourceDecoration extends AbstractAttribute {

	public BAttributeConnectionSourceDecoration(Object value) {
		super(value);
	}

	public static int DECORATION_NONE = 0;
	public static int DECORATION_TRIANGLE = 1;

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"None", "Triangle" });
	}

	@Override
	public String getName() {
		return "Connection-Source-Decoration";
	}

}
