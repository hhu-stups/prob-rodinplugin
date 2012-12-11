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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.AttributeExpressionEdittingSupport;
import de.bmotionstudio.gef.editor.edit.IsExpressionModeEditingSupport;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.SwitchImage;
import de.bmotionstudio.gef.editor.observer.ToggleObjectImage;
import de.bmotionstudio.gef.editor.property.CheckboxCellEditorHelper;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

public class WizardObserverSwitchImage extends ObserverWizard {

	private TableViewer tableViewer;

	@Override
	public Control createWizardContent(Composite parent) {

		parent.setLayout(new FillLayout());

		DataBindingContext dbc = new DataBindingContext();

		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(gl);

		tableViewer = BMotionWizardUtil.createBMotionWizardTableViewer(
				container, ToggleObjectImage.class, getName());

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Predicate");
		column.getColumn().setWidth(300);
		column.setEditingSupport(new PredicateEditingSupport(tableViewer, dbc,
				"eval", getBControl().getVisualization(), getShell()));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Image");
		column.getColumn().setWidth(180);
		column.setEditingSupport(new AttributeExpressionEdittingSupport(
				tableViewer, getBControl(), AttributeConstants.ATTRIBUTE_IMAGE) {

			@Override
			protected Object getValue(final Object element) {
				ToggleObjectImage evalObject = (ToggleObjectImage) element;
				return evalObject.getImage();
			}

			@Override
			protected void setValue(final Object element, final Object value) {
				if (value == null)
					return;
				((ToggleObjectImage) element).setImage(value.toString());
			}

		});

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Expression?");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new IsExpressionModeEditingSupport(
				tableViewer, getBControl()) {

			@Override
			protected void setValue(final Object element, final Object value) {
				Boolean bol = Boolean.valueOf(String.valueOf(value));
				ToggleObjectImage obj = (ToggleObjectImage) element;
				obj.setIsExpressionMode(bol);
			}

		});

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);

		tableViewer.setLabelProvider(new ObserverLabelProvider(BeansObservables
				.observeMaps(contentProvider.getKnownElements(), new String[] {
						"eval", "image", "isExpressionMode" })));
		final WritableList input = new WritableList(
				((SwitchImage) getObserver()).getToggleObjects(),
				ToggleObjectImage.class);
		tableViewer.setInput(input);

		Composite comp = new Composite(container, SWT.NONE);
		comp.setLayout(new RowLayout());
		comp.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Button btRemove = new Button(comp, SWT.PUSH);
		btRemove.setText("Remove");
		btRemove.setImage(BMotionStudioImage
				.getImage(EditorImageRegistry.IMG_ICON_DELETE_EDIT));
		btRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				if (tableViewer.getSelection().isEmpty()) {
					return;
				}
				ToggleObjectImage toggleObj = (ToggleObjectImage) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				input.remove(toggleObj);
			}
		});

		Button btAdd = new Button(comp, SWT.PUSH);
		btAdd.setText("Add");
		btAdd.setImage(BMotionStudioImage
				.getImage(EditorImageRegistry.IMG_ICON_NEW_WIZ));
		btAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				ToggleObjectImage toggleObj = new ToggleObjectImage("", "");
				input.add(toggleObj);
				tableViewer.setSelection(new StructuredSelection(toggleObj));
			}
		});

		return container;
	}

	public WizardObserverSwitchImage(Shell shell, BControl bcontrol,
			Observer bobserver) {
		super(shell, bcontrol, bobserver);
	}

	@Override
	public Point getSize() {
		return new Point(650, 500);
	}

	private static class ObserverLabelProvider extends
			ObservableMapLabelProvider implements ITableLabelProvider,
			ITableColorProvider, ITableFontProvider {

		public ObserverLabelProvider(final IObservableMap[] attributeMaps) {
			super(attributeMaps);
		}

		private final Color errorColor = Display.getDefault().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND);

		// final Font bold = JFaceResources.getFontRegistry().getBold(
		// JFaceResources.BANNER_FONT);

		@Override
		public Image getColumnImage(final Object element, final int columnIndex) {
			if (columnIndex == 2) {
				return CheckboxCellEditorHelper
						.getCellEditorImage(((ToggleObjectImage) element)
								.isExpressionMode());
			}
			return null;
		}

		@Override
		public String getColumnText(final Object element, final int columnIndex) {

			if (columnIndex == 2)
				return "";

			return super.getColumnText(element, columnIndex);

		}

		public Color getBackground(final Object element, final int column) {
			ToggleObjectImage attributeObject = (ToggleObjectImage) element;
			if (attributeObject.hasError())
				return errorColor;
			return null;
		}

		public Color getForeground(final Object element, final int column) {
			return null;
		}

		public Font getFont(final Object element, final int column) {
			return null;
		}

	}

}
