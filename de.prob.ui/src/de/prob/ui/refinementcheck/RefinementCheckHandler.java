/**
 * 
 */
package de.prob.ui.refinementcheck;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.ProBCommandJob;
import de.prob.core.command.ConstraintBasedRefinementCheckCommand;

/**
 * 
 * @author krings
 */
public class RefinementCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		final Animator animator = Animator.getAnimator();
		if (animator.isMachineLoaded()) {
			performRefinementCheck(animator, shell);
		} else {
			MessageDialog
					.openError(
							shell,
							"Error: No ProB animation running",
							"To perform a constraint based refinement check, please start the animation of the model first.");
		}
		return null;
	}

	private void performRefinementCheck(final Animator animator,
			final Shell shell) throws ExecutionException {
		final ConstraintBasedRefinementCheckCommand command = new ConstraintBasedRefinementCheckCommand();
		final Job job = new ProBCommandJob(
				"Performing CBC Refinement Checking", animator, command);
		job.setUser(true);
		job.addJobChangeListener(new RefinementCheckFinishedListener(shell));
		job.schedule();
	}

}
