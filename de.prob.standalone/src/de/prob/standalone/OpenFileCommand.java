package de.prob.standalone;
import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import de.prob.core.Animator;
import de.prob.core.command.ExploreStateCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

public class OpenFileCommand extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);

		FileDialog fileDialog = new FileDialog(window.getShell());
		String filename = fileDialog.open();
		if (filename == null)
			return null;
		File f = new File(filename);
		Animator.killAndLoad(f);
		Animator animator = Animator.getAnimator();
		try {
			// final String START_COMMAND = new
			// PrologTermStringOutput().openTerm(
			// "start_animation").closeTerm().fullstop().toString();
			// PrologTermGenerator.toPrologTermMustNotFail(START_COMMAND,
			// animator
			// .sendCommand(START_COMMAND));

			StartAnimationCommand.start(animator);

			animator.announceReset();

			State commandResult = ExploreStateCommand.exploreState(animator,
					"root");

			animator.announceCurrentStateChanged(commandResult,
					Operation.NULL_OPERATION);

		} catch (ProBException e) {
			e.printStackTrace();
		}

		return null;
	}

}
