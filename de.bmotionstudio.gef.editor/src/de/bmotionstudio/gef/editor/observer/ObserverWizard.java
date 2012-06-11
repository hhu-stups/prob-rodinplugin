/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import org.eclipse.swt.graphics.Point;

import de.bmotionstudio.gef.editor.BMotionAbstractWizard;
import de.bmotionstudio.gef.editor.model.BControl;

/**
 * 
 * The BMotion Studio provides an easy way to handle Observers. For this,
 * Observers can have a corresponding wizard. The user can open it by calling
 * the context menu of a Control.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class ObserverWizard extends BMotionAbstractWizard {

	private Observer observer;

	protected Boolean observerDelete = false;

	public ObserverWizard(BControl control, Observer observer) {
		super(control);
		this.observer = observer;
	}

	public Observer getObserver() {
		return this.observer;
	}

	protected void setObserverDelete(Boolean b) {
		this.observerDelete = b;
	}

	public Boolean isObserverDelete() {
		return this.observerDelete;
	}

	public abstract Point getSize();
	
	@Override
	public String getName() {
		return observer.getName();
	}

}
