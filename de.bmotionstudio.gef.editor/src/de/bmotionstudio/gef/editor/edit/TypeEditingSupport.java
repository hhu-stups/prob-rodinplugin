/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.edit;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.ObservableValueEditingSupport;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.widgets.Composite;

import de.be4.classicalb.core.parser.BParser;

public class TypeEditingSupport extends ObservableValueEditingSupport {

	private CellEditor cellEditor;
	private String propertyName;

	public TypeEditingSupport(ColumnViewer viewer, DataBindingContext dbc,
			String propertyName) {
		super(viewer, dbc);
		this.propertyName = propertyName;
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(
			CellEditor cellEditor) {
		return SWTObservables.observeSelection(cellEditor.getControl());
	}

	@Override
	protected IObservableValue doCreateElementObservable(Object element,
			ViewerCell cell) {
		return BeansObservables.observeValue(element, propertyName);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (cellEditor == null) {
			cellEditor = new ComboBoxCellEditor((Composite) getViewer()
					.getControl(), new String[] { BParser.PREDICATE_PREFIX,
					BParser.EXPRESSION_PREFIX });
		}
		return cellEditor;
	}

}
