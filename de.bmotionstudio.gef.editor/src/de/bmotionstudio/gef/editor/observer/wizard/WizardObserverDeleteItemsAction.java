/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer.wizard;

import java.util.Arrays;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class WizardObserverDeleteItemsAction extends Action {

	private TableViewer viewer;

	public WizardObserverDeleteItemsAction(TableViewer viewer) {
		this.viewer = viewer;
		setText("Delete selected items");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/delete_edit.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Object[] lobjects = sel.toArray();

		if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				"Please confirm", "Do you realy want to delete these objects?")) {
			WritableList list = (WritableList) viewer.getInput();
			list.removeAll(Arrays.asList(lobjects));
		}

	}

}
