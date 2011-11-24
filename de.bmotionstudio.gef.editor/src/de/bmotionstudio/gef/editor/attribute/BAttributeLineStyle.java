/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeLineStyle extends AbstractAttribute {

	public static final int SOLID_CONNECTION = 0;
	public static final int DASHED_CONNECTION = 1;
	public static final int DOTTED_CONNECTION = 2;
	public static final int DASHED_DOTTED_CONNECTION = 3;
	public static final int DASHED_DOTTED_DOTTED_CONNECTION = 4;

	public BAttributeLineStyle(Object value) {
		super(value);
	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"Solid", "Dash", "Dot", "Dash Dot", "Dash Dot Dot" });
	}

	@Override
	public String getName() {
		return "Line-Style";
	}

}
