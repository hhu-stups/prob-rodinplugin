/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.prob.core.ILifecycleListener;
import de.prob.logging.Logger;

public class LifeCycleListenerProxy implements ILifecycleListener {

	private ILifecycleListener instance;
	private final IConfigurationElement config;

	public LifeCycleListenerProxy(final IConfigurationElement config) {
		this.config = config;
	}

	public void reset() {
		if (instance == null) {
			instance = init();
		}
		if (instance != null) {
			instance.reset();
		}
	}

	private ILifecycleListener init() {
		try {
			return (ILifecycleListener) config
					.createExecutableExtension("class");
		} catch (CoreException e) {
			Logger.notifyUser("A Listener could not be instatiated. Class is: "
					+ config.getAttribute("class"), e);
			e.printStackTrace();
		}
		return null;
	}
}
