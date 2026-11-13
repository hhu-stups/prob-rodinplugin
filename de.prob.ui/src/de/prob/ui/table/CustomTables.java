/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;

import de.prob.logging.Logger;
import de.prob.ui.TableSource;

/**
 * The tables contributed with the extension point
 * <code>de.prob.ui.tables</code> (e.g. the disprover's statistics), offered in
 * toolbar menus of the ProB Table view, one per group.
 */
final class CustomTables {

	static final String EXTENSION_POINT = "de.prob.ui.tables";

	/** one contributed table */
	static final class CustomTable {
		private final IConfigurationElement element;
		private TableSource source;

		CustomTable(final IConfigurationElement element) {
			this.element = element;
		}

		String getId() {
			return element.getAttribute("id");
		}

		String getName() {
			return element.getAttribute("name");
		}

		String getDescription() {
			return element.getAttribute("description");
		}

		String getGroup() {
			return element.getAttribute("group");
		}

		/**
		 * @return the source, created when first needed (which may start
		 *         its plug-in); <code>null</code> if it cannot be created
		 */
		synchronized TableSource getSource() {
			if (source == null) {
				try {
					source = (TableSource) element
							.createExecutableExtension("class");
				} catch (CoreException | ClassCastException e) {
					Logger.notifyUser("Could not create the table "
							+ getName(), e);
				}
			}
			return source;
		}
	}

	/** @return the contributed table with this id, or <code>null</code> */
	static CustomTable find(final String id) {
		for (IConfigurationElement element : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT))
			if ("table".equals(element.getName())
					&& id.equals(element.getAttribute("id")))
				return new CustomTable(element);
		return null;
	}

	interface Handler {
		void run(CustomTable table);
	}

	private CustomTables() {
	}

	/**
	 * adds a drop-down menu per group of the contributed tables
	 *
	 * @return whether there are any
	 */
	static boolean addTo(final IToolBarManager toolBar, final Handler handler) {
		final Map<String, List<CustomTable>> groups = new LinkedHashMap<String, List<CustomTable>>();
		for (IConfigurationElement element : Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTENSION_POINT)) {
			if (!"table".equals(element.getName()))
				continue;
			final CustomTable table = new CustomTable(element);
			final String group = table.getGroup() == null ? "Other" : table
					.getGroup();
			List<CustomTable> list = groups.get(group);
			if (list == null) {
				list = new ArrayList<CustomTable>();
				groups.put(group, list);
			}
			list.add(table);
		}
		for (Map.Entry<String, List<CustomTable>> group : groups.entrySet())
			toolBar.add(createGroupAction(group.getKey(), group.getValue(),
					handler));
		return !groups.isEmpty();
	}

	private static Action createGroupAction(final String label,
			final List<CustomTable> tables, final Handler handler) {
		final Action action = new Action(label, IAction.AS_DROP_DOWN_MENU) {
			@Override
			public void runWithEvent(final Event event) {
				// a click on the button itself also opens the menu
				if (event.widget instanceof ToolItem) {
					final ToolItem item = (ToolItem) event.widget;
					final Menu menu = getMenuCreator().getMenu(
							item.getParent());
					final Rectangle bounds = item.getBounds();
					menu.setLocation(item.getParent().toDisplay(bounds.x,
							bounds.y + bounds.height));
					menu.setVisible(true);
				}
			}
		};
		action.setToolTipText(label + " tables");
		action.setMenuCreator(new IMenuCreator() {
			private Menu menu;

			@Override
			public Menu getMenu(final Control parent) {
				dispose();
				menu = new Menu(parent);
				for (final CustomTable table : tables) {
					final MenuItem item = new MenuItem(menu, SWT.PUSH);
					item.setText(table.getName());
					item.setToolTipText(table.getDescription());
					item.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {
							handler.run(table);
						}
					});
				}
				return menu;
			}

			@Override
			public Menu getMenu(final Menu parent) {
				return null;
			}

			@Override
			public void dispose() {
				if (menu != null && !menu.isDisposed())
					menu.dispose();
				menu = null;
			}
		});
		return action;
	}
}
