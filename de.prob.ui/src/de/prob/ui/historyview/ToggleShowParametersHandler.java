/**
 * 
 */
package de.prob.ui.historyview;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.State;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.services.IServiceLocator;

import de.prob.ui.stateview.StateViewPart;

/**
 * @author plagge
 * 
 */
public class ToggleShowParametersHandler extends AbstractHandler implements
		IHandler, IElementUpdater {
	private static final String COMMAND_ID = "de.prob.ui.history.toggleShowParameters";
	private static final String STATE_ID = "de.prob.ui.history.showParametersState";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final State state = event.getCommand().getState(STATE_ID);
		if (state != null) {
			final boolean newFilterValue = toggleState(state);
			setShowParameters(newFilterValue);
		} else
			throw new ExecutionException("Command state " + STATE_ID
					+ " not found");
		return null;
	}

	private boolean toggleState(final State state) {
		final Boolean show = (Boolean) state.getValue();
		state.setValue(!show);
		return !show;
	}

	private void setShowParameters(final boolean show)
			throws ExecutionException {
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		final HistoryView view = (HistoryView) page
				.findView(HistoryView.VIEW_ID);
		if (view != null) {
			view.setShowParameters(show);
		} else
			throw new ExecutionException("View " + StateViewPart.STATE_VIEW_ID
					+ " not found");
	}

	@Override
	public void updateElement(UIElement element,
			@SuppressWarnings("rawtypes") Map parameters) {
		final State state = getCurrentState(element.getServiceLocator());
		final Boolean filterSet = (Boolean) state.getValue();
		element.setChecked(filterSet);
	}

	private static State getCurrentState(final IServiceLocator locator) {
		final ICommandService service = (ICommandService) locator
				.getService(ICommandService.class);
		final Command command = service.getCommand(COMMAND_ID);
		final State state = command.getState(STATE_ID);
		return state;
	}

}
