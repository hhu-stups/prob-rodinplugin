/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public class RemoveSchedulerEventCommand extends Command {

	private BControl control;
	private SchedulerEvent schedulerEvent;

	public void execute() {
		control.removeEvent(schedulerEvent.getEventID());
	}

	public boolean canExecute() {
		return true;
	}

	public void undo() {
		control.addEvent(schedulerEvent.getEventID(), schedulerEvent);
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

	public void setSchedulerEvent(SchedulerEvent schedulerEvent) {
		this.schedulerEvent = schedulerEvent;
	}

	public SchedulerEvent getSchedulerEvent() {
		return this.schedulerEvent;
	}

}
