/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import java.util.ArrayList;
import java.util.List;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.ObserverRootVirtualTreeNode;
import de.bmotionstudio.gef.editor.observer.IObserverListener;
import de.bmotionstudio.gef.editor.observer.Observer;

public class ObserverRootTreeEditpart extends BMSAbstractTreeEditPart implements
		IObserverListener {

	public void activate() {
		if (!isActive()) {
			super.activate();
			((ObserverRootVirtualTreeNode) getModel()).getControl()
					.addObserverListener(this);
		}
	}

	public void deactivate() {
		if (isActive()) {
			super.deactivate();
			((ObserverRootVirtualTreeNode) getModel()).getControl()
					.removeObserverListener(this);
		}
	}

	@Override
	protected List<Object> getModelChildren() {
		ObserverRootVirtualTreeNode model = (ObserverRootVirtualTreeNode) getModel();
		return new ArrayList<Object>(model.getObserver());
	}

	@Override
	public void refreshVisuals() {
		setWidgetText("Observer");
		setWidgetImage(BMotionStudioImage
				.getImage(EditorImageRegistry.IMG_ICON_OBSERVER));
	}

	@Override
	public void addedObserver(BControl control, Observer observer) {
		refreshChildren();
	}

	@Override
	public void removedObserver(BControl control) {
		refreshChildren();
	}

}
