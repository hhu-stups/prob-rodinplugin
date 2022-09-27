package de.prob.ui.ltl;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public final class CounterExampleTreeViewer extends TreeViewer {

	public CounterExampleTreeViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Item getItemAt(Point p) {
		Tree tree = getTree();
		TreeItem[] selection = tree.getSelection();

		if (selection.length == 1) {
			int columnCount = tree.getColumnCount();

			for (int i = 0; i < columnCount; i++) {
				if (selection[0].getBounds(i).contains(p)) {
					return selection[0];
				}
			}
		}

		TreeItem item = getItemAt(null, p);

		return item;
	}

	private TreeItem getItemAt(TreeItem parentItem, Point point) {
		TreeItem[] items = (parentItem == null) ? getTree().getItems()
				: parentItem.getItems();

		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < getTree().getColumnCount(); j++) {
				if (items[i].getBounds(j).contains(point)) {
					return items[i];
				}
			}

			TreeItem foundItem = null;

			if (items[i].getExpanded() == true) {
				foundItem = getItemAt(items[i], point);
			}

			if (foundItem != null) {
				return foundItem;
			}
		}

		return null;
	}
}
