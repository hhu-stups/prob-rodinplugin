package de.prob.ui.ltl;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

/***
 * Provides a menu handler for a menu in a counter-example view
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleViewMenuHandler extends AbstractHandler
		implements IHandler {

	private static String currentViewType = "Table";

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event))
			return null;

		currentViewType = event.getParameter(RadioState.PARAMETER_ID);
		HandlerUtil.updateRadioState(event.getCommand(), currentViewType);

		CounterExampleView.setViewType(currentViewType);
		return null;
	}

	public static String getCurrentViewType() {
		return currentViewType;
	}
}
