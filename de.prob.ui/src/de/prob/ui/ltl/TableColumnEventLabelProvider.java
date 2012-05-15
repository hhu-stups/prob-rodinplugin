/**
 * 
 */
package de.prob.ui.ltl;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;

import de.prob.core.domainobjects.Operation;
import de.prob.core.domainobjects.ltl.CounterExampleState;

/**
 * @author plagge
 * 
 */
public final class TableColumnEventLabelProvider extends CellLabelProvider {
	private final Font normal = new Font(Display.getDefault(), "Arial", 10,
			SWT.NORMAL);
	private final Font bold = new Font(Display.getDefault(), "Arial", 10,
			SWT.BOLD);

	@Override
	public void update(final ViewerCell cell) {
		if (cell != null) {
			final CounterExampleState state = (CounterExampleState) cell
					.getElement();
			final Operation operation = state.getOperation();
			final int index = state.getIndex();

			final String opText = operation != null ? operation.getName()
					: "No operation";

			final CounterExampleViewPart counterExampleView = CounterExampleViewPart
					.getDefault();
			final Font font;
			if (counterExampleView != null) {
				final int currentIndex = counterExampleView.getCurrentIndex();
				font = index == currentIndex ? bold : normal;
			} else {
				font = normal;
			}

			cell.setText(opText);
			cell.setFont(font);
		}
	}
}
