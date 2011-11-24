/**
 * 
 */
package de.prob.ui.deadlock;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.core.command.ConstraintBasedDeadlockCheckCommand.ResultType;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * This JobChangeAdapter presents the user the results of a deadlock freedom
 * check.
 * 
 * @see DeadlockCheckHandler
 * 
 * @author plagge
 */
public class DeadlockCheckFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;

	public DeadlockCheckFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	protected void showResult(final IComposableCommand cmd,
			final Animator animator) {
		final ConstraintBasedDeadlockCheckCommand command = (ConstraintBasedDeadlockCheckCommand) cmd;
		final ResultType result = command.getResult();
		final int dialogType;
		final String dialogTitle;
		final String message;
		if (result == null) {
			dialogType = MessageDialog.ERROR;
			dialogTitle = "Errow During Deadlock Freedom Check";
			message = "ProB did not return a result";
		} else {
			switch (result) {
			case NO_DEADLOCK:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "No Deadlock Found";
				message = "The model does not contain any deadlock.";
				break;
			case ERROR:
				dialogType = MessageDialog.ERROR;
				dialogTitle = "Errow During Deadlock Freedom Check";
				message = "An unexpected error occurred while typechecking the given predicate.";
				break;
			case DEADLOCK_FOUND:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "Deadlock Found";
				message = "The model contains a deadlock, it will be shown in the state view.";
				displayDeadlock(command, animator);
				break;
			case INTERRUPTED:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "User Interrupt";
				message = "The deadlock check has been interrupted by the user or a time-out.";
				break;
			default:
				Logger.notifyUser("Unexpected result: " + result);
				return;
			}
		}
		if (shell.isDisposed()) {
			System.out.println("Deadlock freedom check finished: "
					+ dialogTitle);
		} else {
			final Runnable runnable = new Runnable() {
				@Override
				public void run() {
					MessageDialog.open(dialogType, shell, dialogTitle, message,
							SWT.NONE);
				}
			};
			shell.getDisplay().asyncExec(runnable);
		}
	}

	private void displayDeadlock(final ConstraintBasedDeadlockCheckCommand cmd,
			final Animator animator) {
		final Operation operation = cmd.getDeadlockOperation();
		try {
			// we do not reset the history because we want to keep the root
			// state, we just start a new path from root
			animator.getHistory().gotoPos(0);
			ExecuteOperationCommand.executeOperation(animator, operation);
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
	}

}
