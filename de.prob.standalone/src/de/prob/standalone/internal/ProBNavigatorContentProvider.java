package de.prob.standalone.internal;

/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ProBNavigatorContentProvider implements ITreeContentProvider {

	public Object getParent(final Object element) {
		return null;
	}

	public boolean hasChildren(final Object element) {
		return false;
	}

	public Object[] getElements(final Object inputElement) {
		return null;
	}

	public void dispose() {
		// do nothing
	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		// do nothing
	}

	public Object[] getChildren(Object parentElement) {
		return null;
	}

}
