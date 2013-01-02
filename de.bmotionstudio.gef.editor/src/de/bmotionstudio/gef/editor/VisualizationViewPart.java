package de.bmotionstudio.gef.editor;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import de.bmotionstudio.gef.editor.internal.BControlTransferDropTargetListener;
import de.bmotionstudio.gef.editor.model.BMotionRuler;
import de.bmotionstudio.gef.editor.model.BMotionRulerProvider;
import de.bmotionstudio.gef.editor.model.Simulation;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.model.VisualizationView;
import de.bmotionstudio.gef.editor.part.BMSEditPartFactory;

public class VisualizationViewPart extends PageBookView {

	public static String ID = "de.bmotionstudio.gef.editor.VisualizationView";
	
	private EditDomain editDomain;

	private VisualizationView visualizationView;

	private VisualizationViewPage page;

	private ActionRegistry actionRegistry;

	private Composite container;

	private BMotionSelectionSynchronizer selectionSynchronizer;

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {

		// // Adapter for zoom manager
		// if (type == ZoomManager.class)
		// return ((ScalableRootEditPart) getGraphicalViewer()
		// .getRootEditPart()).getZoomManager();

		// Adapter for content outline page
		if (type == IContentOutlinePage.class) {
			return new BMotionOutlinePage(this);
		}

		// Adapter for property page
		if (type == IPropertySheetPage.class) {
			BMotionPropertyPage page = new BMotionPropertyPage(
					getCommandStack(), getActionRegistry().getAction(
							ActionFactory.UNDO.getId()), getActionRegistry()
							.getAction(ActionFactory.REDO.getId()));
			page.setRootEntry(new CustomSortPropertySheetEntry(
					getCommandStack()));
			return page;
		}

		return super.getAdapter(type);

	}

	public SelectionSynchronizer getSelectionSynchronizer() {
		if (selectionSynchronizer == null)
			selectionSynchronizer = new BMotionSelectionSynchronizer();
		return selectionSynchronizer;
	}

	// Workaround for prevent recursive activiation of part
	@Override
	public void setFocus() {
		container.setFocus();
		super.setFocus();
	}

	protected ActionRegistry getActionRegistry() {
		if (actionRegistry == null)
			actionRegistry = new ActionRegistry();
		return actionRegistry;
	}

	public Visualization getVisualization() {
		return this.visualizationView.getVisualization();
	}

	public GraphicalViewer getGraphicalViewer() {
		if (page != null)
			return page.getGraphicalViewer();
		return null;
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
	public void createPartControl(Composite parent) {
		this.container = parent;
		super.createPartControl(parent);
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		if (part instanceof BMotionStudioEditor) {
			BMotionStudioEditor editor = (BMotionStudioEditor) part;
			Simulation simulation = editor.getSimulation();
			this.editDomain = editor.getEditDomain();
			this.visualizationView = simulation.getVisualizationViews().get(
					getViewSite().getSecondaryId());
			if (this.editDomain == null || this.visualizationView == null)
				return null;
			page = new VisualizationViewPage();
			initPage(page);
			page.createControl(getPageBook());
			return new PageRec(part, page);
		}
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		VisualizationViewPage page = (VisualizationViewPage) rec.page;
		page.dispose();
		rec.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			return page.getActiveEditor();
		}
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof BMotionStudioEditor;
	}

	public EditDomain getEditDomain() {
		return editDomain;
	}

	protected CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}

	public void setEditDomain(EditDomain editDomain) {
		this.editDomain = editDomain;
	}

	public VisualizationView getVisualizationView() {
		return visualizationView;
	}

	public void setVisualizationView(VisualizationView visualizationView) {
		this.visualizationView = visualizationView;
	}

	private class VisualizationViewPage extends Page {

		private RulerComposite container;

		private GraphicalViewer graphicalViewer;

		public GraphicalViewer getGraphicalViewer() {
			return graphicalViewer;
		}

		@Override
		public void createControl(Composite parent) {
			container = new RulerComposite(parent, SWT.NONE);
			graphicalViewer = new ScrollingGraphicalViewer();
			graphicalViewer.createControl(container);
			configureGraphicalViewer();
			initGraphicalViewer();
			hookGraphicalViewer();
			setPartName(getVisualizationView().getName());
		}

		protected void hookGraphicalViewer() {
			getSelectionSynchronizer().addViewer(getGraphicalViewer());
			getSite().setSelectionProvider(getGraphicalViewer());
		}

		public void configureGraphicalViewer() {
			ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
			rootEditPart.setViewer(graphicalViewer);
			graphicalViewer.setRootEditPart(rootEditPart);
			graphicalViewer.setEditPartFactory(new BMSEditPartFactory());
			graphicalViewer.getControl().setBackground(ColorConstants.red);
			container
					.setGraphicalViewer((ScrollingGraphicalViewer) graphicalViewer);
			graphicalViewer.setEditDomain(getEditDomain());
			graphicalViewer
					.addDropTargetListener(new BControlTransferDropTargetListener(
							graphicalViewer, getVisualization()));
			graphicalViewer.getControl().setBackground(ColorConstants.white);
			loadProperties();
		}

		public void initGraphicalViewer() {
			graphicalViewer.setContents(getVisualization());
			loadProperties();
		}

		@Override
		public Control getControl() {
			return container;
		}

		@Override
		public void setFocus() {
		}

		protected void loadProperties() {

			// Ruler properties
			BMotionRuler ruler = getVisualization().getRuler(
					PositionConstants.WEST);
			RulerProvider provider = null;
			if (ruler != null) {
				provider = new BMotionRulerProvider(ruler);
			}
			getGraphicalViewer().setProperty(
					RulerProvider.PROPERTY_VERTICAL_RULER, provider);
			ruler = getVisualization().getRuler(PositionConstants.NORTH);
			provider = null;
			if (ruler != null) {
				provider = new BMotionRulerProvider(ruler);
			}
			getGraphicalViewer().setProperty(
					RulerProvider.PROPERTY_HORIZONTAL_RULER, provider);
			getGraphicalViewer().setProperty(
					RulerProvider.PROPERTY_RULER_VISIBILITY,
					getVisualization().getRulerVisibility());
			getGraphicalViewer().setProperty(
					SnapToGeometry.PROPERTY_SNAP_ENABLED,
					getVisualization().isSnapToGeometryEnabled());
			getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
					getVisualization().isGridEnabled());
			getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
					getVisualization().isGridEnabled());

		}

	}

}
