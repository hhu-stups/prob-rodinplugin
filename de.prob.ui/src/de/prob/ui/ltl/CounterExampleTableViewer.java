package de.prob.ui.ltl;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

public final class CounterExampleTableViewer extends TableViewer {
	public CounterExampleTableViewer(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Item getItemAt(Point p) {
		Table table = getTable();
		TableItem[] items = table.getItems();

		for (int i = 0; i < items.length; i++) {
			for (int j = 0; j < table.getColumnCount(); j++) {
				Rectangle bounds = items[i].getBounds(j);

				if (bounds.contains(p)) {
					return items[i];
				}
			}
		}

		return null;
	}
}
