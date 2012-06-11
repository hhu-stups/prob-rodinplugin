/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.observer;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.draw2d.IFigure;

import de.bmotionstudio.gef.editor.AbstractExpressionControl;
import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.internal.Animation;
import de.bmotionstudio.gef.editor.model.BControl;

/**
 * 
 * Observers are used to link controls to the model's state, i.e., they do the
 * same as the animation function in ProB. The main difference is, that we allow
 * to decompose the animation function into different aspects, i.e., if our
 * model contains information about the speed of a motor, we can separate all
 * information regarding the speed from the information regarding the
 * temperature. This allows us to write small functions and combine them rather
 * than writing a single function covering all aspects of the model.
 * 
 * @author Lukas Ladenberger
 * 
 */
public abstract class Observer extends AbstractExpressionControl {

	// private transient Boolean callBack = false;

	public Observer() {
		init();
	}

	protected Object readResolve() {
		init();
		// callBack = false;
		return this;
	}

	/**
	 * Method to initialize the observer. Gets the ID, name and description from
	 * the corresponding extension point
	 */
	private void init() {
		IConfigurationElement configElement = BMotionEditorPlugin
				.getObserverExtension(getClass().getName());
		if (configElement != null) {
			this.ID = configElement.getAttribute("class");
			this.name = configElement.getAttribute("name");
			this.description = configElement.getAttribute("description");
		}
	}

	/**
	 * Makes a copy of the observer
	 * 
	 * @return the cloned observer
	 */
	public Observer clone() throws CloneNotSupportedException {
		return (Observer) super.clone();
	}

	// public void setCallBack(Boolean callBack) {
	// this.callBack = callBack;
	// }
	//
	// public Boolean isCallBack() {
	// return callBack;
	// }

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
	public abstract void check(Animation animation, BControl control);

	/**
	 * Returns a corresponding wizard for the observer.
	 * 
	 * @param bcontrol
	 *            The corresponding control
	 * @return the corresponding wizard
	 */
	public abstract ObserverWizard getWizard(BControl control);

	public IFigure getToolTip(BControl control) {
		return null;
	}

	public void afterCheck(Animation animation, BControl control) {
	}

}
