/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class SliderPropertyDescriptor extends PropertyDescriptor {

	public SliderPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new SliderCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

}
