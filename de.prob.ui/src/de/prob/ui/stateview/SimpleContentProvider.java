/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.jface.viewers.Viewer;

import de.prob.ui.stateview.LabelViewer.ISimpleContentProvider;

/**
 * The default base class for {@link ISimpleContentProvider}
 * 
 * @author plagge
 */
public abstract class SimpleContentProvider implements
		LabelViewer.ISimpleContentProvider {

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
