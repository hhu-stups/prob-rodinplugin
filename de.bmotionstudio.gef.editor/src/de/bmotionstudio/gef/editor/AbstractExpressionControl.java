/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.IObserver;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public abstract class AbstractExpressionControl extends BindingObject {

	protected transient String ID;
	protected transient String name;
	protected transient String description;

	public String getID() {
		return this.ID;
	}

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	/**
	 * This method is invoked before the expression control ({@link IObserver}
	 * or {@link SchedulerEvent}) will be deleted.
	 * 
	 * @param control
	 *            which holds the expression control
	 */
	public void beforeDelete(BControl control) {
	}

}
