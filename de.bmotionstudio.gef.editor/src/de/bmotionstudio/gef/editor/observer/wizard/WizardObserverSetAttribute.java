/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.observer.wizard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.ComputedList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
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
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.edit.AttributeExpressionEdittingSupport;
import de.bmotionstudio.gef.editor.edit.IsExpressionModeEditingSupport;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.observer.SetAttribute;
import de.bmotionstudio.gef.editor.observer.SetAttributeObject;
import de.bmotionstudio.gef.editor.property.CheckboxCellEditorHelper;
import de.bmotionstudio.gef.editor.util.WizardObserverUtil;

public class WizardObserverSetAttribute extends ObserverWizard {

	private String lastChangedAttributeID;

	private class WizardSetAttributePage extends WizardPage {

		private WritableList input;

		private TableViewer tableViewer;

		protected WizardSetAttributePage(final String pageName) {
			super(pageName);
		}

		public void createControl(Composite parent) {

			DataBindingContext dbc = new DataBindingContext();

			GridLayout gl = new GridLayout(1, true);
			gl.horizontalSpacing = 0;
			gl.verticalSpacing = 0;
			gl.marginHeight = 0;
			gl.marginWidth = 0;

			Composite container = new Composite(parent, SWT.NONE);
			container.setLayout(gl);

			tableViewer = WizardObserverUtil.createObserverWizardTableViewer(
					container, SetAttributeObject.class,
					(ObserverWizard) getWizard());

			tableViewer
					.addSelectionChangedListener(new ISelectionChangedListener() {

						@Override
						public void selectionChanged(SelectionChangedEvent event) {
							IStructuredSelection selection = (IStructuredSelection) event
									.getSelection();
							Object firstElement = selection.getFirstElement();
							if (firstElement instanceof ObserverEvalObject) {

								ObserverEvalObject observerEvalObject = (ObserverEvalObject) firstElement;
								BControl control = getBControl();

								if (lastChangedAttributeID != null)
									control.restoreDefaultValue(lastChangedAttributeID);

								SetAttributeObject setAttributeObj = (SetAttributeObject) observerEvalObject;
								String attribute = setAttributeObj
										.getAttribute();
								Object value = setAttributeObj.getValue();
								control.setAttributeValue(attribute, value);

								lastChangedAttributeID = attribute;

							}
						}

					});

			TableViewerColumn column = new TableViewerColumn(tableViewer,
					SWT.NONE);
			column.getColumn().setText("Predicate");
			column.getColumn().setWidth(300);

			PredicateEditingSupport pEditingSupport = new PredicateEditingSupport(
					tableViewer, dbc, "eval", getBControl().getVisualization(),
					getShell());
			column.setEditingSupport(pEditingSupport);

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Attribute");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new AttributeObserverValueEditing(
					tableViewer));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Value");
			column.getColumn().setWidth(175);
			column.setEditingSupport(new AttributeExpressionEdittingSupport(
					tableViewer, getBControl()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Expression?");
			column.getColumn().setWidth(100);
			column.setEditingSupport(new IsExpressionModeEditingSupport(
					tableViewer, getBControl()));

			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			tableViewer.setContentProvider(contentProvider);

			tableViewer.setLabelProvider(new ObserverLabelProvider(
					BeansObservables.observeMaps(
							contentProvider.getKnownElements(), new String[] {
									"eval", "attribute", "value",
									"isExpressionMode" })));
			input = new WritableList(
					((SetAttribute) getObserver()).getSetAttributeObjects(),
					SetAttributeObject.class);
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
						MessageDialog.openInformation(getShell(),
								"Please select an entry.",
								"Please select an entry.");
						return;
					}
					SetAttributeObject toggleObj = (SetAttributeObject) ((IStructuredSelection) tableViewer
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
					SetAttributeObject toggleObj = new SetAttributeObject(
							BParser.PREDICATE_PREFIX, "");
					input.add(toggleObj);
					tableViewer
							.setSelection(new StructuredSelection(toggleObj));
				}
			});

			setControl(container);

		}

		private class AttributeObserverValueEditing extends EditingSupport {

			private ComboBoxViewerCellEditor cellEditor = null;

			public AttributeObserverValueEditing(TableViewer cv) {
				super(cv);
			}

			@Override
			protected boolean canEdit(Object element) {
				return WizardObserverUtil.isEditElement(getViewer());
			}

			@Override
			protected Object getValue(Object element) {
				return ((SetAttributeObject) element).getAttribute();
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (value != null) {
					SetAttributeObject obj = (SetAttributeObject) element;
					obj.setAttribute(value.toString());
					obj.setIsExpressionMode(false);
				}
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (cellEditor == null) {

					cellEditor = new ComboBoxViewerCellEditor(
							(Composite) tableViewer.getControl(), SWT.READ_ONLY);
					cellEditor
							.setContentProvider(new ObservableListContentProvider());
					cellEditor.setLabelProvider(new LabelProvider() {
						public String getText(Object element) {
							return getBControl().getAttributes()
									.get(element.toString()).getName();
						}
					});
					cellEditor.setInput(new ComputedList() {
						@Override
						protected List<String> calculate() {
							ArrayList<String> atrList = new ArrayList<String>();
							for (AbstractAttribute atr : getBControl()
									.getAttributes().values()) {
								atrList.add(atr.getID());
							}
							return atrList;
						}
					});

					((CCombo) cellEditor.getControl())
							.addFocusListener(new FocusListener() {

								String oldValue;

								public void focusGained(FocusEvent e) {
									oldValue = ((CCombo) cellEditor
											.getControl()).getText();

								}

								public void focusLost(FocusEvent e) {

									if (!oldValue.equals(((CCombo) cellEditor
											.getControl()).getText())) {

										IStructuredSelection selection = (IStructuredSelection) getViewer()
												.getSelection();

										SetAttributeObject p = (SetAttributeObject) selection
												.getFirstElement();

										AbstractAttribute atr = getBControl()
												.getAttributes().get(
														p.getAttribute());

										p.setValue(atr.getValue());
										tableViewer.refresh();

									}
								}

							});

				}
				return cellEditor;
			}

		}

	}

	public WizardObserverSetAttribute(BControl control, Observer observer) {
		super(control, observer);
		addPage(new WizardSetAttributePage("WizardSetAttributePage"));
	}

	@Override
	public Point getSize() {
		return new Point(800, 500);
	}

	@Override
	protected Boolean prepareToFinish() {
		getBControl().restoreDefaultValue(lastChangedAttributeID);
		if (((SetAttribute) getObserver()).getSetAttributeObjects().size() == 0) {
			setObserverDelete(true);
		} else {
			for (SetAttributeObject obj : ((SetAttribute) getObserver())
					.getSetAttributeObjects()) {
				if (obj.getAttribute() == null) {
					MessageDialog
							.openError(getShell(), "Please check your entries",
									"Please check your entries. The attribute field must not be empty.");
					return false;
				}
			}
		}
		return true;
	}

	@Override
	public boolean performCancel() {
		getBControl().restoreDefaultValue(lastChangedAttributeID);
		return super.performCancel();
	}

	private class ObserverLabelProvider extends ObservableMapLabelProvider
			implements ITableLabelProvider, ITableColorProvider,
			ITableFontProvider {

		public ObserverLabelProvider(IObservableMap[] attributeMaps) {
			super(attributeMaps);
		}

		private final Color errorColor = Display.getDefault().getSystemColor(
				SWT.COLOR_INFO_BACKGROUND);

		// final Font bold = JFaceResources.getFontRegistry().getBold(
		// JFaceResources.BANNER_FONT);

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 3) {
				return CheckboxCellEditorHelper
						.getCellEditorImage(((SetAttributeObject) element)
								.isExpressionMode());
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			SetAttributeObject attributeObject = (SetAttributeObject) element;

			if (columnIndex == 1) {

				String atrID = attributeObject.getAttribute();
				String atrName = "";
				if (atrID != null) {
					if (atrID.length() > 0) {
						AbstractAttribute atr = getBControl().getAttributes()
								.get(atrID);
						if (atr != null)
							atrName = atr.getName();
					}
				}
				return atrName;

			}
			if (columnIndex == 2) {

				if (attributeObject.getValue() != null)
					return attributeObject.getValue().toString();
				return "";

			}

			if (columnIndex == 3)
				return "";

			return super.getColumnText(element, columnIndex);
		}

		public Color getBackground(final Object element, final int column) {
			SetAttributeObject attributeObject = (SetAttributeObject) element;
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
