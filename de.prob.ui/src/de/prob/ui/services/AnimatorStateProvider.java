package de.prob.ui.services;

import org.eclipse.swt.widgets.Display;

import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.ui.StaticListenerRegistry;

/**
 * A boolean source provider whose value is derived from the animator. It is
 * recomputed whenever a model is (re)loaded, a load fails or the state
 * changes, so that commands are enabled exactly when they can be used.
 */
public abstract class AnimatorStateProvider extends AbstractBoolProvider
		implements ILifecycleListener, IAnimationListener {

	public AnimatorStateProvider(final String service) {
		super(service);
		setEnabled(computeEnabled());
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
		StaticListenerRegistry.registerListener((IAnimationListener) this);
	}

	/**
	 * Called from the thread announcing the change, not necessarily the UI
	 * thread
	 */
	protected abstract boolean computeEnabled();

	@Override
	public void reset() {
		update();
	}

	@Override
	public void currentStateChanged(final State currentState,
			final Operation operation) {
		update();
	}

	private void update() {
		final boolean enabled = computeEnabled();
		final Display display = Display.getDefault();
		if (display.isDisposed())
			return; // workbench shutting down
		// source changes must be reported in the UI thread
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				setEnabled(enabled);
			}
		});
	}

	@Override
	public void dispose() {
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		super.dispose();
	}
}
