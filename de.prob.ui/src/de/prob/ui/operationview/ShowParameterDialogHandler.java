package de.prob.ui.operationview;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class ShowParameterDialogHandler extends AbstractHandler implements
		IHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		final OperationTableViewer viewer = OperationTableViewer.getInstance();
		if (viewer == null) {
			// the Operations view is not open, e.g. run via Quick Access
			Logger.notifyUser("Please select an event in the Operations view.");
			return null;
		}
		List<Operation> s = viewer.getSelectedOperations();
		if (s.isEmpty()) {
			Logger.notifyUser("Please select an event in the Operations view.");
			return null;
		}

		Operation op = OperationSelectionDialog.getOperation(s);
		if (op == null)
			return null;
		try {
			ExecuteOperationCommand
					.executeOperation(Animator.getAnimator(), op);
		} catch (ProBException e) {
			e.notifyUserOnce();
		}
		return null;
	}
}
