/**
 * 
 */
package de.prob.ui.findvalidstate;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.FindValidStateCommand;
import de.prob.core.command.FindValidStateCommand.ResultType;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class FindValidStateFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;

	public FindValidStateFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	protected void showResult(final IComposableCommand cmd,
			final Animator animator) {
		final FindValidStateCommand command = (FindValidStateCommand) cmd;
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
			case NO_STATE_FOUND:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "No State Found";
				message = "No State satisfying both the Invariant and the Predicate was found.";
				break;
			case ERROR:
				dialogType = MessageDialog.ERROR;
				dialogTitle = "Errow During Search for Valid State";
				message = "An unexpected error occurred while typechecking the given predicate.";
				break;
			case STATE_FOUND:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "State found";
				message = "The model contains a state satisfying the given predicate and the invariant, it will be shown in the state view.";
				displayState(command, animator);
				break;
			case INTERRUPTED:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "User Interrupt";
				message = "The search has been interrupted by the user or a time-out.";
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

	private void displayState(final FindValidStateCommand cmd,
			final Animator animator) {
		final Operation operation = cmd.getOperation();
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
