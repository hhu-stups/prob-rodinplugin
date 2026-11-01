package de.prob.ui.services;

import de.prob.core.Animator;

/**
 * Enabled while a machine or context has been loaded successfully
 */
public class ModelLoadedProvider extends AnimatorStateProvider {

	public static final String SERVICE = "de.prob.core.model_loaded";

	public ModelLoadedProvider() {
		super(SERVICE);
	}

	@Override
	protected boolean computeEnabled() {
		return Animator.getAnimator().isMachineLoaded();
	}

}
