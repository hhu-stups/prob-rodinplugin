/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.gef.editor.command.RemoveSchedulerEventCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.scheduler.SchedulerEvent;

public class RemoveSchedulerEventAction extends WorkbenchPartAction {

	private SchedulerEvent schedulerEvent;
	private BControl control;

	public RemoveSchedulerEventAction(IWorkbenchPart workbenchPart) {
		super(workbenchPart);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	public void run() {
		execute(createRemoveSchedulerEventCommand());
	}

	public RemoveSchedulerEventCommand createRemoveSchedulerEventCommand() {
		RemoveSchedulerEventCommand command = new RemoveSchedulerEventCommand();
		command.setControl(this.control);
		command.setSchedulerEvent(this.schedulerEvent);
		return command;
	}

	public void setControl(BControl control) {
		this.control = control;

	}

	public void setSchedulerEvent(SchedulerEvent schedulerEvent) {
		this.schedulerEvent = schedulerEvent;
	}

}
