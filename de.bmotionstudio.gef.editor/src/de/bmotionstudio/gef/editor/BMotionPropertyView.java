package de.bmotionstudio.gef.editor;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.PropertySheet;

public class BMotionPropertyView extends PropertySheet {

	public static final String ID = "de.bmotionstudio.gef.editor.PropertyView";

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		IViewPart view = page.findView(VisualizationViewPart.ID);
		if (view != null)
			return view;
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof VisualizationViewPart;
	}

}
