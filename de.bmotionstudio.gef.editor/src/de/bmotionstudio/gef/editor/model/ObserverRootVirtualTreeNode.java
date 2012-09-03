/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.model;

import java.util.Collection;

import de.bmotionstudio.gef.editor.observer.Observer;

public class ObserverRootVirtualTreeNode {

	private Collection<Observer> observer;
	private BControl control;

	public ObserverRootVirtualTreeNode(BControl control) {
		this.observer = control.getObservers().values();
		this.control = control;
	}

	public Collection<Observer> getObserver() {
		return observer;
	}

	public BControl getControl() {
		return control;
	}

}
