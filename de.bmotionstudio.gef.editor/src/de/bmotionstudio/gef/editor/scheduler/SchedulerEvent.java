/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.scheduler;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.swt.widgets.Shell;

import de.bmotionstudio.gef.editor.AbstractExpressionControl;
import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.model.BControl;

/**
 * 
 * Scheduler Events are assigned to events (i.e. on-click event) or to
 * schedulers. A scheduler is an independent thread attempting to execute a set
 * of scheduler events. It is very useful when the user does not want to execute
 * each scheduler event by hand during an animation.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class SchedulerEvent extends AbstractExpressionControl
		implements Cloneable {

	private transient String eventID;

	public SchedulerEvent() {
		init();
	}

	protected Object readResolve() {
		init();
		return this;
	}

	/**
	 * Method to initialize the scheduler event. Gets the ID, name and
	 * description from the corresponding extension point
	 */
	private void init() {
		IConfigurationElement configElement = BMotionEditorPlugin
				.getSchedulerExtension(getClass().getName());
		if (configElement != null) {
			this.ID = configElement.getAttribute("class");
			this.name = configElement.getAttribute("name");
			this.description = configElement.getAttribute("description");
		}
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public String getEventID() {
		return eventID;
	}

	/**
	 * Executes the scheduler event (i.e. execute operation).
	 * 
	 * @param animation
	 *            The running animation
	 * @param bcontrol
	 *            The corresponding control
	 */
	public abstract void execute(Animation animation, BControl bcontrol);

	/**
	 * Returns a corresponding wizard for the scheduler event.
	 * 
	 * @param bcontrol
	 *            The corresponding control
	 * @return the corresponding wizard
	 */
	public abstract SchedulerWizard getWizard(Shell shell, BControl bcontrol);

	/**
	 * Makes a copy of the scheduler event
	 * 
	 * @return the cloned scheduler event
	 */
	public SchedulerEvent clone() throws CloneNotSupportedException {
		return (SchedulerEvent) super.clone();
	}

}
