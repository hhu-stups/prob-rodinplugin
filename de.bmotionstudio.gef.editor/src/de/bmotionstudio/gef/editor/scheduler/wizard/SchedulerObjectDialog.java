/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.typed.BeanProperties;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.OperationValueEditingSupport;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.property.IntegerCellEditor;
import de.bmotionstudio.gef.editor.scheduler.AnimationScriptObject;
import de.bmotionstudio.gef.editor.scheduler.AnimationScriptStep;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

public class SchedulerObjectDialog extends Dialog {

	private TableViewer tableViewer;

	private final BControl control;

	private AnimationScriptObject animationScriptObject;

	public SchedulerObjectDialog(Shell parentShell, BControl control,
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

		tableViewer = BMotionWizardUtil
				.createBMotionWizardTableViewer(container,
						AnimationScriptStep.class, "Scheduler Object Dialog");

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Operation");
		column.getColumn().setWidth(175);
		column.setEditingSupport(new OperationValueEditingSupport(tableViewer,
				control) {
			
			@Override
			protected Object getValue(Object element) {
				return ((AnimationScriptStep) element).getCommand();
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (value != null)
					((AnimationScriptStep) element).setCommand(value.toString());
			}
			
		});

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Predicate");
		column.getColumn().setWidth(300);
		column.setEditingSupport(new PredicateEditingSupport(tableViewer, dbc,
				"parameter", control.getVisualization(), getShell()));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Random Ops");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new RandomModeEditingSupport(tableViewer));

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(new IObservableMap[] {
			BeanProperties.value(AnimationScriptStep.class, "command").observeDetail(contentProvider.getKnownElements()),
			BeanProperties.value(AnimationScriptStep.class, "parameter").observeDetail(contentProvider.getKnownElements()),
			BeanProperties.value(AnimationScriptStep.class, "maxrandom").observeDetail(contentProvider.getKnownElements()),
		}) {

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
