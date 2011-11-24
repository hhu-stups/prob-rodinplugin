package de.prob.ui.operationview;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

public class DoubleClickBehaviorHandler extends AbstractHandler implements
		IHandler {

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event))
			return null; // we are already in the updated state - do nothing
		String currentState = event.getParameter(RadioState.PARAMETER_ID);

		OperationTableViewer.setDoubleClickBehavior(currentState);

		HandlerUtil.updateRadioState(event.getCommand(), currentState);
		return null;
	}
}
