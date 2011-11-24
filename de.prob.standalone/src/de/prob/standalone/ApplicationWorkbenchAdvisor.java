package de.prob.standalone;

import java.net.URL;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;

public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {

	private static final String PERSPECTIVE_ID = "de.prob.ui.perspective";

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId() {
		return PERSPECTIVE_ID;
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer) {
		super.initialize(configurer);
		// configurer.setSaveAndRestore(true);
		declareWorkbenchImages();
	}

	/*
	 * Comment from Lukas: Copied from IDEWorkbenchAdvisor.class in order to
	 * show folder images.
	 */
	private void declareWorkbenchImages() {
		final String ICONS_PATH = "$nl$/icons/full/";//$NON-NLS-1$
		final String PATH_ELOCALTOOL = ICONS_PATH + "elcl16/"; // Enabled //$NON-NLS-1$
		final String PATH_OBJECT = ICONS_PATH + "obj16/"; // Model object //$NON-NLS-1$
		Bundle ideBundle = Platform.getBundle("org.eclipse.ui.ide");
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OBJ_PROJECT,
				PATH_OBJECT + "prj_obj.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle,
				IDE.SharedImages.IMG_OBJ_PROJECT_CLOSED, PATH_OBJECT
						+ "cprj_obj.gif", true); //$NON-NLS-1$
		declareWorkbenchImage(ideBundle, IDE.SharedImages.IMG_OPEN_MARKER,
				PATH_ELOCALTOOL + "gotoobj_tsk.gif", true); //$NON-NLS-1$
	}

	private void declareWorkbenchImage(Bundle ideBundle, String symbolicName,
			String path, boolean shared) {
		URL url = FileLocator.find(ideBundle, new Path(path), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		getWorkbenchConfigurer().declareImage(symbolicName, desc, shared);
	}

	@Override
	public void preStartup() {
		IDE.registerAdapters();
	}

	@Override
	public IAdaptable getDefaultPageInput() {
		IAdaptable root = ResourcesPlugin.getWorkspace().getRoot();
		return root;
	}

}
