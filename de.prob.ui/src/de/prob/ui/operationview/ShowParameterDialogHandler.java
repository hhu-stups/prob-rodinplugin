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

public class ShowParameterDialogHandler extends AbstractHandler implements
		IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {

		List<Operation> s = OperationTableViewer.getInstance()
				.getSelectedOperations();

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
