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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.edit.TextEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.CloneObserver;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverCloneObject;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;

/**
 * @author Lukas Ladenberger
 * 
 */
public class WizardObserverClone extends ObserverWizard {

	public WizardObserverClone(BControl control, Observer observer) {
		super(control, observer);
		addPage(new WizardObserverClonePage("WizardObserverClonePage"));
	}

	private class WizardObserverClonePage extends WizardPage {

		private TableViewer tableViewer;

		protected WizardObserverClonePage(String pageName) {
			super(pageName);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt
		 * .widgets.Composite)
		 */
		@Override
		public void createControl(Composite parent) {

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
			column.getColumn().setText("Expression");
			column.getColumn().setWidth(200);
			column.setEditingSupport(new PredicateEditingSupport(tableViewer,
					dbc, "eval", getBControl().getVisualization(), getShell()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Control");
			column.getColumn().setWidth(175);
			column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
					"controlId"));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Count from");
			column.getColumn().setWidth(125);
			column.setEditingSupport(new TextEditingSupport(tableViewer, dbc,
					"counter"));

			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			tableViewer.setContentProvider(contentProvider);
			tableViewer.setLabelProvider(new ObserverLabelProvider(
					BeansObservables.observeMaps(
							contentProvider.getKnownElements(), new String[] {
									"eval", "controlId", "counter" })));

			final WritableList input = new WritableList(
					((CloneObserver) getObserver()).getObserverCloneObjects(),
					ObserverCloneObject.class);
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
					ObserverCloneObject obj = (ObserverCloneObject) ((IStructuredSelection) tableViewer
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
					ObserverCloneObject obj = new ObserverCloneObject();
					input.add(obj);
				}
			});

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.bmotionstudio.gef.editor.observer.ObserverWizard#prepareToFinish()
	 */
	@Override
	protected Boolean prepareToFinish() {
		if (((CloneObserver) getObserver()).getObserverCloneObjects().size() == 0) {
			setObserverDelete(true);
		} else {
			for (ObserverCloneObject obj : ((CloneObserver) getObserver())
					.getObserverCloneObjects()) {
				if (obj.getEval() == null || obj.getControlId() == null) {
					MessageDialog
							.openError(getShell(), "Please check your entries",
									"Please check your entries. The eval and control field must not be empty.");
					return false;
				}
			}
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.bmotionstudio.gef.editor.observer.ObserverWizard#getSize()
	 */
	@Override
	public Point getSize() {
		return new Point(700, 500);
	}

	private static class ObserverLabelProvider extends
			ObservableMapLabelProvider implements ITableLabelProvider,
			ITableColorProvider, ITableFontProvider {

		public ObserverLabelProvider(IObservableMap[] attributeMaps) {
			super(attributeMaps);
		}

		private final Color errorColor = Display.getDefault().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND);

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			ObserverCloneObject obj = (ObserverCloneObject) element;
			if (columnIndex == 0) {
				return obj.getEval();
			}
			if (columnIndex == 1) {
				return obj.getControlId();
			}
			return super.getColumnText(element, columnIndex);
		}

		public Color getBackground(final Object element, final int column) {
			ObserverCloneObject obj = (ObserverCloneObject) element;
			if (obj.hasError())
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
