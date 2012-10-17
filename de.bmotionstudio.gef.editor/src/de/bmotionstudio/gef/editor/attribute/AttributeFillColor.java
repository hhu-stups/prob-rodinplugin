/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.attribute;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;


public class AttributeFillColor extends AbstractAttribute {

	public PropertyDescriptor preparePropertyDescriptor() {
		return new ColorPropertyDescriptor(getID(), getName());
	}

	@Override
	public String getName() {
		return "Fill-Color";
	}

	@Override
	public Object unmarshal(String s) {

		String colorStr = s.toLowerCase().replace(" ", "");
		colorStr = colorStr.replace("rgb", "");
		colorStr = colorStr.replace("}", "");
		colorStr = colorStr.replace("{", "");
		String[] str = String.valueOf(colorStr).split("\\,");
		if (str.length == 3) {
			int red = Integer.valueOf(str[0]);
			int green = Integer.valueOf(str[1]);
			int blue = Integer.valueOf(str[2]);
			return new RGB(red, green, blue);
		} else {
			return new RGB(192, 192, 192);
		}

	}

}
