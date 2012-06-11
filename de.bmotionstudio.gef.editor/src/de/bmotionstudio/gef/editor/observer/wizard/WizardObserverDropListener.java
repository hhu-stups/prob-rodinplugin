/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.observer.ObserverEvalObject;

public class WizardObserverDropListener extends ViewerDropAdapter {

	private String observerName;

	public WizardObserverDropListener(Viewer viewer, String observerName) {
		super(viewer);
		this.observerName = observerName;
	}

	@Override
	public void drop(DropTargetEvent event) {

		Object[] sourceSetAttributeObjects = (Object[]) event.data;
		Object targetSetAttributeObject = determineTarget(event);

		Object input = getViewer().getInput();
		if (input instanceof WritableList) {

			WritableList list = (WritableList) input;

			for (Object sourceObject : sourceSetAttributeObjects) {

				if (sourceObject instanceof ObserverEvalObject) {

					ObserverEvalObject sourceEvalObject = (ObserverEvalObject) sourceObject;

					if (sourceObject.getClass().equals(list.getElementType())) {

						int indexOf = list.indexOf(targetSetAttributeObject);
						if (indexOf == -1)
							indexOf = 0;
						ObserverEvalObject newElement = sourceEvalObject;
						if (!list.remove(sourceEvalObject)) {
							try {
								newElement = sourceEvalObject.clone();
							} catch (CloneNotSupportedException e) {
								e.printStackTrace();
							}
						}

						list.add(indexOf, newElement);

					} else {

						MessageDialog.openInformation(Display.getDefault()
								.getActiveShell(),
								"Drag and Drop is not supported",
								"It is not possible to add an item of the type "
										+ sourceEvalObject.getClass()
										+ " to the observer \"" + observerName
										+ "\".");

					}

				}

			}

		}

		super.drop(event);

	}

	@Override
	public boolean performDrop(Object data) {
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return true;

	}

}
