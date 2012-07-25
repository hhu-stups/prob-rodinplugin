/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.ButtonModel;
import org.eclipse.draw2d.ChangeEvent;
import org.eclipse.draw2d.ChangeListener;
import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FlowLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.bmotionstudio.gef.editor.AttributeConstants;
import de.bmotionstudio.gef.editor.attribute.AbstractAttribute;
import de.bmotionstudio.gef.editor.figure.AbstractBMotionFigure;
import de.bmotionstudio.gef.editor.library.AbstractLibraryCommand;
import de.bmotionstudio.gef.editor.library.AttributeRequest;
import de.bmotionstudio.gef.editor.model.BConnection;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;
import de.bmotionstudio.gef.editor.observer.IObserverListener;
import de.bmotionstudio.gef.editor.observer.Observer;

public abstract class AppAbstractEditPart extends AbstractGraphicalEditPart
		implements PropertyChangeListener, IObserverListener, IAdaptable,
		NodeEditPart {

	private final Cursor cursorHover = new Cursor(Display.getCurrent(),
			SWT.CURSOR_HAND);

	protected ConnectionAnchor anchor;

	private ChangeListener changeListener = new ChangeListener() {
		@Override
		public void handleStateChanged(ChangeEvent event) {
			if (getCastedModel().hasEvent(AttributeConstants.EVENT_MOUSECLICK)) {
				if (event.getPropertyName().equals(
						ButtonModel.MOUSEOVER_PROPERTY))
					getFigure().setCursor(cursorHover);
			}
			if (event.getPropertyName()
					.equals(ButtonModel.PRESSED_PROPERTY)) {
				AbstractBMotionFigure f = (AbstractBMotionFigure) getFigure();
				if (f.getModel().isPressed())
					executeEvent(AttributeConstants.EVENT_MOUSECLICK);
			}
		}
	};

	private String[] layoutAttributes = { BControl.PROPERTY_LAYOUT,
			BControl.PROPERTY_LOCATION, AttributeConstants.ATTRIBUTE_X,
			AttributeConstants.ATTRIBUTE_Y, AttributeConstants.ATTRIBUTE_WIDTH,
			AttributeConstants.ATTRIBUTE_HEIGHT };

	public void activate() {
		if (!isActive()) {
			super.activate();
			((BControl) getModel()).addPropertyChangeListener(this);
			((BControl) getModel()).addObserverListener(this);
			if (getFigure() instanceof AbstractBMotionFigure) {
				AbstractBMotionFigure af = (AbstractBMotionFigure) getFigure();
				if (isRunning())
					af.addChangeListener(changeListener);
				af.activateFigure();
			}
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((BControl) getModel()).removePropertyChangeListener(this);
			((BControl) getModel()).removeObserverListener(this);
			if (getFigure() instanceof AbstractBMotionFigure) {
				AbstractBMotionFigure af = (AbstractBMotionFigure) getFigure();
				if (isRunning())
					af.removeChangeListener(changeListener);
				af.deactivateFigure();
			}
		}
	}

	protected abstract IFigure createEditFigure();

	@Override
	protected void createEditPolicies() {
		if (isRunning())
			prepareRunPolicies();
		else
			prepareEditPolicies();
	}

	protected abstract void prepareEditPolicies();

	protected abstract void prepareRunPolicies();

	protected Boolean isRunning() {
		return ((BControl) getModel()).getVisualization().isRunning();
	}

	@Override
	protected IFigure createFigure() {
		final IFigure figure = createEditFigure();
		IFigure toolTipFigure = getToolTip();
		if (toolTipFigure != null)
			figure.setToolTip(toolTipFigure);
		if (!isRunning()) {
			if (figure instanceof AbstractBMotionFigure) {
				((AbstractBMotionFigure) figure).setEnabled(false);
			}
		}
		return figure;
	}

	@Override
	public void performRequest(Request req) {
		if (!isRunning()) {
			if (req.getType().equals(RequestConstants.REQ_OPEN)) {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench()
							.getActiveWorkbenchWindow().getActivePage();
					page.showView(IPageLayout.ID_PROP_SHEET);
				} catch (PartInitException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	protected void refreshVisuals() {
		IFigure figure = getFigure();
		BControl model = (BControl) getModel();
		for (Entry<String, AbstractAttribute> e : model.getAttributes()
				.entrySet()) {
			PropertyChangeEvent evt = new PropertyChangeEvent(model,
					e.getKey(), null, e.getValue().getValue());
			refreshEditFigure(figure, model, evt);
		}
		refreshEditLayout(figure, model);
	}

	public abstract void refreshEditFigure(IFigure figure, BControl model,
			PropertyChangeEvent pEvent);

	protected void refreshEditLayout(IFigure figure, BControl control) {
		if (!(control instanceof Visualization)) {
			if (getFigure().getParent() != null)
				getFigure().getParent().setConstraint((IFigure) figure,
						control.getLayout());
			getFigure().setPreferredSize(control.getDimension());
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt) {
		final IFigure figure = (IFigure) getFigure();
		final BControl model = (BControl) getModel();
		String propName = evt.getPropertyName();

		if (BControl.SOURCE_CONNECTIONS_PROP.equals(propName)) {
			refreshSourceConnections();
		} else if (BControl.TARGET_CONNECTIONS_PROP.equals(propName)) {
			refreshTargetConnections();
		}
		if (propName.equals(BControl.PROPERTY_ADD)
				|| propName.equals(BControl.PROPERTY_REMOVE)) {
			refreshChildren();
		} else if (Arrays.asList(layoutAttributes).contains(propName)) {
			// Layout attribute
			if (isRunning()) {
				// Display.getDefault().asyncExec(new Runnable() {
				// @Override
				// public void run() {
						refreshEditLayout(figure, model);
				// }
				// });
			} else {
				refreshEditLayout(figure, model);
			}
		} else {

			// Custom attribute
			if (isRunning()) {
				// Display.getDefault().asyncExec(new Runnable() {
				// @Override
				// public void run() {
						refreshEditFigure(figure, model, evt);
				// }
				// });
			} else {
				refreshEditFigure(figure, model, evt);
			}
		}
	}

	public List<BControl> getModelChildren() {
		return new ArrayList<BControl>();
	}

	public void executeEvent(String event) {
		getCastedModel().executeEvent(event);
	}

	protected IFigure getToolTip() {

		Figure fig = new Figure();
		fig.setLayoutManager(new FlowLayout());

		Collection<Observer> observerList = ((BControl) getModel())
				.getObservers().values();
		for (Observer observer : observerList) {
			IFigure observerFigure = observer.getToolTip((BControl) getModel());
			if (observerFigure != null) {
				fig.add(observerFigure);
			}
		}

		return fig;

	}

	@Override
	public void addedObserver(BControl control, Observer observer) {
		// Update Tooltip
		getFigure().setToolTip(getToolTip());
	}

	@Override
	public void removedObserver(BControl control) {

	}

	public AbstractLibraryCommand getLibraryCommand(AttributeRequest request) {
		return null;
	}

	protected BControl getCastedModel() {
		return (BControl) getModel();
	}

	protected ConnectionAnchor getConnectionAnchor() {
		if (anchor == null) {
			anchor = new ChopboxAnchor(getFigure());
		}
		return anchor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelSourceConnections
	 * ()
	 */
	protected List<BConnection> getModelSourceConnections() {
		return getCastedModel().getSourceConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.editparts.AbstractGraphicalEditPart#getModelTargetConnections
	 * ()
	 */
	protected List<BConnection> getModelTargetConnections() {
		return getCastedModel().getTargetConnections();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getSourceConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .ConnectionEditPart)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return getConnectionAnchor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.gef.NodeEditPart#getTargetConnectionAnchor(org.eclipse.gef
	 * .Request)
	 */
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return getConnectionAnchor();
	}

}
