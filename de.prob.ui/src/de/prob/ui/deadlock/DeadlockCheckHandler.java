/**
 * 
 */
package de.prob.ui.deadlock;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.LanguageDependendAnimationPart;
import de.prob.core.ProBCommandJob;
import de.prob.core.command.ConstraintBasedDeadlockCheckCommand;
import de.prob.logging.Logger;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.validators.PredicateValidator;

/**
 * This handler provides a simple dialog to ask for an optional predicate to
 * check for deadlocks in the model.
 * 
 * @author plagge
 */
public class DeadlockCheckHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (Animator.getAnimator().isMachineLoaded()) {
			performDeadlockCheck(shell);
		} else {
			Logger.notifyUser("No ProB animation running. This is a bug. Please submit a report. Error in declaraion for class DeadlockCheckHandler");
		}
		return null;
	}

	private void performDeadlockCheck(final Shell shell)
			throws ExecutionException {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		final IInputValidator validator = new PredicateValidator(ldp);
		final InputDialog dialog = new InputDialog(
				shell,
				"Deadlock Freedom Check",
				"ProB will search for a deadlocking state satisfying the invariant. You can (optionally) specify a predicate to constrain the search:",
				"", validator);
		final int status = dialog.open();
		if (status == InputDialog.OK) {
			startCheck(animator, ldp, dialog.getValue(), shell);
		}
	}

	private void startCheck(final Animator animator,
			final LanguageDependendAnimationPart ldp, final String value,
			final Shell shell) throws ExecutionException {
		final PrologTerm predicate = parsePredicate(ldp, value);
		final ConstraintBasedDeadlockCheckCommand command = new ConstraintBasedDeadlockCheckCommand(
				predicate);
		final Job job = new ProBCommandJob("Checking for Deadlock Freedom",
				animator, command);
		job.setUser(true);
		job.addJobChangeListener(new DeadlockCheckFinishedListener(shell));
		job.schedule();
	}

	private PrologTerm parsePredicate(final LanguageDependendAnimationPart ldp,
			final String input) throws ExecutionException {
		final PrologTerm predicate;
		if (input != null && input.trim().isEmpty()) {
			predicate = null;
		} else {
			try {
				final ProBParserBaseAdapter parser = new ProBParserBaseAdapter(
						ldp);
				predicate = parser.parsePredicate(input, false);
			} catch (Exception e) {
				throw (new ExecutionException(
						"Exception while parsing the input", e));
			}
		}
		return predicate;
	}
}
