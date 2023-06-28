/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.edit;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.bmotionstudio.gef.editor.eventb.EventBHelper;
import de.bmotionstudio.gef.editor.eventb.MachineContentObject;
import de.bmotionstudio.gef.editor.eventb.MachineOperation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

public class OperationValueEditingSupport extends EditingSupport {

	private ComboBoxViewerCellEditor cellEditor = null;

	private BControl control;

	public OperationValueEditingSupport(TableViewer cv, BControl control) {
		super(cv);
		this.control = control;
	}

	@Override
	protected boolean canEdit(Object element) {
		return BMotionWizardUtil.isEditElement(getViewer());
	}

	@Override
	protected Object getValue(Object element) {
		return ((PredicateOperation) element).getOperationName();
	}

	@Override
	protected void setValue(Object element, Object value) {
		if (value != null)
			((PredicateOperation) element).setOperationName(value.toString());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		if (cellEditor == null) {
			cellEditor = new ComboBoxViewerCellEditor((Composite) getViewer()
					.getControl(), SWT.READ_ONLY);
			cellEditor.setContentProvider(new ObservableListContentProvider());
			cellEditor.setInput(new ComputedList() {
				@Override
				protected List<String> calculate() {
					ArrayList<String> tmpList = new ArrayList<String>();
					for (MachineContentObject op : EventBHelper
							.getOperations(control.getVisualization())) {
						tmpList.add(((MachineOperation) op).getLabel());
					}
					return tmpList;
				}
			});
		}
		return cellEditor;
	}

}
