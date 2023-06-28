package de.prob.ui.ltl;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;

/***
 * Provides a label provider for a counter-example proposition column
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class TreeColumnPropositionLabelProvider extends CellLabelProvider {
	@Override
	public void update(ViewerCell cell) {
		if (cell != null) {
			final CounterExampleProposition proposition = (CounterExampleProposition) cell
					.getElement();

			if (proposition != null) {
				cell.setText(proposition.toString());
			}
		}
	}
}
