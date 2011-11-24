/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.scheduler.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.BindingObject;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.eventb.EventBHelper;
import de.bmotionstudio.gef.editor.eventb.MachineContentObject;
import de.bmotionstudio.gef.editor.eventb.MachineOperation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.IntegerCellEditor;
import de.bmotionstudio.gef.editor.scheduler.ExecuteOperationByPredicateMulti;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;
import de.bmotionstudio.gef.editor.scheduler.SchedulerWizard;

/**
 * @author Lukas Ladenberger
 * 
 */
public class WizardExecuteOperationByPredicateMulti extends SchedulerWizard {

	private class MultiPage extends WizardPage {

		private TableViewer tableViewer;

		protected MultiPage(String pageName) {
			super(pageName);
		}

		public void createControl(final Composite parent) {

			DataBindingContext dbc = new DataBindingContext();

			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(1, true));

			setControl(container);

			tableViewer = new TableViewer(container, SWT.BORDER
					| SWT.FULL_SELECTION);
			tableViewer.getTable().setLinesVisible(true);
			tableViewer.getTable().setHeaderVisible(true);
			tableViewer.getTable().setLayoutData(
					new GridData(GridData.FILL_BOTH));
			tableViewer.getTable().setFont(
					new Font(Display.getDefault(), new FontData("Arial", 10,
							SWT.NONE)));

			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.NONE);
			column.getColumn().setText("Execute Rule");
			column.getColumn().setWidth(190);
			column.setEditingSupport(new PredicateEditingSupport(tableViewer,
					dbc, "executePredicate", getBControl().getVisualization(),
					getShell()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Operation");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new OperationValueEditing(tableViewer,
					getBControl()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Parameter");
			column.getColumn().setWidth(190);
			column.setEditingSupport(new PredicateEditingSupport(tableViewer,
					dbc, "predicate", getBControl().getVisualization(),
					getShell()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Random Ops");
			column.getColumn().setWidth(100);
			column.setEditingSupport(new RandomModeEditingSupport(tableViewer));

			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			tableViewer.setContentProvider(contentProvider);

			tableViewer.setLabelProvider(new ObservableMapLabelProvider(
					BeansObservables.observeMaps(
							contentProvider.getKnownElements(), new String[] {
									"executePredicate", "operationName",
									"predicate", "maxrandom" })));
			final WritableList input = new WritableList(
					((ExecuteOperationByPredicateMulti) getScheduler())
							.getOperationList(),
					PredicateOperation.class);
			tableViewer.setInput(input);

			Composite comp = new Composite(container, SWT.NONE);
			comp.setLayout(new RowLayout());
			comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

			Button btRemove = new Button(comp, SWT.PUSH);
			btRemove.setText("Remove");
			btRemove.setImage(BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_DELETE));
			btRemove.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (tableViewer.getSelection().isEmpty()) {
						return;
					}
					PredicateOperation obj = (PredicateOperation) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
					input.remove(obj);
				}
			});

			Button btAdd = new Button(comp, SWT.PUSH);
			btAdd.setText("Add");
			btAdd.setImage(BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_ADD));
			btAdd.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					PredicateOperation obj = new PredicateOperation();
					input.add(obj);
				}
			});

		}
	}

	public WizardExecuteOperationByPredicateMulti(BControl bcontrol,
			SchedulerEvent scheduler) {
		super(bcontrol, scheduler);
		addPage(new MultiPage("MultiPage"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bmotionstudio.gef.editor.scheduler.SchedulerWizard#prepareToFinish()
	 */
	@Override
	protected Boolean prepareToFinish() {
		if (((ExecuteOperationByPredicateMulti) getScheduler())
				.getOperationList().size() == 0) {
			setEventDelete(true);
		} else {
			for (BindingObject obj : ((ExecuteOperationByPredicateMulti) getScheduler())
					.getOperationList()) {
				if (((PredicateOperation) obj).getOperationName() == null
						|| ((PredicateOperation) obj).getOperationName()
								.isEmpty()) {
					MessageDialog
							.openError(getShell(), "Please check your entries",
									"Please check your entries. The operation field must not be empty.");
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.scheduler.SchedulerWizard#getSize()
	 */
	@Override
	public Point getSize() {
		return new Point(700, 500);
	}

	private static class RandomModeEditingSupport extends EditingSupport {

		private CellEditor cellEditor;

		public RandomModeEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (value != null) {
				Integer maxnr = Integer.valueOf(value.toString());
				PredicateOperation obj = (PredicateOperation) element;
				obj.setMaxrandom(maxnr);
				if (maxnr > 1)
					obj.setRandom(true);
				else
					obj.setRandom(false);
			}
		}

		@Override
		protected Object getValue(Object element) {
			return ((PredicateOperation) element).getMaxrandom();
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			if (cellEditor == null) {
				cellEditor = new IntegerCellEditor((Composite) getViewer()
						.getControl());
			}
			return cellEditor;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

	}

	private static class OperationValueEditing extends EditingSupport {

		private ComboBoxViewerCellEditor cellEditor = null;

		private final BControl control;

		public OperationValueEditing(final TableViewer cv,
				final BControl control) {
			super(cv);
			this.control = control;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected Object getValue(final Object element) {
			return ((PredicateOperation) element).getOperationName();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((PredicateOperation) element).setOperationName(value
						.toString());
			}
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			if (cellEditor == null) {
				cellEditor = new ComboBoxViewerCellEditor(
						(Composite) getViewer().getControl(), SWT.READ_ONLY);
				cellEditor
						.setContentProvider(new ObservableListContentProvider());
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

	// private class ObserverLabelProvider extends ObservableMapLabelProvider
	// implements ITableLabelProvider, ITableColorProvider,
	// ITableFontProvider {
	//
	// public ObserverLabelProvider(final IObservableMap[] attributeMaps) {
	// super(attributeMaps);
	// }
	//
	// @Override
	// public Image getColumnImage(final Object element, final int columnIndex)
	// {
	// if (columnIndex == 3) {
	// return CheckboxCellEditorHelper
	// .getCellEditorImage(((PredicateOperation) element)
	// .isRandom());
	// }
	// return null;
	// }
	//
	// @Override
	// public String getColumnText(final Object element, final int columnIndex)
	// {
	//
	// if (columnIndex == 3)
	// return "";
	//
	// return super.getColumnText(element, columnIndex);
	//
	// }
	//
	// public Color getBackground(final Object element, final int column) {
	// return null;
	// }
	//
	// public Color getForeground(final Object element, final int column) {
	// return null;
	// }
	//
	// public Font getFont(final Object element, final int column) {
	// return null;
	// }
	//
	// }

}
