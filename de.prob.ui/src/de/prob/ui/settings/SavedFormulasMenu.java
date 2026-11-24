/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.settings;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eventb.core.IEventBRoot;

import de.prob.ui.settings.ModelSettings.Kind;
import de.prob.ui.settings.ModelSettings.SavedFormula;

/**
 * The "Saved" drop-down next to the formula field of the ProB Graph and Table
 * views: the formulas saved for the animated model (see
 * {@link ModelSettings}), which fill in the field and show their graph or
 * table, and entries to save the formula in the field, remove saved formulas
 * and open the settings file.
 */
public final class SavedFormulasMenu {

	/** what the view provides */
	public interface Host {
		/**
		 * @return ProB's command the formula in the field is for, i.e. the
		 *         shown graph or table if it needs a formula, else the
		 *         default one
		 */
		String currentCommand();

		/** @return the name of ProB's command, e.g. for the menu */
		String commandName(String command);

		/** fills in the formula and shows the graph or table of the command */
		void show(String formula, String command);

		/** shows a short message, e.g. in the status line */
		void status(String message);
	}

	/** the length of formulas shown in the menu */
	private static final int MAX_LABEL = 50;

	private final Kind kind;
	private final Text field;
	private final Host host;

	/** adds the drop-down to the row of the field (one more column) */
	public SavedFormulasMenu(final Composite row, final Kind kind,
			final Text field, final Host host) {
		this.kind = kind;
		this.field = field;
		this.host = host;
		final ToolBar toolBar = new ToolBar(row, SWT.FLAT);
		final ToolItem item = new ToolItem(toolBar, SWT.DROP_DOWN);
		item.setText("Saved");
		item.setToolTipText("Formulas saved for the animated machine or context,"
				+ " in its settings file (e.g. M1/M1.probsettings): choose one, or save the formula in the field");
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				// the button and its arrow both open the menu
				final Rectangle bounds = item.getBounds();
				final Point location = toolBar.toDisplay(bounds.x, bounds.y
						+ bounds.height);
				final Menu menu = createMenu(toolBar);
				menu.setLocation(location);
				menu.setVisible(true);
			}
		});
	}

	private Menu createMenu(final Composite parent) {
		final Menu menu = new Menu(parent);
		menu.addListener(SWT.Hide, event -> parent.getDisplay().asyncExec(
				menu::dispose));
		final IEventBRoot model = ModelSettings.animatedModel();
		if (model == null) {
			disabled(menu, "No model is being animated");
			return menu;
		}
		final List<SavedFormula> saved = ModelSettings.formulas(model, kind);
		final String error = ModelSettings.readError(model);
		final String text = field.getText().trim();
		final String command = host.currentCommand();
		if (error != null)
			disabled(menu, "The settings file has an error: "
					+ shorten(error, 60));
		else if (saved.isEmpty())
			disabled(menu, "No formulas saved for " + model.getComponentName());
		addGrouped(menu, saved, command, new Action() {
			@Override
			public void run(final SavedFormula formula) {
				host.show(formula.formula, formula.command);
			}
		});
		new MenuItem(menu, SWT.SEPARATOR);

		final MenuItem save = new MenuItem(menu, SWT.PUSH);
		save.setText(text.isEmpty() ? "Save Formula (the field is empty)"
				: "Save Formula for " + escape(host.commandName(command)));
		save.setEnabled(!text.isEmpty() && command != null && error == null);
		save.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				save(model, text, command);
			}
		});

		final MenuItem removeItem = new MenuItem(menu, SWT.CASCADE);
		removeItem.setText("Remove Saved Formula");
		removeItem.setEnabled(!saved.isEmpty() && error == null);
		final Menu remove = new Menu(removeItem);
		removeItem.setMenu(remove);
		addGrouped(remove, saved, command, new Action() {
			@Override
			public void run(final SavedFormula formula) {
				remove(model, formula);
			}
		});

		final IFile file = ModelSettings.file(model);
		final MenuItem open = new MenuItem(menu, SWT.PUSH);
		open.setText("Open Settings File");
		open.setEnabled(file.exists());
		open.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				openFile(file);
			}
		});
		return menu;
	}

	private interface Action {
		void run(SavedFormula formula);
	}

	/**
	 * Adds the formulas grouped by their graph or table, each group under a
	 * heading with its name: first those of the current one, then the others
	 * in the order they were saved. The formulas are shown without the name
	 * of the graph or table to keep the menu narrow.
	 */
	private void addGrouped(final Menu menu, final List<SavedFormula> saved,
			final String current, final Action action) {
		final Map<String, List<SavedFormula>> groups = new LinkedHashMap<String, List<SavedFormula>>();
		if (current != null)
			groups.put(current, new ArrayList<SavedFormula>());
		for (final SavedFormula formula : saved) {
			List<SavedFormula> group = groups.get(formula.command);
			if (group == null) {
				group = new ArrayList<SavedFormula>();
				groups.put(formula.command, group);
			}
			group.add(formula);
		}
		boolean first = true;
		for (final Map.Entry<String, List<SavedFormula>> group : groups
				.entrySet()) {
			if (group.getValue().isEmpty())
				continue;
			if (group.getKey() != null) {
				if (!first)
					new MenuItem(menu, SWT.SEPARATOR);
				disabled(menu, host.commandName(group.getKey()) + ":");
			}
			first = false;
			for (final SavedFormula formula : group.getValue()) {
				final MenuItem item = new MenuItem(menu, SWT.PUSH);
				item.setText(escape(shorten(formula.formula, MAX_LABEL)));
				if (formula.formula.length() > MAX_LABEL)
					item.setToolTipText(formula.formula);
				item.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(final SelectionEvent e) {
						action.run(formula);
					}
				});
			}
		}
	}

	/** & would be a mnemonic */
	private static String escape(final String text) {
		return text.replace("&", "&&");
	}

	private static String shorten(final String text, final int max) {
		final String line = text.replaceAll("\\s+", " ");
		return line.length() <= max ? line : line.substring(0, max) + "…";
	}

	private static void disabled(final Menu menu, final String text) {
		final MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText(escape(text));
		item.setEnabled(false);
	}

	private void save(final IEventBRoot model, final String formula,
			final String command) {
		try {
			if (ModelSettings.add(model, kind, formula, command))
				host.status("Saved the formula for "
						+ host.commandName(command) + " in "
						+ ModelSettings.file(model).getFullPath());
			else
				host.status("The formula is already saved for "
						+ host.commandName(command) + ".");
		} catch (CoreException e) {
			MessageDialog.openError(field.getShell(), "Save Formula",
					"Could not save the formula: " + e.getMessage());
		}
	}

	private void remove(final IEventBRoot model, final SavedFormula formula) {
		try {
			ModelSettings.remove(model, kind, formula.formula, formula.command);
			host.status("Removed the saved formula " + formula.formula);
		} catch (CoreException e) {
			MessageDialog.openError(field.getShell(), "Remove Saved Formula",
					"Could not remove the formula: " + e.getMessage());
		}
	}

	private void openFile(final IFile file) {
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		try {
			IDE.openEditor(page, file, "org.eclipse.ui.DefaultTextEditor");
		} catch (PartInitException e) {
			MessageDialog.openError(field.getShell(), "Open Settings File",
					"Could not open " + file.getFullPath() + ": "
							+ e.getMessage());
		}
	}
}
