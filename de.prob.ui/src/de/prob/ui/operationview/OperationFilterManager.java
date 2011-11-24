/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;


public class OperationFilterManager extends FilterManager {

	private final List<Filter> operationFilters = new LinkedList<Filter>();

	public OperationFilterManager(final Menu menu) {
		super(menu);
	}

	public void addOperationFilter(final Filter filter) {
		MenuItem[] items = filtersMenu.getItems();
		int pos = 0;
		for (MenuItem item : items) {
			if (item == configureItem) {
				break;
			}
			pos++;
		}
		pos--;
		operationFilters.add(filter);

		final MenuItem item = new MenuItem(filtersMenu, SWT.CHECK, pos);
		item.setText(filter.getName());
		item.setSelection(filter.getEnabled());
		item.setData(filter);
		item.addSelectionListener(this);
	}

	@Override
	public boolean match(final String text) {
		if (super.match(text)) {
			return true;
		} else {
			for (Filter filter : operationFilters) {
				if (filter.getEnabled()) {
					StringMatcher stringMatcher = new StringMatcher(filter
							.getPattern(), false, false);
					if (stringMatcher.match(text)) {
						return true;
					}
				}

			}
		}
		return false;
	}

}
