/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.controlpanel;

import java.util.UUID;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.PartInitException;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.VisualizationViewPart;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.bmotionstudio.gef.editor.util.PerspectiveUtil;

public class AddVisualizationViewAction extends Action {

	private TreeViewer viewer;

	public AddVisualizationViewAction(TreeViewer viewer) {
		this.viewer = viewer;
		setText("Add View");
		setImageDescriptor(BMotionStudioImage.getImageDescriptor(
				"org.eclipse.ui", "$nl$/icons/full/etool16/new_wiz.gif"));
	}

	@Override
	public void run() {

		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		Object firstElement = sel.getFirstElement();
		if (firstElement instanceof Simulation) {

			Simulation simulation = (Simulation) firstElement;

			PerspectiveUtil.openPerspective(simulation);

			try {

				String secId = UUID.randomUUID().toString();
				// Create a new visualization
				String version = Platform
						.getBundle(BMotionEditorPlugin.PLUGIN_ID).getHeaders()
						.get("Bundle-Version");
				Visualization visualization = new Visualization(simulation
						.getProjectFile().getName(), "EventB", version);

				VisualizationView visualizationView = new VisualizationView(
						"New Visualization View", visualization);
				simulation.getVisualizationViews()
						.put(secId, visualizationView);

				VisualizationViewPart visualizationViewPart = PerspectiveUtil
						.createVisualizationViewPart(secId,
						visualizationView);
				visualizationViewPart.init(simulation, visualizationView);
				
				simulation.setDirty(true);
				viewer.refresh();

			} catch (PartInitException e1) {
				e1.printStackTrace();
			}

		}

	}

}
