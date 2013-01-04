/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.controlpanel;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.util.PerspectiveUtil;

public class SaveSimulationAction extends Action {

	private TreeViewer viewer;

	public SaveSimulationAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Save Simulation");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/save_edit.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof Simulation) {

			Simulation simulation = (Simulation) firstElement;

			IPerspectiveRegistry perspectiveRegistry = PlatformUI
					.getWorkbench().getPerspectiveRegistry();

			String perspectiveId = PerspectiveUtil
					.getPerspectiveIdFromFile(simulation.getProjectFile());
			IPerspectiveDescriptor perspectiveDescriptor = perspectiveRegistry
					.findPerspectiveWithId(perspectiveId);
			if (perspectiveDescriptor != null) {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
						.getActivePage()
						.savePerspectiveAs(perspectiveDescriptor);
				PerspectiveUtil.exportPerspective(simulation,
						perspectiveDescriptor);
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			OutputStreamWriter writer = null;
			try {
				// saveProperties();
				writer = new OutputStreamWriter(out, "UTF8");
				XStream xstream = new XStream();
				BMotionEditorPlugin.setAliases(xstream);
				xstream.toXML(simulation, writer);
				IFile file = simulation.getProjectFile();
				file.setContents(new ByteArrayInputStream(out.toByteArray()),
						true, false, new NullProgressMonitor());
				// getCommandStack().markSaveLocation();
				simulation.setDirty(false);
				viewer.refresh();
			} catch (CoreException ce) {
				ce.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} finally {
				try {
					out.close();
					if (writer != null)
						writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}


			// IWorkbench workbench = PlatformUI.getWorkbench();
			// IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
			// .getActivePage();
			//
			// IPerspectiveRegistry perspectiveRegistry = workbench
			// .getPerspectiveRegistry();
			//
			// Simulation simulation = (Simulation) firstElement;
			// String perspectiveId = PerspectiveUtil
			// .getPerspectiveIdFromFile(simulation.getProjectFile());
			// IPerspectiveDescriptor perspectiveDescriptor =
			// perspectiveRegistry.findPerspectiveWithId(perspectiveId);
			// if(perspectiveDescriptor != null) {
			// PerspectiveUtil.closePerspective(page, perspectiveDescriptor);
			// PerspectiveUtil.deletePerspective(page, perspectiveDescriptor);
			// }
			//
			// BMotionEditorPlugin.closeSimulation(simulation);

		}

	}

}
