/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.command;

import org.eclipse.gef.commands.Command;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.observer.Observer;

public class ObserverCommand extends Command {

	private String className;
	private Observer clonedObserver;
	private Observer newObserver;
	private Observer clonedNewObserver;
	private BControl control;

	public void execute() {

		try {
			clonedNewObserver = newObserver.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		control.addObserver(newObserver);

	}

	public boolean canExecute() {
		return true;
	}

	public void undo() {

		// Remove completely new Observer
		if (clonedObserver == null) {
			control.removeObserver(getClassName());
		} else { // Reset Observer
			control.addObserver(clonedObserver);
		}

	}

	public void redo() {
		control.addObserver(clonedNewObserver);
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

	public Observer getClonedObserver() {
		return clonedObserver;
	}

	public void setClonedObserver(Observer clonedObserver) {
		this.clonedObserver = clonedObserver;
	}

	public Observer getNewObserver() {
		return newObserver;
	}

	public void setNewObserver(Observer newObserver) {
		this.newObserver = newObserver;
	}

}
