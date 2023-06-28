/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.model.BControl;

/**
 * @author Lukas Ladenberger
 * 
 */
public interface IObserverListener {

	public void addedObserver(BControl control, Observer observer);

	public void removedObserver(BControl control);

}
