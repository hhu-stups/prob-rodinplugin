/**
 * 
 */
package de.prob.ui.invcheck;

import java.util.Collection;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.ProBCommandJob;
import de.prob.core.command.ConstraintBasedInvariantCheckCommand;
import de.prob.core.domainobjects.MachineDescription;

/**
 * 
 * @author plagge
 */
public class InvariantCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		final Animator animator = Animator.getAnimator();
		if (animator.isMachineLoaded()) {
			performInvariantCheck(animator, shell);
		} else {
			MessageDialog
					.openError(
							shell,
							"Error: No ProB animation running",
							"To perform a constraint based invariant check, please start the animation of the model first.");
		}
		return null;
	}

	private void performInvariantCheck(final Animator animator,
			final Shell shell) throws ExecutionException {
		final MachineDescription md = animator.getMachineDescription();
		final Collection<String> events = md.getEventNames();
		if (events.isEmpty()) {
			MessageDialog.openError(shell, "Invariant Check: No Events",
					"The model does not contain any events to check!");

		} else {
			final InvariantCheckDialog dialog = new InvariantCheckDialog(shell,
					events);
			final int status = dialog.open();
			if (status == InputDialog.OK) {
				startCheck(animator, dialog.getSelected(), shell);
			}
		}
	}

	private void startCheck(final Animator animator,
			final Collection<String> events, final Shell shell)
			throws ExecutionException {
		final ConstraintBasedInvariantCheckCommand command = new ConstraintBasedInvariantCheckCommand(
				events);
		final Job job = new ProBCommandJob(
				"Checking for Invariant Preservation", animator, command);
		job.setUser(true);
		job.addJobChangeListener(new InvariantCheckFinishedListener(shell));
		job.schedule();
	}

}
