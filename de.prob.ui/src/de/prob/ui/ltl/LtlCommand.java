package de.prob.ui.ltl;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.services.ISourceProviderService;

import de.prob.core.Animator;
import de.prob.ui.PerspectiveFactory;
import de.prob.ui.ProbUiPlugin;
import de.prob.ui.services.ModelLoadedProvider;

public final class LtlCommand extends AbstractHandler implements IHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		IWorkbenchWindow window = HandlerUtil
				.getActiveWorkbenchWindowChecked(event);
		final Shell shell = window.getShell();

		if (!isEnabled(window)) { // prevent from being called while no
									// animation is running
			ErrorDialog.openError(shell, "Error",
					"Please start an animation first", new Status(
							IStatus.ERROR, ProbUiPlugin.PLUGIN_ID,
							"Please start an animation first", null));
			return null;
		}

		PerspectiveFactory.openPerspective();

		final Animator animator = Animator.getAnimator();

		if (!animator.isRunning())
			return null;

		LtlCheckingDialog md = new LtlCheckingDialog(shell);

		md.setBlockOnOpen(true);

		md.open();

		return null;
	}

	private boolean isEnabled(final IWorkbenchWindow window) {
		ISourceProviderService service = (ISourceProviderService) window
				.getService(ISourceProviderService.class);
		// get our source provider by querying by the variable name
		ModelLoadedProvider sourceProvider = (ModelLoadedProvider) service
				.getSourceProvider(ModelLoadedProvider.SERVICE);
		return sourceProvider.isEnabled();
	}

}
