/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.library;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class DeleteItemsAction extends AbstractLibraryAction {

	public DeleteItemsAction(LibraryPage page) {
		super(page);
		setText("Delete selected items");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/delete_edit.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) getPage()
				.getTableViewer().getSelection();
		Object[] lobjects = sel.toArray();

		if (MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
				"Please confirm", "Do you realy want to delete these objects?")) {

			for (Object lobj : lobjects) {
				((LibraryObject) lobj).delete(getPage());
			}

			getPage().refresh();

			try {
				getPage().getEditor().getVisualization().getProjectFile()
						.getProject().getProject().refreshLocal(
								IResource.DEPTH_ONE, new NullProgressMonitor());
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}

	}

}
