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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.edit.TextEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.SwitchChildCoordinates;
import de.bmotionstudio.gef.editor.observer.ToggleObjectCoordinates;
import de.bmotionstudio.gef.editor.util.BMotionWizardUtil;

public class WizardObserverCSwitchCoordinates extends ObserverWizard {

	private TableViewer tableViewer;

	@Override
	protected Control createContents(Composite parent) {

		DataBindingContext dbc = new DataBindingContext();

		GridLayout gl = new GridLayout(1, true);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;

		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(gl);

		tableViewer = BMotionWizardUtil.createBMotionWizardTableViewer(
				container, ToggleObjectCoordinates.class, getName());

		TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Predicate");
		column.getColumn().setWidth(100);
		column.setEditingSupport(new PredicateEditingSupport(tableViewer, dbc,
				"eval", getBControl().getVisualization(), getShell()));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Control");
		column.getColumn().setWidth(175);
		column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
				"bcontrol"));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("X");
		column.getColumn().setWidth(125);
		column.setEditingSupport(new TextEditingSupport(tableViewer, dbc, "x"));

		column = new TableViewerColumn(tableViewer, SWT.NONE);
		column.getColumn().setText("Y");
		column.getColumn().setWidth(125);
		column.setEditingSupport(new TextEditingSupport(tableViewer, dbc, "y"));

		ObservableListContentProvider contentProvider = new ObservableListContentProvider();
		tableViewer.setContentProvider(contentProvider);
		tableViewer.setLabelProvider(new ObserverLabelProvider(BeansObservables
				.observeMaps(contentProvider.getKnownElements(), new String[] {
						"eval", "bcontrol", "x", "y", "animate" })));

		final WritableList input = new WritableList(
				((SwitchChildCoordinates) getObserver()).getToggleObjects(),
				ToggleObjectCoordinates.class);
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
				.getImage(EditorImageRegistry.IMG_ICON_NEW_WIZ));
		btAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ToggleObjectCoordinates toggleObj = new ToggleObjectCoordinates(
						"", "", "", "");
				input.add(toggleObj);
			}
		});

		return container;

	}

	public WizardObserverCSwitchCoordinates(Shell shell, BControl bcontrol,
			Observer bobserver) {
		super(shell, bcontrol, bobserver);
	}

	// @Override
	// protected Boolean prepareToFinish() {
	// // restorePreview();
	// if (((SwitchChildCoordinates) getObserver()).getToggleObjects().size() ==
	// 0) {
	// setObserverDelete(true);
	// } else {
	// for (ToggleObjectCoordinates obj : ((SwitchChildCoordinates)
	// getObserver())
	// .getToggleObjects()) {
	// if (obj.getX().isEmpty() || obj.getY().isEmpty()
	// || obj.getBcontrol().isEmpty()) {
	// MessageDialog
	// .openError(getShell(), "Please check your entries",
	// "Please check your entries. The x , y and control fields must not be empty.");
	// return false;
	// }
	// }
	// }
	// return true;
	// }

	// private void restorePreview() {
	// if (lastChangedControlID != null) {
	// BControl bControl = getBControl().getChild(lastChangedControlID);
	// if (bControl != null) {
	// bControl.restoreDefaultValue(AttributeConstants.ATTRIBUTE_X);
	// bControl.restoreDefaultValue(AttributeConstants.ATTRIBUTE_Y);
	// }
	// }
	// }

	// @Override
	// public boolean performCancel() {
	// // restorePreview();
	// return super.performCancel();
	// }

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
			if (columnIndex == 4) {
				return "";
			}
			return super.getColumnText(element, columnIndex);
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			// if (columnIndex == 4) {
			// return CheckboxCellEditorHelper
			// .getCellEditorImage(((ToggleObjectCoordinates) element)
			// .getAnimate());
			// }
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
