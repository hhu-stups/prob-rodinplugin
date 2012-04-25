/**
 * 
 */
package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import de.prob.core.Animator;
import de.prob.core.command.SetTraceCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.exceptions.ProBException;
import de.prob.ui.ProbUiPlugin;

/**
 * This handler is used to take the current counter-example and fill it into the
 * history.
 * 
 * @author plagge
 */
public final class CounterExampleHistoryHandler extends AbstractHandler
		implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		showCounterExampleInAnimator();

		return null;
	}

	public static void showCounterExampleInAnimator() throws ExecutionException {
		final IWorkbenchWindow window = ProbUiPlugin.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		final CounterExampleViewPart view = findCEView(window);

		if (view != null) {
			final CounterExample ce = view.getCurrentCounterExample();

			if (ce != null) {
				try {

					final List<Operation> fullPath = ce.getFullPath();
					final SetTraceCommand cmd = new SetTraceCommand(fullPath);
					final Animator animator = Animator.getAnimator();
					animator.execute(cmd);
					cmd.setTraceInHistory(animator, fullPath.size());
				} catch (ProBException e) {
					e.notifyUserOnce();
					throw new ExecutionException(
							"ProB setTrace command failed", e);
				}
			}
		}
	}

	private static CounterExampleViewPart findCEView(
			final IWorkbenchWindow window) {
		final IWorkbenchPage page = window.getActivePage();
		IViewPart view = page.findView(CounterExampleViewPart.ID);
		if (view == null) {
			MessageDialog.openError(window.getShell(), "Internal Error",
					"Cannot find the History View");
			return null;
		} else {
			if (view instanceof CounterExampleViewPart)
				return (CounterExampleViewPart) view;
			else {
				MessageDialog.openError(window.getShell(), "Internal Error",
						"Not expected type of the Counter Example View: "
								+ view.getClass().getName());
				return null;
			}
		}
	}
}
