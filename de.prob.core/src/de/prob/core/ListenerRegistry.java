/**
 * 
 */
package de.prob.core;

import java.util.HashSet;
import java.util.Set;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.logging.Logger;

/**
 * @author plagge
 * 
 */
public class ListenerRegistry implements ILifecycleListener,
		IComputationListener, IAnimationListener {

	private final Set<ILifecycleListener> lifeCycleListeners = new HashSet<ILifecycleListener>();
	private final Set<IComputationListener> computationListeners = new HashSet<IComputationListener>();
	private final Set<IAnimationListener> animationListeners = new HashSet<IAnimationListener>();

	public void registerLifecycleListener(final ILifecycleListener listener) {
		lifeCycleListeners.add(listener);
	}

	public void unregisterLifecycleListener(final ILifecycleListener listener) {
		lifeCycleListeners.remove(listener);
	}

	public void registerComputationListener(final IComputationListener listener) {
		computationListeners.add(listener);
	}

	public void unregisterComputationListener(
			final IComputationListener listener) {
		computationListeners.remove(listener);
	}

	public void registerAnimationListener(final IAnimationListener listener) {
		animationListeners.add(listener);
	}

	public void unregisterAnimationListener(final IAnimationListener listener) {
		animationListeners.remove(listener);
	}

	public void reset() {
		for (final ILifecycleListener listener : lifeCycleListeners) {
			try {
				listener.reset();
			} catch (final RuntimeException e) {
				final String classname = listener.getClass().getCanonicalName();
				final String message = "Runtime Exception thrown in bad behaving listener class "
						+ classname + " while sending reset event";
				Logger.notifyUser(message, e);
			}
		}
	}

	public void computedState(final State state) {
		for (final IComputationListener listener : computationListeners) {
			try {
				listener.computedState(state);
			} catch (final RuntimeException e) {
				final String classname = listener.getClass().getCanonicalName();
				final String message = "Runtime Exception thrown in bad behaving listener class "
						+ classname + " while sending computedState event";
				Logger.notifyUser(message, e);
			}
		}
	}

	public void currentStateChanged(final State currentState,
			final Operation operation) {
		for (final IAnimationListener listener : animationListeners) {
			try {
				listener.currentStateChanged(currentState, operation);
			} catch (final RuntimeException e) {
				final String classname = listener.getClass().getCanonicalName();
				final String message = "Runtime Exception thrown in bad behaving listener class "
						+ classname
						+ " while sending currentStateChanged event";
				Logger.notifyUser(message, e);
			}
		}
	}

}
