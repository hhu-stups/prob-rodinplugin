/**
 * 
 */
package de.prob.ui.ltl;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExampleState;

/**
 * @author plagge
 * 
 */
public final class EventColumnLabelProvider extends CellLabelProvider {
	@Override
	public void update(final ViewerCell cell) {
		final CounterExampleState state = (CounterExampleState) cell
				.getElement();
		final Operation operation = state.getOperation();

		if (operation != null)
			cell.setText(operation.getName());
	}
}
