/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;

public class SetObserverCommand extends Command {

	private Observer oldObserver;
	private Observer newObserver;
	private Observer clonedNewObserver;
	private BControl control;

	public void execute() {
		// Clone the new observer
		try {
			clonedNewObserver = newObserver.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		// Set the new observer
		control.addObserver(newObserver);
	}

	public boolean canExecute() {
		if (newObserver == null || control == null)
			return false;
		return true;
	}

	public void undo() {
		// If we had an old observer, set the old one
		if (oldObserver != null) {
			control.addObserver(oldObserver);
			// else remove the observer
		} else {
			control.removeObserver(newObserver);
		}
	}

	public void redo() {
		// Redo method adds the cloned observer, since the observer could be
		// changed during set and redo action
		control.addObserver(clonedNewObserver);
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return this.control;
	}

	public Observer getOldObserver() {
		return oldObserver;
	}

	public void setOldObserver(Observer oldObserver) {
		this.oldObserver = oldObserver;
	}

	public Observer getNewObserver() {
		return newObserver;
	}

	public void setNewObserver(Observer newObserver) {
		this.newObserver = newObserver;
	}

}
