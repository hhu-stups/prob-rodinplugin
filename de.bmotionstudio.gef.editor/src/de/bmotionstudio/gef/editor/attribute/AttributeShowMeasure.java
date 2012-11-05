/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.CheckboxPropertyDescriptor;

public class AttributeShowMeasure extends AbstractAttribute {

	public AttributeShowMeasure(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		return new CheckboxPropertyDescriptor(getID(), getName());
	}

	@Override
	public String validateValue(Object value, BControl control) {
		if ((String.valueOf(value)).trim().equalsIgnoreCase("true")
				|| (String.valueOf(value)).trim().equalsIgnoreCase("false")) {
			return null;
		} else {
			return "Value must be a Boolean value (\"true\" or \"false\")";
		}
	}

	@Override
	public String getName() {
		return "Show-Measure";
	}

	@Override
	public Object unmarshal(String s) {
		return Boolean.valueOf(s);
	}

}
