package de.prob.ui.historyview;

import java.util.List;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.Operation.EventStackElement;
import de.prob.ui.historyview.HistoryView.HistViewItem;

/**
 * Label provider for the columns of the history view that contain the names of
 * executed events.
 * 
 * @author plagge
 */
class HistoryEventLabelProvider extends HistoryLabelProvider {
	private final int eventStackPosition;

	public HistoryEventLabelProvider(final int eventStackPosition) {
		super();
		this.eventStackPosition = eventStackPosition;
	}

	@Override
	protected String getText(final HistViewItem item) {
		final String result;
		final Operation operation = item.getOperation();
		if (operation != null) {
			List<EventStackElement> stack = operation.getEventStack();
			if (stack != null) {
				result = eventStackPosition < stack.size() ? stack.get(
						eventStackPosition).getEventName() : null;
			} else {
				result = eventStackPosition == 0 ? operation.getName() : null;
			}
		} else {
			result = eventStackPosition == 0 ? HistoryViewStrings.uninitialisedState
					: null;
		}
		return result == null ? "" : result;
	}

}