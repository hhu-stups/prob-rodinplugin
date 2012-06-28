/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import de.bmotionstudio.gef.editor.Animation;
import de.bmotionstudio.gef.editor.model.BControl;

public interface IObserver {

	/**
	 * This method is called after every state change. The method tells the
	 * control how it has to look like and how to behave.
	 * 
	 * @param animation
	 *            The running animation
	 * @param bcontrol
	 *            The corresponding control
	 * @throws BMotionObserverException
	 */
	public void check(Animation animation, BControl control);

}
