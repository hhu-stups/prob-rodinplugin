package de.bmotionstudio.gef.editor;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.IPageSite;

import de.bmotionstudio.gef.editor.part.BMSTreeEditPartFactory;

public class BMotionOutlinePage extends ContentOutlinePage {

	private SashForm sash;

	private ScrollableThumbnail thumbnail;

	private DisposeListener disposeListener;

	private VisualizationViewPart viewPart;

	public BMotionOutlinePage(VisualizationViewPart viewPart) {
		super(new TreeViewer());
		this.viewPart = viewPart;
	}

	@Override
	public void init(IPageSite pageSite) {
		super.init(pageSite);
		// IActionBars bars = pageSite.getActionBars();
		// ActionRegistry ar = getActionRegistry();
		// bars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
		// ar.getAction(ActionFactory.UNDO.getId()));
		// bars.setGlobalActionHandler(ActionFactory.REDO.getId(),
		// ar.getAction(ActionFactory.REDO.getId()));
		// bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
		// ar.getAction(ActionFactory.DELETE.getId()));
		// bars.setGlobalActionHandler(ActionFactory.COPY.getId(),
		// ar.getAction(ActionFactory.COPY.getId()));
		// bars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
		// ar.getAction(ActionFactory.PASTE.getId()));
		// buildCustomActions(bars, ar);
		// bars.updateActionBars();
	}

	protected void configureOutlineViewer() {
		getViewer().setEditDomain(this.viewPart.getEditDomain());
		getViewer().setEditPartFactory(new BMSTreeEditPartFactory());
		// ContextMenuProvider provider = new
		// BMSContextMenuProvider(getViewer(),
		// getActionRegistry());
		// getViewer().setContextMenu(provider);
		// getViewer().setKeyHandler(getCommonKeyHandler());
	}

	protected void hookOutlineViewer() {
		this.viewPart.getSelectionSynchronizer().addViewer(getViewer());
	}

	protected void unhookOutlineViewer() {
		GraphicalViewer graphicalViewer = viewPart.getGraphicalViewer();
		this.viewPart.getSelectionSynchronizer().removeViewer(getViewer());
		if (graphicalViewer != null) {
			if (graphicalViewer.getControl() != null
					&& !graphicalViewer.getControl().isDisposed())
				graphicalViewer.getControl().removeDisposeListener(
						disposeListener);
		}
	}

	@Override
	public void createControl(Composite parent) {
		GraphicalViewer graphicalViewer = viewPart.getGraphicalViewer();
		if (graphicalViewer != null) {
			initializeOverview(parent);
			graphicalViewer.getControl().addDisposeListener(disposeListener);
			configureOutlineViewer();
			hookOutlineViewer();
			initializeOutlineViewer();
			createMenu();
		}
	}

	private void initializeOutlineViewer() {
		getViewer().setContents(viewPart.getVisualization());
	}

	/**
	 * 
	 */
	private void createMenu() {

		Action expandAllAction = new Action("Expand All") {

			@Override
			public void run() {
				for (TreeItem item : ((Tree) getViewer().getControl())
						.getItems()) {
					item.setExpanded(true);
				}
			}

		};

		Action collapseAllAction = new Action("Collapse All") {

			@Override
			public void run() {
				for (TreeItem item : ((Tree) getViewer().getControl())
						.getItems()) {
					item.setExpanded(false);
				}
			}

		};

		getSite().getActionBars().getMenuManager().add(expandAllAction);
		getSite().getActionBars().getMenuManager().add(collapseAllAction);

	}

	private void initializeOverview(Composite parent) {

		GraphicalViewer graphicalViewer = this.viewPart.getGraphicalViewer();

		sash = new SashForm(parent, SWT.VERTICAL);

		getViewer().createControl(sash);

		Canvas canvas = new Canvas(sash, SWT.BORDER);
		canvas.setBackground(Display.getDefault().getSystemColor(
				SWT.COLOR_WHITE));
		LightweightSystem lws = new LightweightSystem(canvas);

		thumbnail = new ScrollableThumbnail(
				(Viewport) ((ScalableRootEditPart) graphicalViewer
						.getRootEditPart()).getFigure());
		thumbnail.setSource(((ScalableRootEditPart) graphicalViewer
				.getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));

		lws.setContents(thumbnail);

		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (thumbnail != null) {
					thumbnail.deactivate();
					thumbnail = null;
				}
			}
		};

	}

	public Control getControl() {
		return sash;
	}

	@Override
	public void dispose() {
		unhookOutlineViewer();
		super.dispose();
	}

}
