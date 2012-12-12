package de.bmotionstudio.gef.editor;

import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;

public class BMSPaletteView extends PaletteView {

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null)
			return page.getActiveEditor();
		return null;
	}

	// public BMSPaletteView() {
	// // TODO Auto-generated constructor stub
	// }
	//
	// @Override
	// public void createPartControl(Composite parent) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void setFocus() {
	// // TODO Auto-generated method stub
	//
	// }

}
