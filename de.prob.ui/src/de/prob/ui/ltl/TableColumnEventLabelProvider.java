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

			if (operation != null)
				cell.setText(operation.getName());
			else
				cell.setText("No operation");

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
