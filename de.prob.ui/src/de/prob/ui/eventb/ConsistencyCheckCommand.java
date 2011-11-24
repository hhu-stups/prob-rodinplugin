package de.prob.ui.eventb;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.prob.core.Animator;
import de.prob.ui.PerspectiveFactory;
import de.prob.ui.ProbUiPlugin;

public class ConsistencyCheckCommand extends AbstractHandler implements
		IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		PerspectiveFactory.openPerspective();

		final Animator animator = Animator.getAnimator();
		final Shell shell = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell();
		if (!animator.isRunning()) {
			ErrorDialog.openError(shell, "Error",
					"Please start an animation first", new Status(
							IStatus.ERROR, ProbUiPlugin.PLUGIN_ID,
							"Please start an animation first", null));
			return null;
		}

		ConsistencyCheckingDialog md = new ConsistencyCheckingDialog(shell);

		md.setBlockOnOpen(true);

		md.open();
		return null;
	}

}
