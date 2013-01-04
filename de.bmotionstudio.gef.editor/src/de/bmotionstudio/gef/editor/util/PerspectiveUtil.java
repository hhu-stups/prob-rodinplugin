package de.bmotionstudio.gef.editor.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferenceFilter;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.runtime.preferences.PreferenceFilterEntry;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.bmotionstudio.gef.editor.VisualizationViewPart;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.prob.logging.Logger;

public class PerspectiveUtil {

	public static void deletePerspective(IWorkbenchPage page,
			IPerspectiveDescriptor perspectiveDescriptor) {
		if (perspectiveDescriptor == null || page == null)
			return;
		IPerspectiveRegistry perspectiveRegistry = page.getWorkbenchWindow()
				.getWorkbench().getPerspectiveRegistry();
		perspectiveRegistry.deletePerspective(perspectiveDescriptor);
	}

	public static void closePerspective(IWorkbenchPage page,
			IPerspectiveDescriptor perspectiveDescriptor) {
		if (perspectiveDescriptor == null || page == null)
			return;
		page.closePerspective(perspectiveDescriptor, false, true);
	}

	public static void switchPerspective(
			IPerspectiveDescriptor perspectiveDescriptor) {
		switchPerspective(perspectiveDescriptor.getId());
	}

	public static void switchPerspective(String perspectiveID) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		try {
			workbench.showPerspective(perspectiveID,
					workbench.getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			Logger.notifyUser(
					"An error occured while trying to switch the perspective.",
					e);
		}
	}

	public static void exportPerspective(final Simulation simulation,
			final IPerspectiveDescriptor perspectiveDescriptor) {

		Assert.isNotNull(perspectiveDescriptor);
		Assert.isNotNull(simulation);

		FileOutputStream fos = null;

		try {

			IPreferenceFilter[] transfers = null;

			transfers = new IPreferenceFilter[1];

			// For export all create a preference filter that can export
			// all nodes of the Instance and Configuration scopes
			transfers[0] = new IPreferenceFilter() {
				public String[] getScopes() {
					return new String[] { InstanceScope.SCOPE };
				}

				public Map<String, PreferenceFilterEntry[]> getMapping(
						String scope) {
					Map<String, PreferenceFilterEntry[]> map = new HashMap<String, PreferenceFilterEntry[]>();
					map.put("org.eclipse.ui.workbench",
							new PreferenceFilterEntry[] { new PreferenceFilterEntry(
									perspectiveDescriptor.getId() + "_persp") });
					return map;
				}
			};

			IFile pFile = simulation
					.getProjectFile()
					.getProject()
					.getFile(
							simulation.getProjectFile().getName()
									.replace(".bmso", ".bmsop"));

			String content = "";

			if (!pFile.exists())
				pFile.create(new ByteArrayInputStream(content.getBytes()),
						true, new NullProgressMonitor());

			File exportFile = new File(pFile.getLocationURI());
			fos = new FileOutputStream(exportFile);
			IPreferencesService service = Platform.getPreferencesService();
			service.exportPreferences(service.getRootNode(), transfers, fos);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static String getPerspectiveIdFromFile(IFile file) {
		return "BMS_" + file.getName().replace(".bmso", "");
	}

	public static VisualizationViewPart createVisualizationViewPart(
			String secId, VisualizationView visualizationView)
			throws PartInitException {
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage activePage = window.getActivePage();
		VisualizationViewPart visualizationViewPart = (VisualizationViewPart) activePage
				.showView(VisualizationViewPart.ID, secId,
						IWorkbenchPage.VIEW_VISIBLE);
		return visualizationViewPart;
	}

}
