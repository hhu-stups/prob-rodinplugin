package de.bmotionstudio.gef.editor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

public class BMSPaletteView extends PageBookView {

	// private PaletteViewer paletteViewer;

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		IViewPart view = page.findView(VisualizationViewPart.ID);
		if (view != null)
			return view;
		return null;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (part instanceof VisualizationViewPart) {
			BMSPaletteViewPage page = new BMSPaletteViewPage();
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	@Override
	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof VisualizationViewPart;
	}

	private class BMSPaletteViewPage extends Page {

		private Composite container;

		protected void createPaletteViewer(Composite parent) {
			PaletteViewer viewer = new PaletteViewer();
			viewer.createControl(parent);
			viewer.getControl().setBackground(ColorConstants.green);
			EditDomain domain = new EditDomain();
			domain.setPaletteViewer(viewer);
		}

		@Override
		public void createControl(Composite parent) {
			container = new Composite(parent, SWT.NONE);
			container.setBackground(ColorConstants.red);
			container.setLayout(new FillLayout());
			createPaletteViewer(container);
		}

		@Override
		public Control getControl() {
			return container;
		}

		@Override
		public void setFocus() {
		}

	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage page = new MessagePage();
		initPage(page);
		page.createControl(book);
		page.setMessage("NA");
		return page;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// TODO Auto-generated method stub

	}

}
