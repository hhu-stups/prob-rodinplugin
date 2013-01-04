/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.controlpanel;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.util.PerspectiveUtil;

public class CloseSimulationAction extends Action {

	private TreeViewer viewer;

	public CloseSimulationAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Close Simulation");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/dlcl16/close_view.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof Simulation) {

			IWorkbench workbench = PlatformUI.getWorkbench();
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
					.getActivePage();

			IPerspectiveRegistry perspectiveRegistry = workbench
					.getPerspectiveRegistry();

			Simulation simulation = (Simulation) firstElement;
			String perspectiveId = PerspectiveUtil
					.getPerspectiveIdFromFile(simulation.getProjectFile());
			IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry.findPerspectiveWithId(perspectiveId);
			if(perspectiveDescriptor != null) {
				PerspectiveUtil.closePerspective(page, perspectiveDescriptor);
				PerspectiveUtil.deletePerspective(page, perspectiveDescriptor);
			}
			
			BMotionEditorPlugin.closeSimulation(simulation);

		}

	}

}
