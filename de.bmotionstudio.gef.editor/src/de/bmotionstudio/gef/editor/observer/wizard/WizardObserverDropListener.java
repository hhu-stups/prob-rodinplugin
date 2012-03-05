/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;

public class WizardObserverDropListener extends ViewerDropAdapter {

	public WizardObserverDropListener(Viewer viewer) {
		super(viewer);
	}

	@Override
	public void drop(DropTargetEvent event) {
		Object sourceSetAttributeObject = event.data;
		Object targetSetAttributeObject = determineTarget(event);
		Object input = getViewer().getInput();
		if (input instanceof WritableList) {
			WritableList list = (WritableList) input;
			int indexOf = list.indexOf(targetSetAttributeObject);
			if (indexOf != -1) {
				list.remove(sourceSetAttributeObject);
				list.add(indexOf, sourceSetAttributeObject);
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
