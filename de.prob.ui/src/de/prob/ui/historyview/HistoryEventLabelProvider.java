package de.prob.ui.historyview;

import java.util.Collection;
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
				if (eventStackPosition < stack.size()) {
					EventStackElement stackElem = stack.get(eventStackPosition);
					result = printEventWithParameters(stackElem.getEventName(),
							stackElem.getParameters());
				} else {
					result = null;
				}
			} else {
				result = eventStackPosition == 0 ? printEventWithParameters(
						operation.getName(), operation.getArguments()) : null;
			}
		} else {
			result = eventStackPosition == 0 ? HistoryViewStrings.uninitialisedState
					: null;
		}
		return result == null ? "" : result;
	}

	private String printEventWithParameters(final String name,
			final Collection<String> parameters) {
		final String result;
		if (!showParameters || parameters.isEmpty()) {
			result = name;
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append(name);
			sb.append('(');
			boolean isFirst = true;
			for (final String param : parameters) {
				if (!isFirst) {
					sb.append(',');
				}
				sb.append(param);
				isFirst = false;
			}
			sb.append(')');
			result = sb.toString();
		}
		return result;
	}

}