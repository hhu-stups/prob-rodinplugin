/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class AttributeSignalColor extends AbstractAttribute {

	public static final int RED = 0;
	public static final int GREEN = 1;
	public static final int NO_COLOR = 2;

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"Red", "Green", "No Color" });
	}

	@Override
	public Object unmarshal(final String s) {
		return Integer.valueOf(s);
	}

	@Override
	public String getName() {
		return "Signal Color";
	}

}
