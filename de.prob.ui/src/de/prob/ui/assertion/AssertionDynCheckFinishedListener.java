/**
 * 
 */
package de.prob.ui.assertion;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.ConstraintBasedDynamicAssertionCheckCommand;
import de.prob.core.command.ConstraintBasedDynamicAssertionCheckCommand.ResultType;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * This JobChangeAdapter presents the user the results of a assertion
 * check.
 * 
 * @see AssertionCheckHandler
 * 
 * @author plagge
 */
public class AssertionDynCheckFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;

	public AssertionDynCheckFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	protected void showResult(final IComposableCommand cmd,
			final Animator animator) {
		final ConstraintBasedDynamicAssertionCheckCommand command = (ConstraintBasedDynamicAssertionCheckCommand) cmd;
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
			case NO_COUNTER_EXAMPLE_EXISTS:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "No Counter-Example Exists";
				message = "No counter-example to the invariant theorems exists (for current settings of deferred sets).\nIf your model contains deferred sets, there may exist counter-examples for other sizes of these sets.";
				break;
			case NO_COUNTER_EXAMPLE_FOUND:
				dialogType = MessageDialog.INFORMATION;
				dialogTitle = "No Counter-Example Found";
				message = "No counter-example to the invariant theorems was found (but one may exist).";
				break;
			case COUNTER_EXAMPLE:
				dialogType = MessageDialog.WARNING;
				dialogTitle = "COUNTER-EXAMPLE FOUND!";
				message = "A counter-example to the invariant theorems was found: it will be shown in the state view.\nThis counter-example state satisfies the invariants but not all theorems. It may not be reachable from the initialisation.";
				displayCounterExample(command, animator);
				break;
			case INTERRUPTED:
				dialogType = MessageDialog.WARNING;
				dialogTitle = " Interrupt";
				message = "The assertion check has been interrupted by the user or a time-out.";
				break;
			default:
				Logger.notifyUser("Unexpected result: " + result);
				return;
			}
		}
		if (shell.isDisposed()) {
			System.out.println("Invariant Theorems check finished: "
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
			final ConstraintBasedDynamicAssertionCheckCommand command,
			final Animator animator) {
		final Operation operation = command.getCounterExampleOperation();
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
