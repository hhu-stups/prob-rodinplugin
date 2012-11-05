/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import de.bmotionstudio.gef.editor.model.BControl;

public class BAttributeID extends AbstractAttribute {

	public BAttributeID(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		TextPropertyDescriptor descriptor = new TextPropertyDescriptor(getID(),
				getName());
		return descriptor;
	}

	@Override
	public String validateValue(Object value, BControl control) {
		if (((String) value).trim().length() == 0) {
			return "Value must not be empty string";
		}
		if (!(String.valueOf(value)).trim().matches("^[a-zA-Z_0-9]*")) {
			return "Special characters are not allowed.";
		}
		if (control.getVisualization().checkIfIdExists((String) value)) {
			return "ID already exists";
		}
		return null;
	}

	@Override
	public String getName() {
		return "a1:ID";
	}

}
