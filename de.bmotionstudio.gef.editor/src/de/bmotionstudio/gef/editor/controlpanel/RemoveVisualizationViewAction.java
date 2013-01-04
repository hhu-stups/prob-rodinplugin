/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.controlpanel;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.VisualizationViewPart;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.VisualizationView;

public class RemoveVisualizationViewAction extends Action {

	private TreeViewer viewer;

	public RemoveVisualizationViewAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Remove View");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/delete_edit.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Object firstElement = sel.getFirstElement();

		if (firstElement instanceof VisualizationView) {

			VisualizationView visView = (VisualizationView) firstElement;

			ITreeSelection selection = ((ITreeSelection) viewer.getSelection());
			Object parent = selection.getPaths()[0].getParentPath()
					.getLastSegment();

			if (parent != null && parent instanceof Simulation) {

				Simulation simulation = (Simulation) parent;
				simulation.getVisualizationViews().remove(visView.getViewId());
				viewer.refresh();

				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();

				IViewReference viewReference = page.findViewReference(
						VisualizationViewPart.ID, visView.getViewId());

				page.hideView(viewReference);

			}

		}

	}

}
