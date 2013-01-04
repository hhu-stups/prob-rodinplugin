package de.bmotionstudio.gef.editor;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.util.PerspectiveUtil;

public class BMotionStartup implements IStartup {

	@Override
	public void earlyStartup() {

		IPerspectiveRegistry perspectiveRegistry = PlatformUI.getWorkbench()
				.getPerspectiveRegistry();
		IPerspectiveDescriptor[] perspectives = perspectiveRegistry
				.getPerspectives();
		for (IPerspectiveDescriptor p : perspectives) {
			if (p.getId().startsWith("BMS_"))
				PerspectiveUtil.closePerspective(p);
				PerspectiveUtil.deletePerspective(p);
		}

	}

}
