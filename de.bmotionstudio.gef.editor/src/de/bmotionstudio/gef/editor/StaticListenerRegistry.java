/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import de.prob.core.IAnimationListener;
import de.prob.core.IComputationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;

public class StaticListenerRegistry implements ILifecycleListener,
		IComputationListener, IAnimationListener {

	// copy-on-write: editors unregister themselves while a reset is being sent
	// (reset -> removeRunPage -> unregister), and editors register from the UI
	// thread while ProB threads send events
	private static final Set<ILifecycleListener> lifeCycleListeners = new CopyOnWriteArraySet<ILifecycleListener>();
	private static final Set<IComputationListener> computationListeners = new CopyOnWriteArraySet<IComputationListener>();
	private static final Set<IAnimationListener> animationListeners = new CopyOnWriteArraySet<IAnimationListener>();

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

	@Override
	public void reset() {
		for (final ILifecycleListener listener : lifeCycleListeners) {
			listener.reset();
		}
	}

	@Override
	public void computedState(final State state) {
		for (final IComputationListener listener : computationListeners) {
			listener.computedState(state);
		}
	}

	@Override
	public void currentStateChanged(final State currentState,
			final Operation operation) {
		for (final IAnimationListener listener : animationListeners) {
			listener.currentStateChanged(currentState, operation);
		}
	}
}