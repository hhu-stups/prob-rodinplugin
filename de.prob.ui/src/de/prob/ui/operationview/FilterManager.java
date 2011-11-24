/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

public class FilterManager implements SelectionListener {

	protected List<Filter> filters = new LinkedList<Filter>();
	protected Menu filtersMenu;
	protected List<FilterListener> listeners = new LinkedList<FilterListener>();
	protected MenuItem configureItem;

	public FilterManager(final Menu menu) {
		if (menu == null) {
			throw new IllegalArgumentException("menu can not be null");
		}
		filtersMenu = menu;
		addConfigureItem();
	}

	private void addConfigureItem() {
		new MenuItem(filtersMenu, SWT.SEPARATOR);

		configureItem = new MenuItem(filtersMenu, SWT.NONE);
		configureItem.setText("&Configure...");
		configureItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				super.widgetSelected(e);
				showFilterPopup();
			}
		});
	}

	private void showFilterPopup() {
		FilterDialog filterDialog = new FilterDialog(Display.getCurrent()
				.getActiveShell(), filters);
		clearMenu();
		this.filters = filterDialog.open();
		for (Filter filter : filters) {
			addFilterToMenu(filter);
		}
		filtersChanged();
	}

	/**
	 * dispose all MenuItems representing a filter from this.filters
	 */
	private void clearMenu() {
		for (MenuItem item : filtersMenu.getItems()) {
			if (item.getData() instanceof Filter) {
				Filter filter = (Filter) item.getData();
				if (filters.contains(filter)) {
					item.dispose();
				}
			}
		}
	}

	protected void addFilterToMenu(final Filter filter) {
		final MenuItem item = new MenuItem(filtersMenu, SWT.CHECK);
		item.setText(filter.getName());
		item.setSelection(filter.getEnabled());
		item.setData(filter);
		item.addSelectionListener(this);
	}

	/**
	 * Shortcut for addFilter(pattern, pattern)
	 * 
	 * @param pattern
	 *            : the new filters pattern and name
	 * @return the new filter
	 */
	public Filter addFilter(final String pattern) {
		return addFilter(pattern, pattern);
	}

	/**
	 * creates and adds a new enabled filter
	 * 
	 * @param pattern
	 *            : pattern of the new filter
	 * @param name
	 *            : name of the new Filter
	 * @return the new filter
	 */
	public Filter addFilter(final String pattern, final String name) {
		Filter filter = new Filter(pattern, name, true);
		addFilter(filter);
		return filter;
	}

	public void addFilter(final Filter filter) {
		filters.add(filter);
		addFilterToMenu(filter);
	}

	/**
	 * 
	 * @param string
	 * @return true if text matches any enabled filter
	 */
	public boolean match(final String text) {
		for (Filter filter : filters) {
			if (filter.getEnabled()) {
				StringMatcher stringMatcher = new StringMatcher(filter
						.getPattern(), false, false);
				if (stringMatcher.match(text)) {
					return true;
				}
			}

		}
		return false;
	}

	public void addFilterListener(final FilterListener listener) {
		listeners.add(listener);
	}

	public void removeFilterListener(final FilterListener listener) {
		listeners.remove(listener);
	}

	public void widgetDefaultSelected(final SelectionEvent e) {
	}

	public void widgetSelected(final SelectionEvent e) {
		if (e.getSource() instanceof MenuItem) {
			MenuItem item = (MenuItem) e.getSource();
			Filter filter = (Filter) item.getData();
			filter.setEnabled(item.getSelection());
			filtersChanged();
		}
	}

	public void filtersChanged() {
		for (FilterListener l : listeners) {
			l.filtersChanged();
		}
	}

}
