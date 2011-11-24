package de.prob.ui.operationview;

import org.eclipse.core.commands.*;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowOnlyEnabledOpsHandler extends AbstractHandler implements
		IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean showOnlyEnabledStates = !HandlerUtil
				.toggleCommandState(command);
		// use the old value and perform the operation

		OperationTableViewer otv = OperationTableViewer.getInstance();
		if (otv == null)
			return null;
		if (showOnlyEnabledStates) {
			otv.applyFilter();
		} else {
			otv.unapplyFilter();
		}

		// needed to fix "lazy" TableEditor
		// OperationTableViewer.getInstance().getViewer().getTable().select(0);

		return null;
	}
}
