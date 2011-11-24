/**
 * 
 */
package de.prob.ui.ltl;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleState;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * @author plagge
 * 
 */
public final class PropositionColumnLabelProvider extends CellLabelProvider {
	final CounterExampleProposition proposition;

	public PropositionColumnLabelProvider(final CounterExampleProposition proposition) {
		super();
		this.proposition = proposition;
	}

	@Override
	public void update(final ViewerCell cell) {
		final CounterExampleState state = (CounterExampleState) cell
				.getElement();
		final int index = state.getIndex();
		final CounterExampleValueType value = proposition.getValues()
				.get(index);
		cell.setText(value.toString());
	}
}
