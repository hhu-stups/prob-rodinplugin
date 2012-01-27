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

/***
 * Provides a label provider for a counter-example value column
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class TreeColumnValueLabelProvider extends CellLabelProvider {
	private final CounterExampleState state;

	public TreeColumnValueLabelProvider(CounterExampleState state) {
		super();
		this.state = state;
	}

	@Override
	public void update(ViewerCell cell) {
		if (cell != null) {
			final CounterExampleProposition proposition = (CounterExampleProposition) cell
					.getElement();

			if (proposition != null) {
				final int index = state.getIndex();

				final CounterExampleValueType value = proposition.getValues()
						.get(index);
				cell.setText(value.toString());

				final IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				final CounterExampleViewPart counterExampleView = (CounterExampleViewPart) workbenchPage
						.findView(CounterExampleViewPart.ID);

				if (counterExampleView != null) {
					final int currentIndex = counterExampleView
							.getCurrentIndex();

					int font = SWT.NORMAL;

					if (index == currentIndex) {
						font = SWT.BOLD;
					}

					Font currentFont = new Font(Display.getDefault(), "Arial",
							10, font);

					cell.setFont(currentFont);
				}
			}
		}
	}
}
