package de.prob.ui.ltl;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TableItem;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ltl.handler.CounterExampleHistoryHandler;

public final class CounterExampleTableMouseAdapter extends MouseAdapter {
	private final CounterExampleTableViewer tableViewer;
	private final CounterExample counterExample;

	public CounterExampleTableMouseAdapter(
			CounterExampleTableViewer tableViewer, CounterExample counterExample) {
		this.tableViewer = tableViewer;
		this.counterExample = counterExample;
	}

	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		final ViewerCell cell = tableViewer
				.getCell(new Point(event.x, event.y));

		if (cell != null) {
			List<TableItem> items = Arrays.asList(tableViewer.getTable()
					.getItems());
			TableItem item = (TableItem) cell.getItem();
			int index = items.indexOf(item);
			index += counterExample.getInitPath().size();

			final Animator animator = Animator.getAnimator();
			final History history = animator.getHistory();

			try {
				CounterExampleHistoryHandler.showCounterExampleInAnimator();
				history.gotoPos(index);
			} catch (final ExecutionException e) {
				Logger.notifyUser("Internal Error. Please submit a bugreport",
						e);
			} catch (final ProBException e) {
				Logger.notifyUser("Internal Error. Please submit a bugreport",
						e);
			}
		}
	}
}
