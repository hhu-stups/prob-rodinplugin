package de.bmotionstudio.gef.editor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IExportedPreferences;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.bmotionstudio.gef.editor.util.PerspectiveUtil;


public class BMotionStudioLauncher implements IEditorLauncher {

	private Simulation simulation;

	private IFile file;

	@Override
	public void open(IPath path) {

		file = ResourcesPlugin.getWorkspace().getRoot()
				.getFileForLocation(path);

		if (BMotionEditorPlugin.getOpenSimulations()
				.containsKey(file.getName()))
			return;

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

			IWorkbenchPage activePage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			IWorkbenchPartSite site = activePage.getActivePart().getSite();

			importPerspective(file.getProject().getFile(
					getPerspectiveFileName()));
			openPerspective(site.getPage());

			if (obj instanceof Visualization) {

				simulation = new Simulation();

				Visualization visualization = (Visualization) obj;
				visualization.setProjectFile(file);

				VisualizationView visualizationView = new VisualizationView(
						"New Visualization View", visualization);

				String secId = UUID.randomUUID().toString();

				simulation.getVisualizationViews()
						.put(secId, visualizationView);

			} else if (obj instanceof Simulation) {
				simulation = (Simulation) obj;
			}

			if (simulation != null) {

				simulation.setProjectFile(file);

				for (Map.Entry<String, VisualizationView> entry : simulation
						.getVisualizationViews().entrySet()) {

					String secId = entry.getKey();
					VisualizationView visView = entry.getValue();
					Visualization vis = visView.getVisualization();
					vis.setProjectFile(file);
					// String partName = visView.getPartName();
					// IViewReference viewReference = site.getPage()
					// .findViewReference(VisualizationViewPart.ID, secId);
					// Check if view already exists
					// if (viewReference != null) {
					// } else {
					// If not, create a new one
					VisualizationViewPart visualizationViewPart = PerspectiveUtil
							.createVisualizationViewPart(
							secId, visView);
					if (!visualizationViewPart.isInitialized())
						visualizationViewPart.init(simulation, visView);
					// }

				}

				// Close all unused visualization views
				for (IViewReference viewReference : site.getPage()
						.getViewReferences()) {
					if (viewReference.getId().equals(VisualizationViewPart.ID)) {
						if (!simulation.getVisualizationViews().containsKey(
								viewReference.getSecondaryId()))
							site.getPage().hideView(viewReference);
					}
				}

				BMotionEditorPlugin.openSimulation(simulation);

			}

		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private IPerspectiveDescriptor openPerspective(IWorkbenchPage page) {

		if (page == null)
			return null;

		// Try to get the corresponding perspective
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		String perspectiveId = PerspectiveUtil.getPerspectiveIdFromFile(file);
		IPerspectiveDescriptor perspective = perspectiveRegistry
				.findPerspectiveWithId(perspectiveId);

		// Yes --> just switch to this perspective
		if (perspective != null) {
			PerspectiveUtil.switchPerspective(perspective);
		} else {
			// No --> create a new one
			IPerspectiveDescriptor originalPerspectiveDescriptor = perspectiveRegistry
					.findPerspectiveWithId(BMSPerspectiveFactory.ID);
			PerspectiveUtil.switchPerspective(originalPerspectiveDescriptor);
			perspective = perspectiveRegistry.clonePerspective(perspectiveId,
					perspectiveId, originalPerspectiveDescriptor);
			// save the perspective
			page.savePerspectiveAs(perspective);
		}

		return perspective;

	}

	private void importPerspective(IFile perspectiveFile) {

		FileInputStream fis = null;

		try {

			IPreferenceFilter[] transfers = null;
			transfers = new IPreferenceFilter[1];

			// Only import if a perspective file exists
			if (perspectiveFile.exists()) {

				File exportFile = new File(perspectiveFile.getLocationURI());
				fis = new FileInputStream(exportFile);
				IPreferencesService service = Platform.getPreferencesService();
				// service.importPreferences(fis);
				IExportedPreferences prefs = service.readPreferences(fis);
				transfers[0] = new IPreferenceFilter() {
					public String[] getScopes() {
						return new String[] { InstanceScope.SCOPE };
					}

					public Map<String, PreferenceFilterEntry[]> getMapping(
							String scope) {
						Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
						map.put("org.eclipse.ui.workbench",
								new PreferenceFilterEntry[] { new PreferenceFilterEntry(
										PerspectiveUtil
												.getPerspectiveIdFromFile(file)
												+ "_persp") });
						return map;
					}
				};
				service.applyPreferences(prefs, transfers);
			}

		} catch (FileNotFoundException e) {
		} catch (CoreException e) {
		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private String getPerspectiveFileName() {
		return file.getName().replace(".bmso", ".bmsop");
	}

}
