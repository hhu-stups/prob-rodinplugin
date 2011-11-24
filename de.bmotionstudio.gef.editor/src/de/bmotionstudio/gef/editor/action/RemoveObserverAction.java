/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.action;

import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.ui.IWorkbenchPart;

import de.bmotionstudio.gef.editor.command.RemoveObserverCommand;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;

public class RemoveObserverAction extends WorkbenchPartAction {

	private Observer observer;
	private BControl control;

	public RemoveObserverAction(IWorkbenchPart workbenchPart) {
		super(workbenchPart);
	}

	@Override
	protected boolean calculateEnabled() {
		return true;
	}

	public void run() {
		execute(createRemoveObserverCommand());
	}

	public RemoveObserverCommand createRemoveObserverCommand() {
		RemoveObserverCommand command = new RemoveObserverCommand();
		command.setControl(this.control);
		command.setObserver(this.observer);
		return command;
	}

	public void setControl(BControl control) {
		this.control = control;

	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

}
