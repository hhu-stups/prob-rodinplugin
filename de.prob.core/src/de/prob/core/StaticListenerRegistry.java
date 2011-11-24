/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.core;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

/**
 * The Registry is registered as a listener to the extension point. It is a
 * simple way to dynamically register new listeners without the need to use the
 * extension point
 * 
 * @author Jens Bendisposto
 */
public final class StaticListenerRegistry implements ILifecycleListener,
		IComputationListener, IAnimationListener {

	private static final ListenerRegistry registry = new ListenerRegistry();

	public static void registerLifecycleListener(
			final ILifecycleListener listener) {
		registry.registerLifecycleListener(listener);
	}

	public static void unregisterLifecycleListener(
			final ILifecycleListener listener) {
		registry.unregisterLifecycleListener(listener);
	}

	public static void registerComputationListener(
			final IComputationListener listener) {
		registry.registerComputationListener(listener);
	}

	public static void unregisterComputationListener(
			final IComputationListener listener) {
		registry.unregisterComputationListener(listener);
	}

	public static void registerAnimationListener(
			final IAnimationListener listener) {
		registry.registerAnimationListener(listener);
	}

	public static void unregisterAnimationListener(
			final IAnimationListener listener) {
		registry.unregisterAnimationListener(listener);
	}

	public void reset() {
		registry.reset();
	}

	public void computedState(final State state) {
		registry.computedState(state);
	}

	public void currentStateChanged(final State currentState,
			final Operation operation) {
		registry.currentStateChanged(currentState, operation);
	}
}
