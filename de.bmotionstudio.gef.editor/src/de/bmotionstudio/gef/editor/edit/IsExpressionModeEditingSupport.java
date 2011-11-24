/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;

public class IsExpressionModeEditingSupport extends EditingSupport {

	private CellEditor cellEditor;

	private BControl control;

	public IsExpressionModeEditingSupport(ColumnViewer viewer, BControl control) {
		super(viewer);
		this.control = control;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Boolean bol = Boolean.valueOf(String.valueOf(value));
		ObserverEvalObject obj = (ObserverEvalObject) element;
		obj.setIsExpressionMode(bol);
		if (obj.getAttribute() != null) {
			AbstractAttribute atr = getControl().getAttributes().get(
					obj.getAttribute());
			if (atr != null) {
				if (!bol) {
					obj.setValue(atr.getValue());
				} else {
					obj.setValue(atr.getValue().toString());
				}
			}
		}
	}

	@Override
	protected Object getValue(Object element) {
		Boolean b = ((ObserverEvalObject) element).isExpressionMode();
		return b != null ? b : false;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (cellEditor == null)
			cellEditor = new CheckboxCellEditor((Composite) getViewer()
					.getControl());
		return cellEditor;
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

}
