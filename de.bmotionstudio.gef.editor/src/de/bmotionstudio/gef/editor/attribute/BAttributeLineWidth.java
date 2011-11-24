package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.IntegerPropertyDescriptor;

public class BAttributeLineWidth extends AbstractAttribute {

	public BAttributeLineWidth(Object value) {
		super(value);
	}

	public PropertyDescriptor preparePropertyDescriptor() {
		IntegerPropertyDescriptor descriptor = new IntegerPropertyDescriptor(
				getID(), getName());
		return descriptor;
	}

	@Override
	public String validateValue(Object value, BControl control) {
		if (!(String.valueOf(value)).trim().matches("\\d*")) {
			return "Value must be a number";
		}
		if ((String.valueOf(value)).trim().length() == 0) {
			return "Value must not be empty string";
		}
		return null;
	}

	@Override
	public String getName() {
		return "Line-Width";
	}

}
