/**
 * 
 */
package de.prob.ui.ltl.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.core.Animator;
import de.prob.core.command.SetTraceCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.exceptions.ProBException;
import de.prob.ui.ltl.CounterExampleViewPart;

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
		final CounterExampleViewPart view = CounterExampleViewPart.getDefault();
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

}
