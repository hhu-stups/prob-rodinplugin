/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */
package de.bmotionstudio.gef.editor.observer.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

public class WizardObserverDragListener implements DragSourceListener {

	private final TableViewer viewer;

	public WizardObserverDragListener(TableViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void dragFinished(DragSourceEvent event) {
	}

	@Override
	public void dragSetData(DragSourceEvent event) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		Object[] lobjects = selection.toArray();
		event.data = lobjects;
	}

	@Override
	public void dragStart(DragSourceEvent event) {
	}

}