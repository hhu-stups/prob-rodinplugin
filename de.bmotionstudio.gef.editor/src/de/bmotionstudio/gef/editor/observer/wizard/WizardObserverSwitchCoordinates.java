/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.be4.classicalb.core.parser.BParser;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.edit.TextEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.SwitchCoordinates;
import de.bmotionstudio.gef.editor.observer.ToggleObjectCoordinates;
import de.bmotionstudio.gef.editor.property.CheckboxCellEditorHelper;
import de.bmotionstudio.gef.editor.util.WizardObserverUtil;

public class WizardObserverSwitchCoordinates extends ObserverWizard {

	private class ObserverToggleCoordinatesPage extends WizardPage {

		private TableViewer tableViewer;

		protected ObserverToggleCoordinatesPage(final String pageName) {
			super(pageName);
		}

		public void createControl(Composite parent) {

			DataBindingContext dbc = new DataBindingContext();

			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(new GridLayout(1, true));

			tableViewer = WizardObserverUtil
					.createObserverWizardTableViewer(container);

			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.NONE);
			column.getColumn().setText("Predicate");
			column.getColumn().setWidth(200);
			column.setEditingSupport(new PredicateEditingSupport(tableViewer,
					dbc, "eval", getBControl().getVisualization(), getShell()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("X");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
					"x"));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Y");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
					"y"));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Animate?");
			column.getColumn().setWidth(75);
			column.setEditingSupport(new EditingSupport(tableViewer) {

				private CellEditor cellEditor = new CheckboxCellEditor(
						(Composite) tableViewer.getControl());

				@Override
				protected void setValue(Object element, Object value) {
					((ToggleObjectCoordinates) element).setAnimate(Boolean
							.valueOf(String.valueOf(value)));
				}

				@Override
				protected Object getValue(Object element) {
					Boolean b = ((ToggleObjectCoordinates) element)
							.getAnimate();
					return b != null ? b : false;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return cellEditor;
				}

				@Override
				protected boolean canEdit(Object element) {
					return true;
				}

			});

			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			tableViewer.setContentProvider(contentProvider);
			tableViewer.setLabelProvider(new ObserverLabelProvider(
					BeansObservables.observeMaps(
							contentProvider.getKnownElements(), new String[] {
									"eval", "x", "y", "animate" })));

			final WritableList input = new WritableList(
					((SwitchCoordinates) getObserver()).getToggleObjects(),
					ToggleObjectCoordinates.class);
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
					ToggleObjectCoordinates toggleObj = (ToggleObjectCoordinates) ((IStructuredSelection) tableViewer
							.getSelection()).getFirstElement();
					input.remove(toggleObj);
				}
			});

			Button btAdd = new Button(comp, SWT.PUSH);
			btAdd.setText("Add");
			btAdd.setImage(BMotionStudioImage
					.getImage(EditorImageRegistry.IMG_ICON_ADD));
			btAdd.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ToggleObjectCoordinates toggleObj = new ToggleObjectCoordinates(
							BParser.PREDICATE_PREFIX, "", "", "", false);
					input.add(toggleObj);
					tableViewer
							.setSelection(new StructuredSelection(toggleObj));
				}
			});

			setControl(container);

		}
	}

	public WizardObserverSwitchCoordinates(final BControl bcontrol,
			final Observer bobserver) {
		super(bcontrol, bobserver);
		addPage(new ObserverToggleCoordinatesPage(
				"ObserverToggleCoordinatesPage"));
	}

	@Override
	protected Boolean prepareToFinish() {
		if (((SwitchCoordinates) getObserver()).getToggleObjects().size() == 0) {
			setObserverDelete(true);
		} else {
			for (ToggleObjectCoordinates obj : ((SwitchCoordinates) getObserver())
					.getToggleObjects()) {
				if (obj.getX().isEmpty() || obj.getY().isEmpty()) {
					MessageDialog
							.openError(getShell(), "Please check your entries",
									"Please check your entries. The x and y fields must not be empty.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public Point getSize() {
		return new Point(650, 500);
	}

	private static class ObserverLabelProvider extends
			ObservableMapLabelProvider implements ITableLabelProvider,
			ITableColorProvider, ITableFontProvider {

		public ObserverLabelProvider(IObservableMap[] attributeMaps) {
			super(attributeMaps);
		}

		private final Color errorColor = Display.getDefault().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND);

		// final Font bold = JFaceResources.getFontRegistry().getBold(
		// JFaceResources.BANNER_FONT);

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (columnIndex == 3) {
				return "";
			}
			return super.getColumnText(element, columnIndex);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 3) {
				return CheckboxCellEditorHelper
						.getCellEditorImage(((ToggleObjectCoordinates) element)
								.getAnimate());
			}
			return null;
		}

		public Color getBackground(final Object element, final int column) {
			ToggleObjectCoordinates attributeObject = (ToggleObjectCoordinates) element;
			if (attributeObject.hasError())
				return errorColor;
			return null;
		}

		public Color getForeground(final Object element, final int column) {
			return null;
		}

		public Font getFont(final Object element, final int column) {
			// return JFaceResources.getFontRegistry().get(
			// BMotionStudioConstants.RODIN_FONT_KEY);
			return null;
		}

	}

}
