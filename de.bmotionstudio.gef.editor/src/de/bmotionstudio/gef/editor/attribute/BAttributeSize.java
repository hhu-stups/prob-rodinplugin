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

public class BAttributeSize extends AbstractAttribute {

	public static final String ID = "de.bmotionstudio.gef.editor.attribute.BAttributeSize";

	@Override
	public PropertyDescriptor preparePropertyDescriptor() {
		PropertyDescriptor descriptor = new PropertyDescriptor(getID(),
				getName());
		descriptor.setLabelProvider(new LabelProvider() {
			public String getText(Object element) {
				Point point = (Point) element;
				StringBuffer buf = new StringBuffer();
				buf.append("[");
				if (point.x >= 0)
					buf.append(point.x);
				if (point.y >= 0) {
					buf.append(",  ");
					buf.append(point.y);
				}
				buf.append("]");
				return buf.toString();
			}
		});
		return descriptor;
	}

	@Override
	public Object getEditableValue() {

		AbstractAttribute atrWidth = getChildren().get(
				AttributeConstants.ATTRIBUTE_WIDTH);
		AbstractAttribute atrHeight = getChildren().get(
				AttributeConstants.ATTRIBUTE_HEIGHT);

		int width = -1;
		int height = -1;

		if (atrWidth != null)
			width = Integer.valueOf(atrWidth.getValue().toString());
		if (atrHeight != null)
			height = Integer.valueOf(atrHeight.getValue().toString());

		return new Point(width, height);

	}

	@Override
	public String getName() {
		return "a5:Size";
	}

}
