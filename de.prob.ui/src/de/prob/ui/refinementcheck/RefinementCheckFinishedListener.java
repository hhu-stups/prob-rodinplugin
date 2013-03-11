/**
 * 
 */
package de.prob.ui.refinementcheck;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

import de.prob.core.Animator;
import de.prob.core.ProBJobFinishedListener;
import de.prob.core.command.ConstraintBasedRefinementCheckCommand;
import de.prob.core.command.ConstraintBasedRefinementCheckCommand.RefinementCheckCounterExample;
import de.prob.core.command.ConstraintBasedRefinementCheckCommand.ResultType;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.command.IComposableCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

/**
 * 
 * @author krings
 * 
 */
public class RefinementCheckFinishedListener extends ProBJobFinishedListener {
	private final Shell shell;

	public RefinementCheckFinishedListener(final Shell shell) {
		this.shell = shell;
	}

	@Override
	protected void showResult(final IComposableCommand command,
			final Animator animator) {
		final ConstraintBasedRefinementCheckCommand refCmd = (ConstraintBasedRefinementCheckCommand) command;
		final ResultType result = refCmd.getResult();
		final int dialogType;
		final String dialogTitle;
		final String message;

		switch (result) {
		case INTERRUPTED:
			dialogType = MessageDialog.WARNING;
			dialogTitle = "User Interrupt";
			message = "The refinement check has been interrupted by the user.";
			break;
		case NO_VIOLATION_FOUND:
			dialogType = MessageDialog.INFORMATION;
			dialogTitle = "No Refinement Violation found";
			message = "No possible refinement violation has been found.";
			break;
		case VIOLATION_FOUND:
			dialogType = MessageDialog.ERROR;
			dialogTitle = "Refinement Violation found";
			message = "A refinement violation has been found.\nThe first found example will be shown in the history.";
			displayViolation(refCmd, animator);
			break;
		default:
			Logger.notifyUser("Unexpected result: " + result);
			return;
		}
		if (shell.isDisposed()) {
			System.out.println("Refinement Check finished: " + dialogTitle);
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

	private void displayViolation(
			final ConstraintBasedRefinementCheckCommand cmd,
			final Animator animator) {
		final RefinementCheckCounterExample example = cmd.getCounterExamples()
				.iterator().next();
		final Operation step1 = example.getStep1();
		final Operation step2 = example.getStep2();
		try {
			// we do not reset the history because we want to keep the root
			// state, we just start a new path from root
			animator.getHistory().gotoPos(0);
			ExecuteOperationCommand.executeOperation(animator, step1);
			ExecuteOperationCommand.executeOperation(animator, step2);
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
	}

}
