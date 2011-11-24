package de.prob.ui.ltl;

import org.eclipse.swt.widgets.Composite;

/***
 * Provides a customer widget for a counter example view
 * 
 * @author Andriy Tolstoy
 * 
 */
public final class CounterExampleComposite extends Composite {

	private Composite tableView;
	private Composite treeView;

	public CounterExampleComposite(Composite parent, int style) {
		super(parent, style);
	}

	public void setTableComposite(Composite composite) {
		tableView = composite;
	}

	public void setTreeComposite(Composite composite) {
		treeView = composite;
	}

	public Composite getTableView() {
		return tableView;
	}

	public Composite getTreeView() {
		return treeView;
	}
}
