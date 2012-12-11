/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.util;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableItem;

import de.bmotionstudio.gef.editor.action.BMotionWizardAddItemAction;
import de.bmotionstudio.gef.editor.action.BMotionWizardDeleteItemsAction;
import de.bmotionstudio.gef.editor.library.AttributeTransfer;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverDragListener;
import de.bmotionstudio.gef.editor.observer.wizard.WizardObserverDropListener;

public class BMotionWizardUtil {

	public static boolean isEditElement(ColumnViewer viewer) {
		Object data = viewer.getData("editElement");
		if (data != null)
			return Boolean.valueOf(data.toString());
		return false;
	}

	public static TableViewer createBMotionWizardTableViewer(Composite parent,
			Class<?> itemClass, final String wizardName) {

		final TableViewer tableViewer = new TableViewer(parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		tableViewer.setData("editElement", false);
		tableViewer.getTable().setLinesVisible(true);
		tableViewer.getTable().setHeaderVisible(true);
		tableViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		// tableViewer.getTable().setFont(BMotionStudioSWTConstants.fontArial10);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { AttributeTransfer
				.getInstance() };
		
		tableViewer.addDropSupport(operations, transferTypes,
				new WizardObserverDropListener(tableViewer, wizardName));
		tableViewer.addDragSupport(operations, transferTypes,
				new WizardObserverDragListener(tableViewer));

		MenuManager manager = new MenuManager();
		tableViewer.getControl().setMenu(
				manager.createContextMenu(tableViewer.getControl()));
		manager.add(new BMotionWizardDeleteItemsAction(tableViewer));
		manager.add(new BMotionWizardAddItemAction(tableViewer, itemClass));

		tableViewer.getTable().addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event event) {
				tableViewer.setData("editElement", false);
			}
		});

		tableViewer.getTable().addListener(SWT.MouseDoubleClick,
				new Listener() {

					public void handleEvent(Event event) {

						tableViewer.setData("editElement", true);
						TableItem[] selection = tableViewer.getTable()
								.getSelection();

						if (selection.length != 1) {
							return;
						}

						TableItem item = tableViewer.getTable().getSelection()[0];

						for (int i = 0; i < tableViewer.getTable()
								.getColumnCount(); i++) {
							if (item.getBounds(i).contains(event.x, event.y)) {
								tableViewer.editElement(item.getData(), i);
								tableViewer.setData("editElement", false);
								break;
							}
						}
					}

				});

		return tableViewer;

	}

}
