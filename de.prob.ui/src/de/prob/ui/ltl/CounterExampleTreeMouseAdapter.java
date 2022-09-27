package de.prob.ui.ltl;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;

import de.prob.core.Animator;
import de.prob.core.domainobjects.History;
import de.prob.core.domainobjects.ltl.CounterExample;
import de.prob.exceptions.ProBException;
import de.prob.logging.Logger;
import de.prob.ui.ltl.handler.CounterExampleHistoryHandler;

public final class CounterExampleTreeMouseAdapter extends MouseAdapter {
	private final CounterExampleTreeViewer treeViewer;
	private final CounterExample counterExample;

	public CounterExampleTreeMouseAdapter(CounterExampleTreeViewer treeViewer,
			CounterExample counterExample) {
		this.treeViewer = treeViewer;
		this.counterExample = counterExample;
	}

	@Override
	public void mouseDoubleClick(final MouseEvent event) {
		final ViewerCell cell = treeViewer.getCell(new Point(event.x, event.y));

		if (cell != null && cell.getColumnIndex() > 0) {
			int index = cell.getColumnIndex() - 1;
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
