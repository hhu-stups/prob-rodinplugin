package de.bmotionstudio.gef.editor;

import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSEditPartFactory;

public class VisualizationView extends ViewPart {

	public static String ID = "de.bmotionstudio.gef.editor.VisualizationView";
	
	private EditDomain editDomain;

	private GraphicalViewer graphicalViewer;

	private Visualization visualization;

	public VisualizationView() {
	}

	// public VisualizationView(EditDomain editDomain, Visualization
	// visualization) {
	// this.editDomain = editDomain;
	// this.visualization = visualization;
	// }

	public void initGraphicalViewer(EditDomain editDomain,
			Visualization visualization) {
		this.editDomain = editDomain;
		this.visualization = visualization;
		graphicalViewer.setEditDomain(editDomain);
		graphicalViewer.setContents(visualization);
	}

	@Override
	public void createPartControl(Composite parent) {
		graphicalViewer = new ScrollingGraphicalViewer();
		graphicalViewer.createControl(parent);
		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		rootEditPart.setViewer(graphicalViewer);
		graphicalViewer.setRootEditPart(rootEditPart);
		graphicalViewer.setEditPartFactory(new BMSEditPartFactory());
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {

		// Adapter for zoom manager
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
//		if (type == IContentOutlinePage.class)
//			return new BMotionOutlinePage();

		// Adapter for property page
		if (type == IPropertySheetPage.class) {
			BMotionStudioPropertySheet page = new BMotionStudioPropertySheet();
			page.setRootEntry(new CustomSortPropertySheetEntry(
					getCommandStack()));
			return page;
		}

		return super.getAdapter(type);

	}
	
	/**
	 * Returns the <code>CommandStack</code> of this editor's
	 * <code>EditDomain</code>.
	 * 
	 * @return the <code>CommandStack</code>
	 */
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}
	
	/**
	 * Returns the edit domain.
	 * 
	 * @return the edit domain
	 */
	protected EditDomain getEditDomain() {
		return editDomain;
	}

	/**
	 * Returns the graphical viewer.
	 * 
	 * @return the graphical viewer
	 */
	protected GraphicalViewer getGraphicalViewer() {
		return graphicalViewer;
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
