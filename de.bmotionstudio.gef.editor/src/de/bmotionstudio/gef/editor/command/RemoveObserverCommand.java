/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;

public class RemoveObserverCommand extends Command {

	private BControl control;
	private Observer observer;

	public void execute() {
		control.removeObserver(observer.getID());
	}

	public boolean canExecute() {
		return true;
	}

	public void undo() {
		control.addObserver(observer);
	}

	public void setControl(BControl control) {
		this.control = control;
	}

	public BControl getControl() {
		return control;
	}

	public void setObserver(Observer observer) {
		this.observer = observer;
	}

	public Observer getObserver() {
		return observer;
	}

}
