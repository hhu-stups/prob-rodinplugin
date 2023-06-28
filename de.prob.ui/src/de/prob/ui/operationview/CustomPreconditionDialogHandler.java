package de.prob.ui.operationview;

import java.util.*;

import org.eclipse.core.commands.*;

import de.prob.core.Animator;
import de.prob.core.command.ExecuteOperationCommand;
import de.prob.core.domainobjects.Operation;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;

public class CustomPreconditionDialogHandler extends AbstractHandler implements
		IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {

		List<Operation> s = OperationTableViewer.getInstance()
				.getSelectedOperations();
		List<Operation> filtered = new ArrayList<Operation>();
		List<String> seenNames = new ArrayList<String>();

		for (Operation op : s) {
			if (!seenNames.contains(op.getName())) {
				seenNames.add(op.getName());
				filtered.add(op);
			}
		}

		if (filtered.size() != 1) {
			Logger.notifyUser(filtered.size()
					+ " events selected. An additional Guard Constraint can only be added to a single event");
			return null;
		}

		Operation op = CustomPreconditionInputDialog.getOperation(filtered
				.get(0));
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
