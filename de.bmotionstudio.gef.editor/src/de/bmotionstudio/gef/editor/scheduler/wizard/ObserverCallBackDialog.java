/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.AnimationScriptStep;
import de.bmotionstudio.gef.editor.scheduler.ObserverCallBackObject;

@Deprecated
public class ObserverCallBackDialog extends Dialog {

	private TableViewer tableViewer;

	private final AnimationScriptStep animationScriptStep;

	private final BControl control;

	// private IObservableValue controlObservable;

	public ObserverCallBackDialog(final Shell parentShell,
			final AnimationScriptStep animationScriptStep,
			final BControl control) {
		super(parentShell);
		this.animationScriptStep = animationScriptStep;
		this.control = control;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {

		// DataBindingContext dbc = new DataBindingContext();

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
		column.getColumn().setText("Control");
		column.getColumn().setWidth(225);
		column.setEditingSupport(new ControlValueEditing(tableViewer, control));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Observer");
		column.getColumn().setWidth(150);
		// column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
		// "observerID"));
		column.setEditingSupport(new ObserverValueEditing(tableViewer, control));

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new ObservableMapLabelProvider(
				BeansObservables.observeMaps(
						contentProvider.getKnownElements(), new String[] {
								"control", "observerID" })) {

			@Override
			public String getColumnText(final Object element,
					final int columnIndex) {
				if (columnIndex == 0) {

					ObserverCallBackObject obj = (ObserverCallBackObject) element;

					if (obj.getControl() != null) {
						return obj.getControl().getID();
					}
				}
				return super.getColumnText(element, columnIndex);
			}

			@Override
			public Image getColumnImage(final Object element,
					final int columnIndex) {
				return null;
			}

		});

		final WritableList input = new WritableList(
				animationScriptStep.getCallBackList(),
				ObserverCallBackObject.class);

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
				ObserverCallBackObject obj = (ObserverCallBackObject) ((IStructuredSelection) tableViewer
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
				ObserverCallBackObject obj = new ObserverCallBackObject();
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
		newShell.setText("BMotion Studio - Observer Callback Editor");
	}

	private class ControlValueEditing extends EditingSupport {

		private ComboBoxViewerCellEditor cellEditor = null;

		private final BControl control;

		public ControlValueEditing(final TableViewer cv, final BControl control) {
			super(cv);
			this.control = control;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected Object getValue(final Object element) {
			if (((ObserverCallBackObject) element).getControl() != null) {
				return ((ObserverCallBackObject) element).getControl().getID();
			} else {
				return "";
			}
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((ObserverCallBackObject) element).setControl(control
						.getVisualization().getBControl(value.toString()));
			}
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {
			if (cellEditor == null) {
				cellEditor = new ComboBoxViewerCellEditor(
						(Composite) getViewer().getControl(), SWT.READ_ONLY);
				cellEditor
						.setContenProvider(new ObservableListContentProvider());
				cellEditor.setInput(new ComputedList() {
					@Override
					protected List<String> calculate() {
						ArrayList<String> tmpList = new ArrayList<String>();
						for (String controlID : control.getVisualization()
								.getAllBControlIDs()) {
							tmpList.add(controlID);
						}
						return tmpList;
					}
				});
				((CCombo) cellEditor.getControl())
						.addFocusListener(new FocusListener() {

							String oldValue;

							public void focusGained(final FocusEvent e) {
								oldValue = ((CCombo) cellEditor.getControl())
										.getText();

							}

							public void focusLost(final FocusEvent e) {

								if (!oldValue.equals(((CCombo) cellEditor
										.getControl()).getText())) {

									IStructuredSelection selection = (IStructuredSelection) getViewer()
											.getSelection();

									ObserverCallBackObject obj = (ObserverCallBackObject) selection
											.getFirstElement();
									obj.setObserverID("");
									tableViewer.refresh();

								}
							}

						});
			}
			return cellEditor;
		}
	}

	private static class ObserverValueEditing extends EditingSupport {

		private ComboBoxViewerCellEditor cellEditor = null;

		// private final BControl control;

		public ObserverValueEditing(final TableViewer cv, final BControl control) {
			super(cv);
			// this.control = control;
		}

		@Override
		protected boolean canEdit(final Object element) {
			return true;
		}

		@Override
		protected Object getValue(final Object element) {
			if (((ObserverCallBackObject) element).getObserverID() != null) {
				return ((ObserverCallBackObject) element).getObserverID();
			} else {
				return "";
			}
		}

		@Override
		protected void setValue(final Object element, final Object value) {
			if (value != null) {
				((ObserverCallBackObject) element).setObserverID(value
						.toString());
			}
		}

		@Override
		protected CellEditor getCellEditor(final Object element) {

			if (cellEditor == null) {
				cellEditor = new ComboBoxViewerCellEditor(
						(Composite) getViewer().getControl(), SWT.READ_ONLY);
				cellEditor
						.setContenProvider(new ObservableListContentProvider());
			}
			// cellEditor.setInput(new ComputedList() {
			// @Override
			// protected List<String> calculate() {
			//
			// ArrayList<String> tmpList = new ArrayList<String>();
			//
			// ObserverCallBackObject obj = (ObserverCallBackObject) element;
			// BControl control = obj.getControl();
			// if (control != null) {
			//
			// for (String id : control.getObservers().keySet()) {
			// tmpList.add(id);
			// }
			//
			// }
			//
			// return tmpList;
			//
			// }
			// });

			return cellEditor;
		}
	}

}
