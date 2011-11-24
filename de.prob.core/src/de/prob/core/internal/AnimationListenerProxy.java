/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.prob.core.IAnimationListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.logging.Logger;

public class AnimationListenerProxy implements IAnimationListener {

	private final IConfigurationElement config;
	private IAnimationListener instance;

	public AnimationListenerProxy(final IConfigurationElement config) {
		this.config = config;
	}

	public void currentStateChanged(final State currentState,
			final Operation operation) {
		if (instance == null) {
			instance = init();
		}
		if (instance != null) {
			instance.currentStateChanged(currentState, operation);
		}

	}

	private IAnimationListener init() {
		try {
			return (IAnimationListener) config
					.createExecutableExtension("class");
		} catch (CoreException e) {
			Logger.notifyUser("A Listener could not be instatiated. Class is: "
					+ config.getAttribute("class"), e);
			e.printStackTrace();
		}
		return null;
	}

}
