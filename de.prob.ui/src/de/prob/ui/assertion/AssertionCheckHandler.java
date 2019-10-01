/**
 * 
 */
package de.prob.ui.assertion;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.ProBCommandJob;
import de.prob.core.command.ConstraintBasedAssertionCheckCommand;
import de.prob.logging.Logger;

/**
 * This handler provides a way to start the static assertion checking command
 * 
 * @author plagge
 */
public class AssertionCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (Animator.getAnimator().isMachineLoaded()) {
			performAssertionCheck(shell);
		} else {
			Logger.notifyUser("No ProB animation running. This is a bug. Please submit a report. Error in declaraion for class DeadlockCheckHandler");
		}
		return null;
	}

	private void performAssertionCheck(final Shell shell)
			throws ExecutionException {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		startCheck(animator, ldp, shell);

	}

	private void startCheck(final Animator animator,
			final LanguageDependendAnimationPart ldp, final Shell shell)
			throws ExecutionException {
		final ConstraintBasedAssertionCheckCommand command = new ConstraintBasedAssertionCheckCommand();
		final Job job = new ProBCommandJob(
				"Checking Context Theorems / Assertions", animator, command);
		job.setUser(true);
		job.addJobChangeListener(new AssertionCheckFinishedListener(shell));
		job.schedule();
	}
}
