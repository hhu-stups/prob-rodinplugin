/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class BMotionWizardAddItemAction extends Action {

	private TableViewer viewer;
	private Class<?> itemClass;

	public BMotionWizardAddItemAction(TableViewer viewer, Class<?> itemClass) {
		this.viewer = viewer;
		this.itemClass = itemClass;
		setText("Add new item");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/new_wiz.gif"));
	}

	@Override
	public void run() {

		try {
			IStructuredSelection sel = (IStructuredSelection) viewer
					.getSelection();
			Object firstElement = sel.getFirstElement();
			WritableList list = (WritableList) viewer.getInput();
			int indexOf = list.size();
			if (firstElement != null)
				indexOf = list.indexOf(firstElement) + 1;
			Object newInstance = itemClass.newInstance();
			list.add(indexOf, newInstance);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

}
