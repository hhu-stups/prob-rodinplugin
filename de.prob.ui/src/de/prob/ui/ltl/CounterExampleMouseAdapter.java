package de.prob.ui.ltl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerRow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

import de.prob.core.domainobjects.ltl.CounterExampleBinaryOperator;
import de.prob.core.domainobjects.ltl.CounterExampleProposition;
import de.prob.core.domainobjects.ltl.CounterExampleUnaryOperator;

public final class CounterExampleMouseAdapter extends MouseAdapter {
	private final List<ViewerCell> coloredCells = new ArrayList<ViewerCell>();
	private final CounterExampleTreeViewer treeViewer;

	public CounterExampleMouseAdapter(CounterExampleTreeViewer treeViewer) {
		this.treeViewer = treeViewer;
	}

	@Override
	public void mouseDown(MouseEvent e) {
		for (ViewerCell cell : coloredCells) {
			cell.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_WHITE));
		}

		coloredCells.clear();

		ViewerCell cell = treeViewer.getCell(new Point(e.x, e.y));

		if (cell != null && cell.getColumnIndex() > 0) {
			cell.setBackground(Display.getDefault().getSystemColor(
					SWT.COLOR_YELLOW));
			coloredCells.add(cell);

			Object element = cell.getElement();

			System.out.println("Parent element: " + element);
			int columnIndex = cell.getColumnIndex();

			ViewerRow row = cell.getViewerRow();

			if (element instanceof CounterExampleUnaryOperator) {
				CounterExampleUnaryOperator unary = (CounterExampleUnaryOperator) element;
				CounterExampleProposition first = unary.getArgument();
				ViewerRow firstRow = getNeighborRow(row, first);

				if (firstRow != null) {
					List<List<Integer>> allPositions = unary
							.getHighlightedPositions();

					if (columnIndex - 1 < allPositions.size()) {
						List<Integer> positions = allPositions
								.get(columnIndex - 1);
						markArgument(firstRow, positions);
					}
				}
			} else if (element instanceof CounterExampleBinaryOperator) {
				CounterExampleBinaryOperator binary = (CounterExampleBinaryOperator) element;
				CounterExampleProposition first = binary.getFirstArgument();
				CounterExampleProposition second = binary.getSecondArgument();

				ViewerRow firstRow = getNeighborRow(row, first);
				ViewerRow secondRow = getNeighborRow(row, second);

				if (firstRow != null && secondRow != null) {
					List<List<Integer>> firstAllPositions = binary
							.getFirstHighlightedPositions();
					List<List<Integer>> secondAllPositions = binary
							.getSecondHighlightedPositions();

					Assert.isTrue(firstAllPositions.size() == secondAllPositions
							.size());

					if (columnIndex - 1 < firstAllPositions.size()) {
						List<Integer> firstPositions = binary
								.getFirstHighlightedPositions().get(
										columnIndex - 1);
						List<Integer> secondPositions = binary
								.getSecondHighlightedPositions().get(
										columnIndex - 1);

						markArgument(firstRow, firstPositions);
						markArgument(secondRow, secondPositions);
					}
				}
			}
		}
	}

	private ViewerRow getNeighborRow(ViewerRow propositionRow,
			CounterExampleProposition argument) {
		ViewerRow argumentRow = propositionRow;
		Object element;

		do {
			argumentRow = argumentRow.getNeighbor(ViewerRow.BELOW, false);

			if (argumentRow == null)
				return null;

			element = argumentRow.getElement();
		} while (!element.equals(argument));

		return argumentRow;
	}

	private void markArgument(ViewerRow row, List<Integer> positions) {
		Display display = Display.getDefault();

		if (row != null) {
			for (Integer position : positions) {
				ViewerCell neighborCell = row.getCell(position + 1);

				System.out.println("Argument: " + neighborCell.getElement());

				if (neighborCell.getText().equals("T")) {
					neighborCell.setBackground(display
							.getSystemColor(SWT.COLOR_GREEN));
				} else if (neighborCell.getText().equals("F")) {
					neighborCell.setBackground(display
							.getSystemColor(SWT.COLOR_RED));
				} else if (neighborCell.getText().equals("U")) {
					neighborCell.setBackground(display
							.getSystemColor(SWT.COLOR_GRAY));
				}

				coloredCells.add(neighborCell);
			}
		}
	}
}
