package de.prob.ui.services;

import de.prob.core.Animator;

/**
 * Enabled while the history has steps up to the current state, i.e. while
 * there is a trace to save (see the History view's Save Trace button)
 */
public class TraceSavableProvider extends AnimatorStateProvider {

	public static final String SERVICE = "de.prob.core.trace_savable";

	public TraceSavableProvider() {
		super(SERVICE);
	}

	@Override
	protected boolean computeEnabled() {
		final Animator animator = Animator.getAnimator();
		// checked first, so that probcli is not started just to find out
		return animator.isMachineLoaded()
				&& animator.getHistory().getCurrentPosition() > 0;
	}

}
