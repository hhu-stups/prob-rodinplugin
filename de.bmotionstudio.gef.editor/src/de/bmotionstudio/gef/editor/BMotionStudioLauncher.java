package de.bmotionstudio.gef.editor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.bmotionstudio.gef.editor.util.PerspectiveUtil;


public class BMotionStudioLauncher implements IEditorLauncher {

	private IFile file;

	@Override
	public void open(IPath path) {

		file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(path);

		Simulation simulation = BMotionEditorPlugin.getOpenSimulations().get(
				file.getName());

		// The simulation is already open
		if (simulation != null) {
			PerspectiveUtil.openPerspective(simulation);
			initViews(simulation);
			return;
		}
		
		InputStream inputStream = null;

		try {

			inputStream = file.getContents();

			XStream xstream = new XStream() {
				@Override
				protected MapperWrapper wrapMapper(final MapperWrapper next) {
					return new MapperWrapper(next) {
						@Override
						public boolean shouldSerializeMember(
								@SuppressWarnings("rawtypes") final Class definedIn,
								final String fieldName) {
							if (definedIn == Object.class)
								return false;
							return super.shouldSerializeMember(definedIn,
									fieldName);
						}
					};
				}
			};

			BMotionEditorPlugin.setAliases(xstream);
			Object obj = xstream.fromXML(inputStream);

			if (obj instanceof Visualization) {

				Visualization visualization = (Visualization) obj;
				visualization.setProjectFile(file);

				simulation = new Simulation(visualization.getMachineName());

				String secId = UUID.randomUUID().toString();

				VisualizationView visualizationView = new VisualizationView(
						"New Visualization View", visualization, secId);

				simulation.getVisualizationViews()
						.put(secId, visualizationView);

			} else if (obj instanceof Simulation) {
				simulation = (Simulation) obj;
			}

			if (simulation != null) {

				simulation.setProjectFile(file);

				PerspectiveUtil.openPerspective(simulation);
				initViews(simulation);
				BMotionEditorPlugin.openSimulation(simulation);

			}

		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private void initViews(Simulation simulation) {

		IWorkbenchPage activePage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPartSite site = activePage.getActivePart().getSite();

		for (Map.Entry<String, VisualizationView> entry : simulation
				.getVisualizationViews().entrySet()) {

			String secId = entry.getKey();
			VisualizationView visView = entry.getValue();
			Visualization vis = visView.getVisualization();
			vis.setProjectFile(file);
			// String partName = visView.getPartName();
			IViewReference viewReference = site.getPage().findViewReference(
					VisualizationViewPart.ID, secId);
			VisualizationViewPart visualizationViewPart = null;
			// Check if view already exists
			if (viewReference != null) {
				visualizationViewPart = (VisualizationViewPart) viewReference
						.getPart(true);
			} else {
				// If not, create a new one
				try {
					visualizationViewPart = PerspectiveUtil
							.createVisualizationViewPart(secId, visView);
				} catch (PartInitException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (visualizationViewPart != null
					&& !visualizationViewPart.isInitialized()) {
				visualizationViewPart.init(simulation, visView);
			}

		}

		// Close all unused visualization views
		for (IViewReference viewReference : site.getPage().getViewReferences()) {
			if (viewReference.getId().equals(VisualizationViewPart.ID)) {
				if (!simulation.getVisualizationViews().containsKey(
						viewReference.getSecondaryId()))
					site.getPage().hideView(viewReference);
			}
		}

	}

}
