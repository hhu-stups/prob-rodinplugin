/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class BAttributeMisc extends AbstractAttribute {

	public BAttributeMisc(Object value) {
		super(value);
	}

	@Override
	public PropertyDescriptor preparePropertyDescriptor() {
		PropertyDescriptor descriptor = new PropertyDescriptor(getID(),
				getName());
		descriptor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				return "";
			}
		});
		return descriptor;
	}

	@Override
	public String getName() {
		return "Misc";
	}

}
