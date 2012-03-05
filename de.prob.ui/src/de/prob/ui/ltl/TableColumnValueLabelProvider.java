/**
 * 
 */
package de.prob.ui.ltl;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleState;
import de.prob.core.domainobjects.ltl.CounterExampleValueType;

/**
 * @author plagge
 * 
 */
public final class TableColumnValueLabelProvider extends CellLabelProvider {
	final CounterExampleProposition proposition;
	private final Font normal = new Font(Display.getDefault(), "Arial", 10,
			SWT.NORMAL);
	private final Font bold = new Font(Display.getDefault(), "Arial", 10,
			SWT.BOLD);

	public TableColumnValueLabelProvider(
			final CounterExampleProposition proposition) {
		super();
		this.proposition = proposition;
	}

	@Override
	public void update(final ViewerCell cell) {
		if (cell != null) {
			final CounterExampleState state = (CounterExampleState) cell
					.getElement();
			final int index = state.getIndex();
			final CounterExampleValueType value = proposition.getValues().get(
					index);
			cell.setText(value.toString());

			final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage();
			final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
					.findView(CounterExampleViewPart.ID);

			if (counterExampleView != null) {
				final int currentIndex = counterExampleView.getCurrentIndex();

				if (index != currentIndex)
					cell.setFont(normal);
				else
					cell.setFont(bold);
			}
		}
	}
}
