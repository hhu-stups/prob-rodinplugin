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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public class IntEditingSupport extends ObservableValueEditingSupport {

	private CellEditor cellEditor;
	private String atr;

	public IntEditingSupport(TableViewer tv, DataBindingContext dbc, String atr) {
		super(tv, dbc);
		this.atr = atr;
		cellEditor = new TextCellEditor((Composite) tv.getControl());
		cellEditor.getControl().addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});
	}

	@Override
	protected IObservableValue doCreateCellEditorObservable(
			CellEditor cellEditor) {
		return SWTObservables.observeText(cellEditor.getControl(), SWT.Modify);
	}

	@Override
	protected IObservableValue doCreateElementObservable(Object element,
			ViewerCell cell) {
		return BeansObservables.observeValue(element, atr);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return cellEditor;
	}

}
