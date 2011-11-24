/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.core.internal;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

import de.prob.core.IComputationListener;
import de.prob.core.domainobjects.State;
import de.prob.logging.Logger;

public class ComputationListenerProxy implements IComputationListener {

	private final IConfigurationElement config;
	private IComputationListener instance;

	public ComputationListenerProxy(final IConfigurationElement config) {
		this.config = config;
	}

	public void computedState(final State state) {
		if (instance == null) {
			instance = init();
		}
		if (instance != null) {
			instance.computedState(state);
		}

	}

	private IComputationListener init() {
		try {
			return (IComputationListener) config
					.createExecutableExtension("class");
		} catch (CoreException e) {
			Logger.notifyUser("A Listener could not be instatiated. Class is: "
					+ config.getAttribute("class"), e);
			e.printStackTrace();
		}
		return null;
	}

}
