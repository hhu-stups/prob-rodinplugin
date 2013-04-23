/**
 * 
 */
package de.prob.ui.assertion;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.ConstraintBasedAssertionCheckCommand;
import de.prob.core.command.ConstraintBasedAssertionCheckCommand.ResultType;
import de.prob.core.command.IComposableCommand;
import de.prob.logging.Logger;
import de.prob.prolog.term.ListPrologTerm;

/**
 * This JobChangeAdapter presents the user the results of a deadlock freedom
 * check.
 * 
 * @see AssertionCheckHandler
 * 
 * @author plagge
 */
public class AssertionCheckFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;

	public AssertionCheckFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	protected void showResult(final IComposableCommand cmd,
			final Animator animator) {
		final ConstraintBasedAssertionCheckCommand command = (ConstraintBasedAssertionCheckCommand) cmd;
		final ResultType result = command.getResult();
		final int dialogType;
		final String dialogTitle;
		final String message;

		if (result == null) {
			dialogType = MessageDialog.ERROR;
			dialogTitle = "Errow During Constraint Based Check";
			message = "ProB did not return a result";
		} else {
			switch (result) {
			case NO_COUNTER_EXAMPLE:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "No Counter-Example Found";
				message = "No Counter-Example to the Context Theorems was found.";
				break;
			case COUNTER_EXAMPLE:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "COUNTER-EXAMPLE FOUND!";
				message = "The model contains a Counter-Example state, it will be shown in the state view.";
				displayCounterExample(command, animator);
				break;
			case TIMEOUT:
				dialogType = MessageDialog.WARNING;
				dialogTitle = " Interrupt";
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

	private void displayCounterExample(
			final ConstraintBasedAssertionCheckCommand command,
			final Animator animator) {
		final ListPrologTerm errorState = command.getCounterExampleState();
		// try {
		// animator.getHistory().reset();

		// ExecuteOperationCommand.executeOperation(animator, operation);
		// } catch (ProBException e) {
		// e.notifyUserOnce();
		// }
	}
}
