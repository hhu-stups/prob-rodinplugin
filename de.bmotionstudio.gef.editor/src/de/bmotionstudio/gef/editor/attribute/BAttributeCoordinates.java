/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.AttributeConstants;

public class BAttributeCoordinates extends AbstractAttribute {

	public BAttributeCoordinates(Object value) {
		super(value);
	}

	@Override
	public PropertyDescriptor preparePropertyDescriptor() {
		PropertyDescriptor descriptor = new PropertyDescriptor(getID(),
				getName());
		descriptor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				Point point = (Point) element;
				StringBuffer buf = new StringBuffer();
				buf.append("[");
				buf.append(point.x);
				buf.append(",  ");
				buf.append(point.y);
				buf.append("]");
				return buf.toString();
			}
		});
		return descriptor;
	}

	@Override
	public Object getEditableValue() {
		int x = Integer.valueOf(getChildren()
				.get(AttributeConstants.ATTRIBUTE_X).getValue().toString());
		int y = Integer.valueOf(getChildren()
				.get(AttributeConstants.ATTRIBUTE_Y).getValue().toString());
		return new Point(x, y);
	}

	@Override
	public String getName() {
		return "a4:Coordinates";
	}

}
