/**
 * 
 */
package de.prob.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.Animator;
import de.prob.core.IAnimationListener;
import de.prob.core.ILifecycleListener;
import de.prob.core.domainobjects.HistoryItem;
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

	// lays the view out again when it becomes visible: it stayed empty
	// when BMotion Studio loaded the machine while switching perspectives
	private final IPartListener2 visibilityListener = new IPartListener2() {
		@Override
		public void partVisible(final IWorkbenchPartReference partRef) {
			if (partRef.getPart(false) != StateBasedViewPart.this
					|| isPartDisposed())
				return;
			// no ProB calls if the state is already shown, the animator may
			// be busy (e.g. model checking)
			if (stateView != null || !showCurrentState()) {
				parent.layout(true, true);
				parent.redraw();
			}
		}
	};

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
		getSite().getPage().addPartListener(visibilityListener);
		if (!showCurrentState()) {
			createNoStateMessage();
			reset();
		}
	}

	/**
	 * Shows the current state of the animator, if any.
	 * 
	 * @return false if no machine is loaded or it has no state yet (an event
	 *         follows when it has one)
	 */
	private boolean showCurrentState() {
		final Animator animator = Animator.getAnimator();
		final HistoryItem current = animator.getHistory().getCurrent();
		if (!animator.isMachineLoaded() || current == null)
			return false;
		createStateControl();
		stateChanged(current.getState(), current.getOperation());
		parent.layout(true, true);
		return true;
	}

	private void createNoStateMessage() {
		if (noStateMessage == null) {
			disposeStateView();
			Label message = new Label(parent, SWT.NONE);
			message.setText("no machine available");
			noStateMessage = message;
			parent.layout(true, true);
		}
	}

	private void createStateControl() {
		if (stateView == null) {
			disposeNoStateMessage();
			stateView = createStatePartControl(parent);
			parent.layout(true, true);
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
				// the view may have been closed in the meantime
				if (isPartDisposed())
					return;
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
				if (isPartDisposed())
					return;
				createNoStateMessage();
				stateReset();
			}
		};
		Display.getDefault().asyncExec(runnable);
	}

	/**
	 * @return true if the view's controls are gone (e.g. the view was closed
	 *         after an update had been scheduled with asyncExec)
	 */
	protected boolean isPartDisposed() {
		return parent == null || parent.isDisposed();
	}

	@Override
	public void dispose() {
		getSite().getPage().removePartListener(visibilityListener);
		super.dispose();
		StaticListenerRegistry.unregisterListener((ILifecycleListener) this);
		StaticListenerRegistry.unregisterListener((IAnimationListener) this);
		disposeStateView();
		disposeNoStateMessage();
	}

}
