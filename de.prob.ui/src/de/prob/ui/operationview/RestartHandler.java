package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.core.Animator;
import de.prob.exceptions.ProBException;

public class RestartHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Animator animator = Animator.getAnimator();
		try {
			animator.getLanguageDependendPart().reload(animator);
		} catch (ProBException e) {
			e.notifyUserOnce();
			throw new ExecutionException("Restarting the machine failed", e);
		}
		return null;
	}

}
