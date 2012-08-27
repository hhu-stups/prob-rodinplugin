/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.commands.CommandStackListener;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.GridLayer;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.gef.ui.actions.ToggleGridAction;
import org.eclipse.gef.ui.actions.ToggleRulerVisibilityAction;
import org.eclipse.gef.ui.actions.ToggleSnapToGeometryAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.FlyoutPaletteComposite;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerProvider;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.rulers.RulerComposite;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;

import com.thoughtworks.xstream.XStream;

import de.bmotionstudio.gef.editor.action.CopyAction;
import de.bmotionstudio.gef.editor.action.OpenObserverAction;
import de.bmotionstudio.gef.editor.action.OpenSchedulerEventAction;
import de.bmotionstudio.gef.editor.action.PasteAction;
import de.bmotionstudio.gef.editor.internal.BControlTransferDropTargetListener;
import de.bmotionstudio.gef.editor.library.AttributeTransferDropTargetListener;
import de.bmotionstudio.gef.editor.model.BMotionRuler;
import de.bmotionstudio.gef.editor.model.BMotionRulerProvider;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.BMSAbstractEditPart;
import de.bmotionstudio.gef.editor.part.BMSAbstractTreeEditPart;
import de.bmotionstudio.gef.editor.part.BMSEditPartFactory;
import de.bmotionstudio.gef.editor.part.BMSTreeEditPartFactory;

public class BMotionStudioEditorPage extends GraphicalEditorWithFlyoutPalette {

	public final static String ID = "de.bmotionstudio.gef.editor";

	private boolean isDirty;

	private KeyHandler sharedKeyHandler;

	private Visualization visualization;

	private RulerComposite rulerComp;

	// private BControl editArea;

	private BMotionStudioEditor bmotionStudioEditor;

	private BMotionSelectionSynchronizer bmotionSelectionSynchronizer;

	private Color gridColor = new Color(null, 240, 240, 240);

	/** Palette component, holding the tools and b-controls. */
	private PaletteRoot palette;

	private PropertyChangeListener viewerListener = new PropertyChangeListener() {

		@Override
		public void propertyChange(PropertyChangeEvent event) {
			String propertyName = event.getPropertyName();
			if (propertyName.equals(SnapToGrid.PROPERTY_GRID_VISIBLE)
					|| propertyName.equals(SnapToGrid.PROPERTY_GRID_ENABLED)) {
				setDirty(true);
			}
		}

	};

	public BMotionStudioEditorPage(Visualization visualization,
			BMotionStudioEditor bmotionStudioEditor) {
		this.visualization = visualization;
		this.bmotionStudioEditor = bmotionStudioEditor;
		setEditDomain(new DefaultEditDomain(this));
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		// If not the active editor, ignore selection changed.
		if (!getBMotionStudioEditor().equals(
				getSite().getPage().getActiveEditor()))
			return;

		Object selectedElement = ((IStructuredSelection) selection)
				.getFirstElement();
		if (selectedElement instanceof BMSAbstractEditPart
				|| selectedElement instanceof BMSAbstractTreeEditPart)
			updateActions(getSelectionActions());

	}

	/**
	 * @see org.eclipse.ui.IEditorPart#init(IEditorSite, IEditorInput)
	 **/
	public void init(IEditorSite iSite, IEditorInput iInput)
			throws PartInitException {
		super.init(iSite, iInput);
		setSite(iSite);
		// add CommandStackListener
		getCommandStack().addCommandStackListener(getCommandStackListener());
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#initializeGraphicalViewer()
	 **/
	protected void initializeGraphicalViewer() {

		super.initializeGraphicalViewer();

		getGraphicalViewer().setContents(getVisualization());

		getGraphicalViewer().addDropTargetListener(
				new BControlTransferDropTargetListener(getGraphicalViewer(),
						visualization));
		getGraphicalViewer().addDropTargetListener(
				new AttributeTransferDropTargetListener(getGraphicalViewer(),
						getSite().getPart()));

		getPalettePreferences().setPaletteState(
				FlyoutPaletteComposite.STATE_PINNED_OPEN);

	}

	@Override
	protected PaletteViewerProvider createPaletteViewerProvider() {
		return new PaletteViewerProvider(getEditDomain()) {
			protected void configurePaletteViewer(PaletteViewer viewer) {
				super.configurePaletteViewer(viewer);
				viewer.addDragSourceListener(new TemplateTransferDragSourceListener(
						viewer));
			}

			protected void hookPaletteViewer(PaletteViewer viewer) {
				super.hookPaletteViewer(viewer);
			}
		};
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#isSaveAsAllowed()
	 **/
	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Creates an appropriate output stream and writes the activity diagram out
	 * to this stream.
	 * 
	 * @param os
	 *            the base output stream
	 * @throws IOException
	 */
	protected void createOutputStream(OutputStream os) throws IOException {
		OutputStreamWriter writer = new OutputStreamWriter(os, "UTF8");
		XStream xstream = new XStream();
		BMotionEditorPlugin.setAliases(xstream);
		xstream.toXML(visualization, writer);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSave(IProgressMonitor)
	 **/
	public void doSave(IProgressMonitor iMonitor) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			saveProperties();
			createOutputStream(out);
			IFile file = ((IFileEditorInput) getEditorInput()).getFile();
			file.setContents(new ByteArrayInputStream(out.toByteArray()), true,
					false, iMonitor);
			getCommandStack().markSaveLocation();
		} catch (CoreException ce) {
			ce.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPart#dispose()
	 */
	public void dispose() {
		// remove CommandStackListener
		getCommandStack().removeCommandStackListener(getCommandStackListener());

		// remove PropertyChangeListener from graphical viewer
		getGraphicalViewer().removePropertyChangeListener(viewerListener);

		// important: always call super implementation of dispose
		super.dispose();
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#doSaveAs()
	 **/
	public void doSaveAs() {
		// Nothing to do here, this is never allowed
		throw new IllegalAccessError("No way to enter this method.");
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
		if (type == IContentOutlinePage.class)
			return new BMotionOutlinePage();
		if (type == IPropertySheetPage.class) {
			BMotionStudioPropertySheet page = new BMotionStudioPropertySheet();
			page.setRootEntry(new CustomSortPropertySheetEntry(
					getCommandStack()));
			return page;
		}
		return super.getAdapter(type);
	}

	/**
	 * @see org.eclipse.ui.IEditorPart#gotoMarker(IMarker)
	 **/
	public void gotoMarker(IMarker iMarker) {
	}

	/**
	 * Returns the KeyHandler with common bindings for both the Outline and
	 * Graphical Views. For example, delete is a common action.
	 */
	protected KeyHandler getCommonKeyHandler() {
		if (sharedKeyHandler == null) {
			sharedKeyHandler = new KeyHandler();
			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));
			sharedKeyHandler
					.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
							getActionRegistry().getAction(
									ActionFactory.DELETE.getId()));

			sharedKeyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
					getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

			sharedKeyHandler.put(
					KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
					getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

			sharedKeyHandler.put(
					KeyStroke.getPressed(SWT.F2, 0),
					getActionRegistry().getAction(
							GEFActionConstants.DIRECT_EDIT));
		}
		return sharedKeyHandler;
	}

	public void setDirty(boolean dirty) {
		if (isDirty() != dirty) {
			isDirty = dirty;
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	/*
	 * @see EditorPart#isDirty
	 */
	@Override
	public boolean isDirty() {
		return isDirty;
	}

	@SuppressWarnings("unchecked")
	public void createActions() {

		super.createActions();

		ActionRegistry registry = getActionRegistry();

		installCustomActions();

		IAction action = new CopyAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		action = new PasteAction(this);
		registry.registerAction(action);
		getSelectionActions().add(action.getId());

		installObserverActions();
		installSchedulerActions();

	}

	@SuppressWarnings("unchecked")
	private void installObserverActions() {

		IAction action;
		ActionRegistry registry = getActionRegistry();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.gef.editor.observer");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("observer".equals(configurationElement.getName())) {

					String observerClassName = configurationElement
							.getAttribute("class");

					action = new OpenObserverAction(this);
					action.setId("de.bmotionstudio.gef.editor.observerAction."
							+ observerClassName);
					((OpenObserverAction) action).setClassName(observerClassName);
					registry.registerAction(action);
					getSelectionActions().add(
							"de.bmotionstudio.gef.editor.observerAction."
									+ observerClassName);

				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private void installSchedulerActions() {

		IAction action;
		ActionRegistry registry = getActionRegistry();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.gef.editor.schedulerEvent");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("schedulerEvent".equals(configurationElement.getName())) {

					String sClassName = configurationElement
							.getAttribute("class");

					action = new OpenSchedulerEventAction(this);
					action.setId("de.bmotionstudio.gef.editor.SchedulerEventAction."
							+ sClassName);
					((OpenSchedulerEventAction) action).setClassName(sClassName);
					registry.registerAction(action);
					getSelectionActions().add(
							"de.bmotionstudio.gef.editor.SchedulerEventAction."
									+ sClassName);

				}

			}

		}

	}

	@SuppressWarnings("unchecked")
	private void installCustomActions() {

		ActionRegistry registry = getActionRegistry();

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.gef.editor.installActions");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("action".equals(configurationElement.getName())) {

					try {

						IInstallActions installActionsClass = (IInstallActions) configurationElement
								.createExecutableExtension("class");

						installActionsClass.installActions(this);
						HashMap<String, Action> actionMap = installActionsClass
								.getActionMap();

						for (Map.Entry<String, Action> entry : actionMap
								.entrySet()) {

							registry.registerAction(entry.getValue());
							if (entry.getValue() instanceof SelectionAction)
								getSelectionActions().add(entry.getKey());

						}

					} catch (final CoreException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}

	protected Control getGraphicalControl() {
		return rulerComp;
	}

	protected void createGraphicalViewer(Composite parent) {
		rulerComp = new RulerComposite(parent, SWT.NONE);
		super.createGraphicalViewer(rulerComp);
		rulerComp
				.setGraphicalViewer((ScrollingGraphicalViewer) getGraphicalViewer());
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 **/
	protected void configureGraphicalViewer() {

		double[] zoomLevels;

		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();

		viewer.setEditPartFactory(new BMSEditPartFactory());

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		GridLayer gridLayer = (GridLayer) rootEditPart
				.getLayer(ScalableRootEditPart.GRID_LAYER);
		gridLayer.setForegroundColor(gridColor);
		viewer.setRootEditPart(rootEditPart);

		ZoomManager manager = rootEditPart.getZoomManager();
		getActionRegistry().registerAction(new ZoomInAction(manager));
		getActionRegistry().registerAction(new ZoomOutAction(manager));

		zoomLevels = new double[] { 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 2.5, 3.0,
				4.0, 5.0, 10.0, 20.0 };
		manager.setZoomLevels(zoomLevels);
		ArrayList<String> zoomContributions = new ArrayList<String>();
		zoomContributions.add(ZoomManager.FIT_ALL);
		zoomContributions.add(ZoomManager.FIT_HEIGHT);
		zoomContributions.add(ZoomManager.FIT_WIDTH);
		manager.setZoomLevelContributions(zoomContributions);

		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);

		// viewer.setKeyHandler(keyHandler);

		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer)
				.setParent(getCommonKeyHandler()));

		loadProperties();

		getActionRegistry().registerAction(
				new ToggleRulerVisibilityAction(getGraphicalViewer()));
		getActionRegistry().registerAction(
				new ToggleSnapToGeometryAction(getGraphicalViewer()));
		getActionRegistry().registerAction(
				new ToggleGridAction(getGraphicalViewer()));

		ContextMenuProvider provider = new BMSContextMenuProvider(viewer,
				getActionRegistry());
		viewer.setContextMenu(provider);
		
		viewer.addPropertyChangeListener(viewerListener);

	}

	protected void loadProperties() {

		// Ruler properties
		BMotionRuler ruler = getVisualization()
				.getRuler(PositionConstants.WEST);
		RulerProvider provider = null;
		if (ruler != null) {
			provider = new BMotionRulerProvider(ruler);
		}
		getGraphicalViewer().setProperty(RulerProvider.PROPERTY_VERTICAL_RULER,
				provider);
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
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
				getVisualization().isSnapToGeometryEnabled());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				getVisualization().isGridEnabled());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
				getVisualization().isGridEnabled());

		getPalettePreferences().setPaletteState(
				FlyoutPaletteComposite.STATE_PINNED_OPEN);

	}

	protected void saveProperties() {
		getVisualization().setRulerVisibility(
				((Boolean) getGraphicalViewer().getProperty(
						RulerProvider.PROPERTY_RULER_VISIBILITY))
						.booleanValue());
		getVisualization().setGridEnabled(
				((Boolean) getGraphicalViewer().getProperty(
						SnapToGrid.PROPERTY_GRID_ENABLED)).booleanValue());
		getVisualization().setSnapToGeometry(
				((Boolean) getGraphicalViewer().getProperty(
						SnapToGeometry.PROPERTY_SNAP_ENABLED)).booleanValue());
	}

	/**
	 * The <code>CommandStackListener</code> that listens for
	 * <code>CommandStack </code>changes.
	 */
	private CommandStackListener commandStackListener = new CommandStackListener() {
		public void commandStackChanged(EventObject event) {
			setDirty(getCommandStack().isDirty());
		}
	};

	/**
	 * Returns the <code>CommandStack</code> of this editor's
	 * <code>EditDomain</code>.
	 * 
	 * @return the <code>CommandStack</code>
	 */
	@Override
	public CommandStack getCommandStack() {
		return getEditDomain().getCommandStack();
	}

	/**
	 * Returns the <code>CommandStackListener</code>.
	 * 
	 * @return the <code>CommandStackListener</code>
	 */
	protected CommandStackListener getCommandStackListener() {
		return commandStackListener;
	}

	/**
	 * Returns the palette root.
	 */
	protected PaletteRoot getPaletteRoot() {
		if (palette == null) {
			palette = new EditorPaletteFactory().createPalette(visualization);
		}
		return palette;
	}

	protected FigureCanvas getEditor() {
		return (FigureCanvas) getGraphicalViewer().getControl();
	}

	@Override
	public SelectionSynchronizer getSelectionSynchronizer() {
		if (bmotionSelectionSynchronizer == null)
			bmotionSelectionSynchronizer = new BMotionSelectionSynchronizer(
					bmotionStudioEditor);
		return bmotionSelectionSynchronizer;
	}

	protected class BMotionOutlinePage extends ContentOutlinePage {

		private SashForm sash;

		private ScrollableThumbnail thumbnail;
		private DisposeListener disposeListener;

		public BMotionOutlinePage() {
			super(new TreeViewer());
		}

		public void init(IPageSite pageSite) {
			super.init(pageSite);
			IActionBars bars = pageSite.getActionBars();
			ActionRegistry ar = getActionRegistry();
			bars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
					ar.getAction(ActionFactory.UNDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.REDO.getId(),
					ar.getAction(ActionFactory.REDO.getId()));
			bars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
					ar.getAction(ActionFactory.DELETE.getId()));
			bars.setGlobalActionHandler(ActionFactory.COPY.getId(),
					ar.getAction(ActionFactory.COPY.getId()));
			bars.setGlobalActionHandler(ActionFactory.PASTE.getId(),
					ar.getAction(ActionFactory.PASTE.getId()));
			buildCustomActions(bars, ar);
			bars.updateActionBars();
		}

		protected void configureOutlineViewer() {
			getViewer().setEditDomain(getEditDomain());
			getViewer().setEditPartFactory(new BMSTreeEditPartFactory());
			ContextMenuProvider provider = new BMSContextMenuProvider(
					getViewer(), getActionRegistry());
			getViewer().setContextMenu(provider);
			getViewer().setKeyHandler(getCommonKeyHandler());
		}

		protected void initializeOutlineViewer() {
			setContents(getVisualization());
		}

		public void setContents(Object contents) {
			getViewer().setContents(contents);
		}

		protected void hookOutlineViewer() {
			getSelectionSynchronizer().addViewer(getViewer());
		}

		protected void unhookOutlineViewer() {
			getSelectionSynchronizer().removeViewer(getViewer());
			if (getGraphicalViewer().getControl() != null
					&& !getGraphicalViewer().getControl().isDisposed())
				getGraphicalViewer().getControl().removeDisposeListener(
						disposeListener);
		}

		@Override
		public void createControl(Composite parent) {
			initializeOverview(parent);
			getGraphicalViewer().getControl().addDisposeListener(
					disposeListener);
			configureOutlineViewer();
			hookOutlineViewer();
			initializeOutlineViewer();
			createMenu();
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

		protected void initializeOverview(Composite parent) {
			sash = new SashForm(parent, SWT.VERTICAL);

			getViewer().createControl(sash);

			Canvas canvas = new Canvas(sash, SWT.BORDER);
			canvas.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_WHITE));
			LightweightSystem lws = new LightweightSystem(canvas);

			thumbnail = new ScrollableThumbnail(
					(Viewport) ((ScalableRootEditPart) getGraphicalViewer()
							.getRootEditPart()).getFigure());
			thumbnail.setSource(((ScalableRootEditPart) getGraphicalViewer()
					.getRootEditPart())
					.getLayer(LayerConstants.PRINTABLE_LAYERS));

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

		public void dispose() {
			unhookOutlineViewer();
			gridColor.dispose();
			super.dispose();
		}

	}

	private void buildCustomActions(IActionBars bars, ActionRegistry ar) {

		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = reg
				.getExtensionPoint("de.bmotionstudio.gef.editor.installMenu");
		for (IExtension extension : extensionPoint.getExtensions()) {
			for (IConfigurationElement configurationElement : extension
					.getConfigurationElements()) {

				if ("menu".equals(configurationElement.getName())) {

					try {

						IInstallMenu installMenuClass = (IInstallMenu) configurationElement
								.createExecutableExtension("class");

						installMenuClass.installBar(bars, getActionRegistry());

					} catch (final CoreException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}

	public Visualization getVisualization() {
		return visualization;
	}

	public void setBMotionStudioEditor(BMotionStudioEditor bmotionStudioEditor) {
		this.bmotionStudioEditor = bmotionStudioEditor;
	}

	public BMotionStudioEditor getBMotionStudioEditor() {
		return bmotionStudioEditor;
	}

	public ScalableRootEditPart getRootEditPart() {
		return (ScalableRootEditPart) getGraphicalViewer().getRootEditPart();
	}

}
