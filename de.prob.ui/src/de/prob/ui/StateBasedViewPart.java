/**
 * 
 */
package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.ui.services.HistoryActiveProvider;

/**
 * This serves as a base class for views that depend on the current state of the
 * animation.
 * 
 * A message is shown as long no machine is loaded. To use this class, one must
 * overwrite {@link #createStatePartControl(Composite)} to create the specific
 * view and can overwrite {@link #stateReset()} and
 * {@link #stateChanged(State, Operation)} to react to state changes.
 * 
 * @author plagge
 */
public abstract class StateBasedViewPart extends ViewPart implements
		IAnimationListener, ILifecycleListener {

	private Composite parent;
	private Control noStateMessage;
	private Control stateView;

	abstract protected Control createStatePartControl(Composite parent);

	/**
	 * The machine was reseted. The GUI of the component is usually not
	 * initialized when this method is called.
	 * 
	 * So don't modify the GUI, wait for the coming
	 * {@link #stateChanged(State, Operation)} call later
	 */
	protected void stateReset() {
	}

	/**
	 * The current state has changed. When this method is called, the GUI
	 * components are initialized by {@link #createStatePartControl(Composite)},
	 * and the call takes place in the UI thread, so you can access the GUI.
	 * 
	 * @param currentState
	 * @param operation
	 */
	protected void stateChanged(final State currentState,
			final Operation operation) {
	}

	@Override
	public void createPartControl(final Composite parent) {
		this.parent = parent;
		StaticListenerRegistry.registerListener((ILifecycleListener) this);
		StaticListenerRegistry.registerListener((IAnimationListener) this);
		final Animator animator = Animator.getAnimator();
		if (animator.isMachineLoaded()) {
			createStateControl();
			final State state = animator.getCurrentState();
			final Operation op = animator.getHistory().getCurrent()
					.getOperation();
			stateChanged(state, op);
		} else {
			createNoStateMessage();
			reset();
		}
	}

	private void createNoStateMessage() {
		if (noStateMessage == null) {
			disposeStateView();
			Label message = new Label(parent, SWT.NONE);
			message.setText("no machine available");
			noStateMessage = message;
			parent.layout();
		}
	}

	private void createStateControl() {
		if (stateView == null) {
			disposeNoStateMessage();
			stateView = createStatePartControl(parent);
			parent.layout();
		}
	}

	private void disposeNoStateMessage() {
		if (noStateMessage != null && !noStateMessage.isDisposed()) {
			noStateMessage.dispose();
		}
		noStateMessage = null;
	}

	private void disposeStateView() {
		if (stateView != null && !stateView.isDisposed()) {
			stateView.dispose();
		}
		stateView = null;
	}

	@Override
	public void setFocus() {
		if (noStateMessage != null) {
			noStateMessage.setFocus();
		}
		if (stateView != null) {
			stateView.setFocus();
		}
	}

	@Override
	final public void currentStateChanged(final State currentState,
			final Operation operation) {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				createStateControl();
				stateChanged(currentState, operation);
			}
		};
		Display.getDefault().asyncExec(runnable);



	}

	@Override
	final public void reset() {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				createNoStateMessage();
				stateReset();
			}
		};
		Display.getDefault().asyncExec(runnable);
	}

	@Override
	public void dispose() {
		super.dispose();
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		disposeStateView();
		disposeNoStateMessage();
	}

}
