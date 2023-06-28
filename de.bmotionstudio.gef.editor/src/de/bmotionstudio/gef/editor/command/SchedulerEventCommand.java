/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public class SchedulerEventCommand extends Command {

	private String className;
	private String eventID;
	private SchedulerEvent clonedSchedulerEvent;
	private SchedulerEvent newSchedulerEvent;
	private SchedulerEvent clonedNewSchedulerEvent;
	private BControl control;

	public void execute() {

		try {
			clonedNewSchedulerEvent = newSchedulerEvent.clone();
		} catch (CloneNotSupportedException e) {
		}

		if (clonedSchedulerEvent == null) {
			control.addEvent(eventID, newSchedulerEvent);
		}

	}

	public boolean canExecute() {
		return true;
	}

	public void undo() {

		// Remove completely new Observer
		if (clonedSchedulerEvent == null) {
			control.removeEvent(eventID);
		} else { // Reset Observer
			control.addEvent(eventID, clonedSchedulerEvent);
		}

	}

	public void redo() {
		control.addEvent(eventID, clonedNewSchedulerEvent);
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassName() {
		return className;
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return this.control;
	}

	public String getEventID() {
		return eventID;
	}

	public void setEventID(String eventID) {
		this.eventID = eventID;
	}

	public SchedulerEvent getClonedSchedulerEvent() {
		return clonedSchedulerEvent;
	}

	public void setClonedSchedulerEvent(SchedulerEvent clonedSchedulerEvent) {
		this.clonedSchedulerEvent = clonedSchedulerEvent;
	}

	public SchedulerEvent getNewSchedulerEvent() {
		return newSchedulerEvent;
	}

	public void setNewSchedulerEvent(SchedulerEvent newSchedulerEvent) {
		this.newSchedulerEvent = newSchedulerEvent;
	}

}
