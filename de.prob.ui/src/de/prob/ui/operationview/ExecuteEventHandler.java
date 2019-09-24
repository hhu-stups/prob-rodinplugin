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

public class ExecuteEventHandler extends AbstractHandler implements IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		long id = -1;
		try {
			id = Long.parseLong(event.getParameter("de.prob.ui.operation"));
		} catch (NumberFormatException e) {
			Logger.notifyUser("Internal Error. Please submit a bugreport", e);
		}
		Logger.assertProB("id >= 0", id >= 0);

		List<Operation> operations = OperationTableViewer.getInstance()
				.getSelectedOperations();

		if (operations.isEmpty())
			return null;

		for (Operation operation : operations) {
			long cid = operation.getId();
			if (cid == id) {
				try {
					ExecuteOperationCommand.executeOperation(
							Animator.getAnimator(), operation);
				} catch (ProBException e) {
					e.notifyUserOnce();
					throw new ExecutionException(
							"executing the event failed", e);
				}
			}
		}

		return null;
	}
}
