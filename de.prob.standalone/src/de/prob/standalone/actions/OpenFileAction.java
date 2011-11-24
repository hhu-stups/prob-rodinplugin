package de.prob.standalone.actions;

import java.io.File;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import de.prob.core.Animator;
import de.prob.core.command.ExploreStateCommand;
import de.prob.core.command.StartAnimationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.State;
import de.prob.exceptions.ProBException;

/**
 * Our sample action implements workbench action delegate. The action proxy will
 * be created by the workbench and shown in the UI. When the user tries to use
 * the action, this delegate will be created and execution will be delegated to
 * it.
 * 
 * @see IWorkbenchWindowActionDelegate
 */
public class OpenFileAction implements IWorkbenchWindowActionDelegate {
	private IWorkbenchWindow window;

	/**
	 * The constructor.
	 */
	public OpenFileAction() {
	}

	/**
	 * The action has been activated. The argument of the method represents the
	 * 'real' action sitting in the workbench UI.
	 * 
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(final IAction action) {
		FileDialog fileDialog = new FileDialog(window.getShell());
		String filename = fileDialog.open();
		File f = new File(filename);
		Animator.killAndLoad(f);
		Animator animator = Animator.getAnimator();
		try {
			// final String START_COMMAND = new PrologTermStringOutput()
			// .openTerm("start_animation").closeTerm().fullstop().toString();
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

	}

	/**
	 * Selection in the workbench has been changed. We can change the state of
	 * the 'real' action here if we want, but this can only happen after the
	 * delegate has been created.
	 * 
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(final IAction action,
			final ISelection selection) {
	}

	/**
	 * We can use this method to dispose of any system resources we previously
	 * allocated.
	 * 
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to be able to provide parent shell
	 * for the message dialog.
	 * 
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(final IWorkbenchWindow window) {
		this.window = window;
	}
}