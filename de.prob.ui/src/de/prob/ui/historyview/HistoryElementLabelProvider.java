/**
 * 
 */
package de.prob.ui.historyview;

import org.eclipse.swt.graphics.Color;

import de.prob.ui.historyview.HistoryView.HistViewItem;
import de.prob.ui.stateview.StateLabelProvider;
import de.prob.ui.stateview.statetree.StaticStateElement;

/**
 * Label provider for the columns of the history view that contain StateElements
 * (like in the state view). To be consistent with the state view, the class
 * acts as a delegate to a {@link StateLabelProvider} object.
 * 
 * @author plagge
 */
public class HistoryElementLabelProvider extends HistoryLabelProvider {
	private final StateLabelProvider slProvider = new StateLabelProvider();
	private final StaticStateElement sse;

	public HistoryElementLabelProvider(final StaticStateElement sse) {
		super();
		this.sse = sse;
	}

	@Override
	protected String getText(final HistViewItem item) {
		return slProvider.getText(item.getDestination(), sse);
	}

	@Override
	protected Color getForeground(final HistViewItem item) {
		// set changed to false, as we do not have the state here
		return slProvider.getForeground(item.getDestination(), false, sse);
	}
}
