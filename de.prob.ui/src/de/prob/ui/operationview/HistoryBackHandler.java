package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class HistoryBackHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		History history = Animator.getAnimator().getHistory();
		Logger.assertProB("history != null", history != null);
		int pos = history.getCurrentPosition() - 1;
		try {
			pos = Integer.parseInt(event.getParameter("de.prob.ui.history.pos"));
		} catch (NumberFormatException e) {
			// one step back
		}
		if (pos >= 0)
			try {
				history.gotoPos(pos);
			} catch (ProBException e) {
				Logger.notifyUser("Internal Error. Please submit a bugreport", e);
			}
		return null;
	}
}
