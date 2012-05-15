package de.prob.ui.ltl.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.handlers.RadioState;

import de.prob.ui.ltl.CounterExampleViewPart;
import de.prob.ui.ltl.CounterExampleViewPart.ViewType;

/***
 * Provides a menu handler for a menu in a counter-example view
 * 
 * @author Andriy Tolstoy
 * 
 */

public final class CounterExampleViewMenuHandler extends AbstractHandler
		implements IHandler {

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event))
			return null;

		final String viewTypeAsString = event
				.getParameter(RadioState.PARAMETER_ID);
		final ViewType viewType = ViewType.valueOf(viewTypeAsString);
		HandlerUtil.updateRadioState(event.getCommand(), viewTypeAsString);

		final CounterExampleViewPart counterExampleView = CounterExampleViewPart
				.getDefault();
		if (counterExampleView != null)
			counterExampleView.setViewType(viewType);

		return null;
	}
}
