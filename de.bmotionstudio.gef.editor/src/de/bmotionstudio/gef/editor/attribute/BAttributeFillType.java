/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeFillType extends AbstractAttribute {

	public static final int FILLED = 0;
	public static final int EMPTY = 1;
	public static final int SHADED = 2;
	public static final int GRADIENT = 3;

	public BAttributeFillType(Object value) {
		super(value);
	}

	@Override
	protected PropertyDescriptor preparePropertyDescriptor() {
		return new ComboBoxPropertyDescriptor(getID(), getName(), new String[] {
				"Filled", "Empty", "Shaded", "Gradient" });
	}

	@Override
	public String getName() {
		return "Fill-Type";
	}

}
