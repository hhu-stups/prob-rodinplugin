/**
 * 
 */
package de.prob.ui.stateview;

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

/**
 * This handler controls the behavior of the filter for duplicate variables in
 * the state view.
 * 
 * @author plagge
 */
public class ToggleShowDuplicatesHandler extends AbstractHandler implements
		IHandler, IElementUpdater {
	private static final String COMMAND_ID = "de.prob.ui.stateview.toggleShowDuplicates";
	private static final String STATE_ID = "de.prob.ui.stateview.duplicateFilterState";

	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final State state = event.getCommand().getState(STATE_ID);
		if (state != null) {
			final boolean newFilterValue = toggleState(state);
			setFilter(newFilterValue);
		} else
			throw new ExecutionException("Command state " + STATE_ID
					+ " not found");
		return null;
	}

	private void setFilter(final boolean newFilterValue)
			throws ExecutionException {
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		final StateViewPart view = (StateViewPart) page
				.findView(StateViewPart.STATE_VIEW_ID);
		if (view != null) {
			view.setDuplicateVariableFilter(newFilterValue);
		} else
			throw new ExecutionException("View " + StateViewPart.STATE_VIEW_ID
					+ " not found");
	}

	private boolean toggleState(final State state) {
		final Boolean filterSet = (Boolean) state.getValue();
		state.setValue(!filterSet);
		return !filterSet;
	}

	public void updateElement(final UIElement element,
			@SuppressWarnings("rawtypes") final Map parameters) {
		final State state = getCurrentState(element.getServiceLocator());
		final Boolean filterSet = (Boolean) state.getValue();
		element.setChecked(filterSet);
	}

	public static State getCurrentState(final IServiceLocator locator) {
		final ICommandService service = (ICommandService) locator
				.getService(ICommandService.class);
		final Command command = service.getCommand(COMMAND_ID);
		final State state = command.getState(STATE_ID);
		return state;
	}
}
