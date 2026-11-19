package de.prob.ui.services;

import org.eventb.core.IContextRoot;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.langdep.EventBAnimatorPart;

/**
 * Enabled while the loaded model is a context (not a machine)
 */
public class ContextLoadedProvider extends AnimatorStateProvider {

	public static final String SERVICE = "de.prob.core.context_loaded";

	public ContextLoadedProvider() {
		super(SERVICE);
	}

	@Override
	protected boolean computeEnabled() {
		final Animator animator = Animator.getAnimator();
		// checked first, so that probcli is not started just to find out
		if (!animator.isMachineLoaded())
			return false;
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		return ldp instanceof EventBAnimatorPart
				&& ((EventBAnimatorPart) ldp).getRoot() instanceof IContextRoot;
	}

}
