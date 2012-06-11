/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.animation;

import java.util.HashSet;
import java.util.Set;

import de.prob.core.IAnimationListener;
import de.prob.core.IComputationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

public class StaticListenerRegistry implements ILifecycleListener,
		IComputationListener, IAnimationListener {

	private static final Set<ILifecycleListener> lifeCycleListeners = new HashSet<ILifecycleListener>();
	private static final Set<IComputationListener> computationListeners = new HashSet<IComputationListener>();
	private static final Set<IAnimationListener> animationListeners = new HashSet<IAnimationListener>();

	public static void registerListener(final ILifecycleListener listener) {
		lifeCycleListeners.add(listener);
	}

	public static void unregisterListener(final ILifecycleListener listener) {
		lifeCycleListeners.remove(listener);
	}

	public static void registerListener(final IComputationListener listener) {
		computationListeners.add(listener);
	}

	public static void unregisterListener(final IComputationListener listener) {
		computationListeners.remove(listener);
	}

	public static void registerListener(final IAnimationListener listener) {
		animationListeners.add(listener);
	}

	public static void unregisterListener(final IAnimationListener listener) {
		animationListeners.remove(listener);
	}

	public void reset() {
		for (final ILifecycleListener listener : lifeCycleListeners) {
			listener.reset();
		}
	}

	public void computedState(final State state) {
		for (final IComputationListener listener : computationListeners) {
			listener.computedState(state);
		}
	}

	public void currentStateChanged(final State currentState,
			final Operation operation) {
		for (final IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(currentState, operation);
		}
	}
}