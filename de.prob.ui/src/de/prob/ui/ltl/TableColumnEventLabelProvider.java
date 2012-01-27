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
public final class TableColumnEventLabelProvider extends CellLabelProvider {
	@Override
	public void update(final ViewerCell cell) {
		if (cell != null) {
			final CounterExampleState state = (CounterExampleState) cell
					.getElement();
			final Operation operation = state.getOperation();

			if (operation != null)
				cell.setText(operation.getName());
			else
				cell.setText("No operation");
		}
	}
}
