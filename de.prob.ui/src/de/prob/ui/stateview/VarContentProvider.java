/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.prob.ui.stateview.statetree.StateTreeElement;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * This class is the ContentProvider for the main tree in the StateView.
 * 
 * Its input is an array of {@link StateTreeElement}s.
 * 
 * @author plagge
 */
class VarContentProvider implements ITreeContentProvider {
	public VarContentProvider() {
	}

	public Object[] getChildren(final Object parentElement) {
		StateTreeElement ste = toTreeElement(parentElement);
		StaticStateElement[] children;
		if (ste == null) {
			children = new StaticStateElement[0];
		} else {
			children = ste.getChildren();
		}
		return children;
	}

	public Object getParent(final Object element) {
		StateTreeElement ste = toTreeElement(element);
		return ste != null ? ste.getParent() : null;
	}

	public boolean hasChildren(final Object element) {
		StateTreeElement ste = toTreeElement(element);
		return ste != null && ste.hasChildren();
	}

	private static StateTreeElement toTreeElement(final Object object) {
		final StateTreeElement ste;
		if (object != null && object instanceof StateTreeElement) {
			ste = (StateTreeElement) object;
		} else {
			ste = null;
		}
		return ste;
	}

	public Object[] getElements(final Object inputElement) {
		final StateTreeElement[] elements;
		if (inputElement != null && inputElement instanceof StateTreeElement[]) {
			elements = (StateTreeElement[]) inputElement;
		} else {
			elements = null;
		}
		return elements;
	}

	public void dispose() {

	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
	}

}
