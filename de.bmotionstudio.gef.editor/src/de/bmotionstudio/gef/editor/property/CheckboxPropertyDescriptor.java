/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.property;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public class CheckboxPropertyDescriptor extends PropertyDescriptor {
	/**
	 * @param id
	 * @param displayName
	 */
	public CheckboxPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		init();
	}

	protected void init() {
		setLabelProvider(new LabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (element instanceof Boolean) {
					boolean b = (Boolean) element;
					return CheckboxCellEditorHelper.getCellEditorImage(b);
				}
				return super.getImage(element);
			}

			@Override
			public String getText(Object element) {
				if (element instanceof Boolean) {
					Boolean b = (Boolean) element;
					return b.toString();
				}
				return "";
			}
		});
	}

	/*
	 * @see
	 * org.eclipse.ui.views.properties.IPropertyDescriptor#createPropertyEditor
	 * (org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public CellEditor createPropertyEditor(Composite parent) {
		CellEditor editor = new CheckboxCellEditor(parent);
		return editor;
	}

}
