/**
 * 
 */
package de.prob.ui.findvalidstate;

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
import de.prob.core.command.FindValidStateCommand;
import de.prob.logging.Logger;
import de.prob.parserbase.ProBParserBaseAdapter;
import de.prob.prolog.term.PrologTerm;
import de.prob.ui.validators.PredicateValidator;

/**
 * This handler provides a simple dialog to ask for an optional predicate to
 * check for deadlocks in the model.
 * 
 * @author krings
 */
public class FindValidStateHandler extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Shell shell = HandlerUtil.getActiveShell(event);
		if (Animator.getAnimator().isMachineLoaded()) {
			findValidState(shell);
		} else {
			Logger.notifyUser("No ProB animation running. This is a bug. Please submit a report. Error in declaraion for class DeadlockCheckHandler");
		}
		return null;
	}

	private void findValidState(final Shell shell) throws ExecutionException {
		final Animator animator = Animator.getAnimator();
		final LanguageDependendAnimationPart ldp = animator
				.getLanguageDependendPart();
		final IInputValidator validator = new PredicateValidator(ldp);
		final InputDialog dialog = new InputDialog(
				shell,
				"Find Valid State Freedom Check",
				"ProB will try to find a state satisfying the invariant and the predicate:",
				"1=1", validator);
		final int status = dialog.open();
		if (status == InputDialog.OK) {
			startFindState(animator, ldp, dialog.getValue(), shell);
		}
	}

	private void startFindState(final Animator animator,
			final LanguageDependendAnimationPart ldp, final String value,
			final Shell shell) throws ExecutionException {
		final PrologTerm predicate = parsePredicate(ldp, value);
		final FindValidStateCommand command = new FindValidStateCommand(
				predicate);
		final Job job = new ProBCommandJob(
				"Searching for State satisfying Predicate", animator, command);
		job.setUser(true);
		job.addJobChangeListener(new FindValidStateFinishedListener(shell));
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
