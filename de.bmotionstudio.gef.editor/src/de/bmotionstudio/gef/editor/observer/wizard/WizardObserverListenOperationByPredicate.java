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
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxViewerCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.edit.AttributeExpressionEdittingSupport;
import de.bmotionstudio.gef.editor.edit.IsExpressionModeEditingSupport;
import de.bmotionstudio.gef.editor.edit.OperationValueEditingSupport;
import de.bmotionstudio.gef.editor.edit.PredicateEditingSupport;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.ListenOperationByPredicate;
import de.bmotionstudio.gef.editor.observer.Observer;
import de.bmotionstudio.gef.editor.observer.ObserverWizard;
import de.bmotionstudio.gef.editor.property.CheckboxCellEditorHelper;
import de.bmotionstudio.gef.editor.scheduler.PredicateOperation;

public class WizardObserverListenOperationByPredicate extends ObserverWizard {

	private class ObserverListenOperationByPredicatePage extends WizardPage {

		private TableViewer tableViewer;

		protected ObserverListenOperationByPredicatePage(final String pageName) {
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
			column.getColumn().setText("Operation");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new OperationValueEditingSupport(
					tableViewer, getBControl()));

			column = new TableViewerColumn(tableViewer, SWT.NONE);
			column.getColumn().setText("Predicate");
			column.getColumn().setWidth(150);
			column.setEditingSupport(new PredicateEditingSupport(tableViewer,
					dbc, "predicate", getBControl().getVisualization(),
					getShell()));

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

			// MathTableViewerColumn columnEval = new MathTableViewerColumn(
			// tableViewer, column, dbc, "predicate");
			// columnEval.addErrorMessageListener(new IMessageListener() {
			// public void setMsg(final String errorMsg) {
			// if (errorMsg != null) {
			// setErrorMessage(errorMsg);
			// } else {
			// setErrorMessage(null);
			// setMessage(getObserver().getDescription());
			// }
			// }
			// });

			ObservableListContentProvider contentProvider = new ObservableListContentProvider();
			tableViewer.setContentProvider(contentProvider);
			tableViewer.setLabelProvider(new ObserverLabelProvider(
					BeansObservables.observeMaps(
							contentProvider.getKnownElements(), new String[] {
									"operationName", "predicate", "attribute",
									"value", "isExpressionMode" })));
			final WritableList input = new WritableList(
					((ListenOperationByPredicate) getObserver()).getList(),
					PredicateOperation.class);
			tableViewer.setInput(input);

			// ColumnViewerEditorActivationStrategy activationSupport = new
			// ColumnViewerEditorActivationStrategy(
			// tableViewer) {
			// protected boolean isEditorActivationEvent(
			// ColumnViewerEditorActivationEvent event) {
			// return event.eventType ==
			// ColumnViewerEditorActivationEvent.TRAVERSAL
			// || event.eventType ==
			// ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
			// || event.eventType ==
			// ColumnViewerEditorActivationEvent.PROGRAMMATIC
			// || (event.eventType ==
			// ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode ==
			// KeyLookupFactory
			// .getDefault().formalKeyLookup(
			// IKeyLookup.ENTER_NAME));
			// }
			// };
			// activationSupport.setEnableEditorActivationWithKeyboard(true);

			/*
			 * Without focus highlighter, keyboard events will not be delivered
			 * to
			 * ColumnViewerEditorActivationStragety#isEditorActivationEvent(...)
			 * (see above)
			 */
			// FocusCellHighlighter focusCellHighlighter = new
			// FocusCellOwnerDrawHighlighter(
			// tableViewer);
			// TableViewerFocusCellManager focusCellManager = new
			// TableViewerFocusCellManager(
			// tableViewer, focusCellHighlighter);

			// TableViewerEditor.create(tableViewer, focusCellManager,
			// activationSupport, ColumnViewerEditor.TABBING_VERTICAL
			// | ColumnViewerEditor.KEYBOARD_ACTIVATION);

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
					tableViewer.setSelection(new StructuredSelection(obj));
				}
			});

		}

		private class AttributeObserverValueEditing extends EditingSupport {

			private ComboBoxViewerCellEditor cellEditor = null;

			public AttributeObserverValueEditing(TableViewer cv) {
				super(cv);
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected Object getValue(Object element) {
				return ((PredicateOperation) element).getAttribute();
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (value != null) {
					PredicateOperation obj = (PredicateOperation) element;
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

										PredicateOperation p = (PredicateOperation) selection
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

	public WizardObserverListenOperationByPredicate(final BControl bcontrol,
			final Observer bobserver) {
		super(bcontrol, bobserver);
		addPage(new ObserverListenOperationByPredicatePage(
				"ObserverListenOperationByPredicatePage"));
	}

	@Override
	protected Boolean prepareToFinish() {
		return true;
	}

	@Override
	public Point getSize() {
		return new Point(800, 500);
	}

	private class ObserverLabelProvider extends ObservableMapLabelProvider
			implements ITableLabelProvider, ITableColorProvider,
			ITableFontProvider {

		public ObserverLabelProvider(IObservableMap[] attributeMaps) {
			super(attributeMaps);
		}

		// private final Color errorColor = Display.getDefault().getSystemColor(
		// SWT.COLOR_INFO_BACKGROUND);

		// final Font bold = JFaceResources.getFontRegistry().getBold(
		// JFaceResources.BANNER_FONT);

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 4) {
				return CheckboxCellEditorHelper
						.getCellEditorImage(((PredicateOperation) element)
								.isExpressionMode());
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {

			PredicateOperation obj = (PredicateOperation) element;

			if (columnIndex == 2) {

				String atrID = obj.getAttribute();
				String atrName = "";
				if (atrID != null) {
					if (atrID.length() > 0) {
						atrName = getBControl().getAttributes().get(atrID)
								.getName();
					}
				}
				return atrName;

			}
			if (columnIndex == 3) {

				if (obj.getValue() != null)
					return obj.getValue().toString();
				return "";

			}

			if (columnIndex == 4)
				return "";

			return super.getColumnText(element, columnIndex);
		}

		public Color getBackground(final Object element, final int column) {
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
