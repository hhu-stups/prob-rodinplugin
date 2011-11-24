/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eventb.core.IMachineRoot;
import org.eventb.core.ISCEvent;
import org.eventb.core.ISCGuard;
import org.eventb.core.ISCMachineRoot;
import org.eventb.core.ISCParameter;
import org.rodinp.core.IRodinFile;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.eventb.EventBHelper;
import de.bmotionstudio.gef.editor.eventb.MachineContentObject;
import de.bmotionstudio.gef.editor.eventb.MachineOperation;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.property.IntegerCellEditor;
import de.bmotionstudio.gef.editor.scheduler.AnimationScriptObject;
import de.bmotionstudio.gef.editor.scheduler.AnimationScriptStep;
import de.prob.logging.Logger;

public class AnimationScriptObjectDialog extends Dialog {

	private ISCMachineRoot getCorrespondingFile(IFile file,
			String machineFileName) {
		IRodinProject myProject = RodinCore.valueOf(file.getProject());
		IRodinFile rodinSource = myProject.getRodinFile(machineFileName);
		ISCMachineRoot machineRoot = ((IMachineRoot) rodinSource.getRoot())
				.getSCMachineRoot();
		return machineRoot;
	}

	public List<MachineContentObject> getOperations(Visualization visualization) {

		ISCMachineRoot machineRoot = null;

		machineRoot = getCorrespondingFile(visualization.getProjectFile(),
				visualization.getMachineName());

		ISCEvent[] events = null;
		ArrayList<MachineContentObject> tmpSet = new ArrayList<MachineContentObject>();

		try {
			events = machineRoot.getSCEvents();

			for (ISCEvent event : events) {

				Vector<String> parSet = new Vector<String>();
				Vector<String> guardSet = new Vector<String>();

				for (ISCParameter par : event.getSCParameters()) {
					parSet.insertElementAt(par.getIdentifierString(),
							parSet.size());
				}

				for (ISCGuard guard : event.getSCGuards()) {
					guardSet.insertElementAt(guard.getPredicateString(),
							guardSet.size());
				}

				MachineOperation op = new MachineOperation(event.getLabel(),
						parSet, guardSet);
				tmpSet.add(op);

			}
		} catch (RodinDBException e) {
			String message = "Rodin DB Exception while getting operations: "
					+ e.getLocalizedMessage();
			Logger.notifyUser(message, e);
			return Collections
					.unmodifiableList(new ArrayList<MachineContentObject>());
		}

		return tmpSet;

	}

	private TableViewer tableViewer;

	private final BControl control;

	private AnimationScriptObject animationScriptObject;

	public AnimationScriptObjectDialog(Shell parentShell, BControl control,
			AnimationScriptObject animationScriptObject) {
		super(parentShell);
		this.control = control;
		this.animationScriptObject = animationScriptObject;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		DataBindingContext dbc = new DataBindingContext();

		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(1, true));

		tableViewer = new TableViewer(container, SWT.BORDER
				| SWT.FULL_SELECTION);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		tableViewer.getTable().setFont(
				new Font(Display.getDefault(), new FontData("Arial", 10,
						SWT.NONE)));

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Operation");
		column.getColumn().setWidth(175);
		column.setEditingSupport(new OperationValueEditing(tableViewer, control));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Predicate");
		column.getColumn().setWidth(300);
		column.setEditingSupport(new PredicateEditingSupport(tableViewer, dbc,
				"parameter", control.getVisualization(), getShell()));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Random Ops");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new RandomModeEditingSupport(tableViewer));

		// column = new TableViewerColumn(tableViewer, SWT.NONE);
		// column.getColumn().setText("Callback");
		// column.getColumn().setWidth(100);
		// column
		// .setEditingSupport(new ObserverCallbackEditingSupport(
		// tableViewer));

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(
				BeansObservables.observeMaps(
						contentProvider.getKnownElements(), new String[] {
								"command", "parameter", "maxrandom" })) {

			@Override
			public String getColumnText(final Object element,
					final int columnIndex) {
				// if (columnIndex == 2) {
				// return "Edit";
				// }
				return super.getColumnText(element, columnIndex);
			}

			@Override
			public Image getColumnImage(final Object element,
					final int columnIndex) {
				return null;
			}

		});

		final WritableList input = new WritableList(
				animationScriptObject.getSteps(), AnimationScriptStep.class);

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
			public void widgetSelected(final SelectionEvent e) {
				if (tableViewer.getSelection().isEmpty()) {
					return;
				}
				AnimationScriptStep obj = (AnimationScriptStep) ((IStructuredSelection) tableViewer
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
			public void widgetSelected(final SelectionEvent e) {
				AnimationScriptStep obj = new AnimationScriptStep();
				input.add(obj);
			}
		});

		return container;

	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 500);
	}

	@Override
	protected void okPressed() {
		close();
	}

	@Override
	protected void configureShell(final Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("BMotion Studio - Scheduler Editor");
	}

	public void setAnimationScriptObject(
			final AnimationScriptObject animationScriptObject) {
		this.animationScriptObject = animationScriptObject;
	}

	public AnimationScriptObject getAnimationScriptObject() {
		return animationScriptObject;
	}

	// private class ObserverCallbackEditingSupport extends EditingSupport {
	//
	// public ObserverCallbackEditingSupport(ColumnViewer viewer) {
	// super(viewer);
	// }
	//
	// @Override
	// protected boolean canEdit(Object element) {
	// return true;
	// }
	//
	// @Override
	// protected CellEditor getCellEditor(Object element) {
	// return new ObserverCallbackCellEditor((Composite) getViewer()
	// .getControl(), (AnimationScriptStep) element);
	// }
	//
	// @Override
	// protected Object getValue(Object element) {
	// return "Edit";
	// }
	//
	// @Override
	// protected void setValue(Object element, Object value) {
	// }
	//
	// }

	// private class ObserverCallbackCellEditor extends DialogCellEditor {
	//
	// private final AnimationScriptStep step;
	//
	// public ObserverCallbackCellEditor(final Composite parent,
	// final AnimationScriptStep step) {
	// super(parent);
	// this.step = step;
	// }
	//
	// @Override
	// protected Object openDialogBox(final Control cellEditorWindow) {
	// ObserverCallBackDialog dialog = new ObserverCallBackDialog(
	// PlatformUI.getWorkbench().getActiveWorkbenchWindow()
	// .getShell(), step, control);
	// if (dialog.open() == Dialog.OK) {
	// return getValue();
	// }
	// return null;
	// }
	//
	// }

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
			return ((AnimationScriptStep) element).getCommand();
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((AnimationScriptStep) element).setCommand(value.toString());
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

	private static class RandomModeEditingSupport extends EditingSupport {

		private CellEditor cellEditor;

		public RandomModeEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		@Override
		protected void setValue(Object element, Object value) {
			if (value != null) {
				Integer maxnr = Integer.valueOf(value.toString());
				AnimationScriptStep obj = (AnimationScriptStep) element;
				obj.setMaxrandom(maxnr);
			}
		}

		@Override
		protected Object getValue(Object element) {
			return ((AnimationScriptStep) element).getMaxrandom();
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

}
