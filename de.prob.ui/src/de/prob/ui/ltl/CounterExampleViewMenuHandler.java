package de.prob.ui.ltl;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
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

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event))
			return null;

		String currentViewType = event.getParameter(RadioState.PARAMETER_ID);
		HandlerUtil.updateRadioState(event.getCommand(), currentViewType);

		final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();

		final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
				.findView(CounterExampleViewPart.ID);

		if (counterExampleView != null)
			counterExampleView.setViewType(currentViewType);

		return null;
	}
}
