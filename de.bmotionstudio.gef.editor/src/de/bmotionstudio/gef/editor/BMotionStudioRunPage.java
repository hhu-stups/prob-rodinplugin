/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.ActionFactory;

import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.part.AppEditPartFactory;

public class BMotionStudioRunPage extends GraphicalEditor {

	private Visualization visualization;

	private KeyHandler keyHandler;

	// private final Cursor cursor = new Cursor(Display.getCurrent(),
	// SWT.CURSOR_HAND);

	public BMotionStudioRunPage(Visualization visualization) {
		setEditDomain(new DefaultEditDomain(this));
		this.visualization = visualization;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class type) {
		if (type == ZoomManager.class)
			return ((ScalableRootEditPart) getGraphicalViewer()
					.getRootEditPart()).getZoomManager();
		return super.getAdapter(type);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// ignore selection changed.
	}

	@Override
	protected void initializeGraphicalViewer() {

		final GraphicalViewer viewer = getGraphicalViewer();

		viewer.setContents(getVisualization());

		// viewer.getControl().addMouseMoveListener(new MouseMoveListener() {
		//
		// public void mouseMove(MouseEvent e) {
		//
		// EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
		// if (part instanceof AppAbstractEditPart) {
		// AppAbstractEditPart bpart = (AppAbstractEditPart) viewer
		// .findObjectAt(new Point(e.x, e.y));
		//
		// BControl bcontrol = (BControl) bpart.getModel();
		// if (bcontrol.hasEvent(AttributeConstants.EVENT_MOUSECLICK))
		// bpart.getFigure().setCursor(cursor);
		// }
		//
		// }
		//
		// });
		// viewer.getControl().addMouseListener(new MouseListener() {
		//
		// public void mouseDoubleClick(final MouseEvent e) {
		// EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
		// if (part instanceof AppAbstractEditPart) {
		// AppAbstractEditPart bpart = (AppAbstractEditPart) viewer
		// .findObjectAt(new Point(e.x, e.y));
		// bpart.executeEvent(AttributeConstants.EVENT_MOUSEDBLCLICK,
		// e);
		// }
		// }
		//
		// public void mouseDown(final MouseEvent e) {
		// EditPart part = viewer.findObjectAt(new Point(e.x, e.y));
		// if (part instanceof AppAbstractEditPart) {
		// AppAbstractEditPart bpart = (AppAbstractEditPart) viewer
		// .findObjectAt(new Point(e.x, e.y));
		// bpart.executeEvent(AttributeConstants.EVENT_MOUSECLICK, e);
		// }
		// }
		//
		// public void mouseUp(final MouseEvent e) {
		// }
		//
		// });

	}

	public Visualization getVisualization() {
		return this.visualization;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#configureGraphicalViewer()
	 **/
	protected void configureGraphicalViewer() {

		double[] zoomLevels;

		super.configureGraphicalViewer();
		ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer) getGraphicalViewer();

		viewer.setEditPartFactory(new AppEditPartFactory());

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
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

		keyHandler = new KeyHandler();

		keyHandler.put(KeyStroke.getPressed(SWT.DEL, 127, 0),
				getActionRegistry().getAction(ActionFactory.DELETE.getId()));

		keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));

		keyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
				getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));

		viewer.setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.NONE),
				MouseWheelZoomHandler.SINGLETON);

		viewer.setKeyHandler(keyHandler);

		loadProperties();

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Nothing to do here, this is never allowed
		throw new IllegalAccessError("No way to enter this method.");
	}

	protected void loadProperties() {
		getGraphicalViewer().setProperty(
				RulerProvider.PROPERTY_RULER_VISIBILITY, false);
		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
				false);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				false);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
				false);
	}

	public ScalableRootEditPart getRootEditPart() {
		return (ScalableRootEditPart) getGraphicalViewer().getRootEditPart();
	}

	/**
	 * The run page editor should be never dirty!
	 */
	public boolean isDirty() {
		return false;
	}

}
