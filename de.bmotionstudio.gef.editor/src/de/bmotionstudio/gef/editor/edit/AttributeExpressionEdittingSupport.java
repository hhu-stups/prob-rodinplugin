/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

public class AttributeExpressionEdittingSupport extends EditingSupport {

	private CellEditor cellEditor;

	private BControl control;

	private String stdAttribute;

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control) {
		this(viewer, control, null);
	}

	public AttributeExpressionEdittingSupport(ColumnViewer viewer,
			BControl control, String stdAttribute) {
		super(viewer);
		this.control = control;
		this.stdAttribute = stdAttribute;
	}

	@Override
	protected boolean canEdit(Object element) {
		return BMotionWizardUtil.isEditElement(getViewer());
	}

	@Override
	protected Object getValue(Object element) {
		ObserverEvalObject evalObject = (ObserverEvalObject) element;
		return evalObject.getValue();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value == null)
			return;
		((ObserverEvalObject) element).setValue(value);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {

		ObserverEvalObject obj = ((ObserverEvalObject) element);

		if (obj.isExpressionMode()) {
			if (cellEditor == null) {
				cellEditor = new TextCellEditor((Composite) getViewer()
						.getControl());
			}
			return cellEditor;
		} else {

			String atrID = stdAttribute;

			if (atrID == null)
				atrID = obj.getAttribute();

			if (atrID != null) {
				if (atrID.length() > 0) {
					AbstractAttribute atr = getControl().getAttributes().get(
							atrID);
					PropertyDescriptor desc = atr.getPropertyDescriptor();
					return desc.createPropertyEditor((Composite) getViewer()
							.getControl());
				}
			}

		}

		return null;

	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

	public void setStdAttribute(String stdAttribute) {
		this.stdAttribute = stdAttribute;
	}

	public String getStdAttribute() {
		return stdAttribute;
	}

}
