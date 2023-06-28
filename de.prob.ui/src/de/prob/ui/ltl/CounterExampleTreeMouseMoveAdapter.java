package de.prob.ui.ltl;

import java.util.List;

import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Point;

import de.prob.core.command.LtlCheckingCommand.PathType;
import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

public final class CounterExampleTreeMouseMoveAdapter implements
		MouseMoveListener {
	private final CounterExampleTreeViewer treeViewer;
	private ViewerCell currentCell;

	public CounterExampleTreeMouseMoveAdapter(
			CounterExampleTreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	@Override
	public void mouseMove(MouseEvent e) {
		ViewerCell cell = treeViewer.getCell(new Point(e.x, e.y));

		if (cell != null && !cell.equals(currentCell)) {
			currentCell = cell;
			CounterExampleProposition element = (CounterExampleProposition) cell
					.getElement();
			PathType pathType = element.getPathType();
			int loopEntry = element.getLoopEntry();
			String toolTip = "PathType: " + pathType + ", loopEntry = "
					+ loopEntry;

			int columnIndex = cell.getColumnIndex();

			if (cell.getColumnIndex() > 0) {
				if (element instanceof CounterExampleUnaryOperator) {
					CounterExampleUnaryOperator unary = (CounterExampleUnaryOperator) element;
					List<List<Integer>> allPositions = unary
							.getHighlightedPositions();

					if (columnIndex - 1 < allPositions.size()) {
						List<Integer> positions = allPositions
								.get(columnIndex - 1);
						toolTip += ". Positions: " + positions;
					}
				} else if (element instanceof CounterExampleBinaryOperator) {
					CounterExampleBinaryOperator binary = (CounterExampleBinaryOperator) element;

					List<List<Integer>> firstAllPositions = binary
							.getFirstHighlightedPositions();

					if (columnIndex - 1 < firstAllPositions.size()) {
						List<Integer> firstPositions = binary
								.getFirstHighlightedPositions().get(
										columnIndex - 1);
						List<Integer> secondPositions = binary
								.getSecondHighlightedPositions().get(
										columnIndex - 1);

						toolTip += ". FirstPositions: " + firstPositions;
						toolTip += ", SecondPositions: " + secondPositions;
					}
				}
			}

			cell.getControl().setToolTipText(toolTip);
			System.out.println(element);
		}
	}
}
