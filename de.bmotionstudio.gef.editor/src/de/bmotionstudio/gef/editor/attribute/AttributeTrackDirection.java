/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class AttributeTrackDirection extends AbstractAttribute {

	public static final int RIGHT = 0;
	public static final int LEFT = 1;

	public AttributeTrackDirection(Object value) {
		super(value);
	}

	@Override
	public Object unmarshal(final String s) {
		return Integer.valueOf(s);
	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"R", "L" });
	}

	@Override
	public String getName() {
		return "Track Direction";
	}

}
