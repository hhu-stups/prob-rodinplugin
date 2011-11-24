/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui.operationview;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

public class FilterDialog extends Dialog implements SelectionListener {
	final Shell dialog;
	private Button buttonNew;
	private Button buttonRemove;
	private Button buttonSelectAll;
	private Button buttonDeselectAll;
	private Table table;
	private Button buttonDone;
	private List<Filter> filters = new LinkedList<Filter>();
	private static final String TABLE_INFO = "User patterns to exclude from the view:";

	public FilterDialog(final Shell parent, final List<Filter> filters) {
		super(parent);
		dialog = new Shell(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL
				| SWT.RESIZE);
		dialog.setText("Filters");
		createControls();
		setFilters(filters);
	}

	public FilterDialog(final Shell parent, final int style,
			final List<Filter> filters) {
		super(parent, style);
		dialog = new Shell(parent, style);
		dialog.setText("Filters");
		createControls();
		setFilters(filters);
	}

	private void setFilters(final List<Filter> filters) {
		if (filters == null) {
			return;
		}
		this.filters.addAll(filters);
		for (Filter filter : filters) {
			TableItem tableItem = new TableItem(table, SWT.NONE);
			tableItem.setText(filter.getName());
			tableItem.setChecked(filter.getEnabled());
			tableItem.setData(filter);
		}
	}

	private void createControls() {
		dialog.setLayout(new RowLayout());

		FormLayout formLayout = new FormLayout();
		formLayout.spacing = 5;
		formLayout.marginHeight = 15;
		formLayout.marginWidth = 10;
		dialog.setLayout(formLayout);

		Label label1 = new Label(dialog, SWT.NONE);
		label1.setText(TABLE_INFO);
		FormData formaData = new FormData();
		label1.setLayoutData(formaData);

		table = new Table(dialog, SWT.CHECK | SWT.BORDER);
		formaData = new FormData();
		formaData.left = new FormAttachment(0, 0);
		formaData.top = new FormAttachment(label1, -5);
		formaData.right = new FormAttachment(75, 0);
		formaData.bottom = new FormAttachment(90, 0);
		table.setLayoutData(formaData);

		buttonNew = new Button(dialog, SWT.PUSH);
		buttonNew.setText("&New");
		buttonNew.addSelectionListener(this);
		formaData = new FormData();
		formaData.top = new FormAttachment(label1, 0);
		formaData.left = new FormAttachment(table, 0);
		formaData.right = new FormAttachment(100, 0);
		buttonNew.setLayoutData(formaData);

		buttonRemove = new Button(dialog, SWT.PUSH);
		buttonRemove.setText("&Remove");
		buttonRemove.addSelectionListener(this);
		formaData = new FormData();
		formaData.top = new FormAttachment(buttonNew, 0);
		formaData.left = new FormAttachment(table, 0);
		formaData.right = new FormAttachment(100, 0);
		buttonRemove.setLayoutData(formaData);

		buttonSelectAll = new Button(dialog, SWT.PUSH);
		buttonSelectAll.setText("Select &All");
		buttonSelectAll.addSelectionListener(this);
		formaData = new FormData();
		formaData.top = new FormAttachment(table);
		buttonSelectAll.setLayoutData(formaData);

		buttonDeselectAll = new Button(dialog, SWT.PUSH);
		buttonDeselectAll.setText("&Deselect All");
		buttonDeselectAll.addSelectionListener(this);
		formaData = new FormData();
		formaData.top = new FormAttachment(table, 0);
		formaData.left = new FormAttachment(buttonSelectAll);
		buttonDeselectAll.setLayoutData(formaData);

		buttonDone = new Button(dialog, SWT.PUSH);
		buttonDone.setText("    &Ok    ");
		buttonDone.addSelectionListener(this);
		formaData = new FormData();
		formaData.top = new FormAttachment(table, 0);
		formaData.right = new FormAttachment(100, 0);
		buttonDone.setLayoutData(formaData);

		// FormData button1Data = new FormData();
		// button1Data.left = new FormAttachment(0, 0);
		// button1Data.right = new FormAttachment(sash, 0);
		// button1Data.top = new FormAttachment(0, 0);
		// button1Data.bottom = new FormAttachment(100, 0);
		// button1.setLayoutData(button1Data);

		dialog.pack();
	}

	/**
	 * Show the Dialog.
	 * 
	 * @return A List of Strings whith the activated user filters.
	 */
	public List<Filter> open() {
		dialog.open();

		Display display = dialog.getDisplay();
		while (!dialog.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

		return filters;
	}

	public void widgetDefaultSelected(final SelectionEvent e) {

	}

	public void widgetSelected(final SelectionEvent e) {
		if (e.getSource() == buttonNew) {
			// InputDialog inputDialog = new InputDialog(dialog, "Add New
			// Filter",
			// "Pattern to exclude from the view:", null,
			// new IInputValidator() {
			//
			// public String isValid(String newText) {
			// if (newText != null && newText.length() > 0) {
			// return null;
			// }
			// return "";
			// }
			//
			// });
			//
			// inputDialog.open();

			FilterInputDialog filterDialog = new FilterInputDialog(dialog);
			Filter newFilter = filterDialog.open();

			if (newFilter != null) {
				TableItem tableItem = new TableItem(table, SWT.NONE);
				tableItem.setText(newFilter.getName());
				tableItem.setChecked(true);
				tableItem.setData(newFilter);
			}

		} else if (e.getSource() == buttonRemove) {
			if (table.getSelectionCount() > 0) {
				table.getSelection()[0].dispose();
			}
		} else if (e.getSource() == buttonSelectAll) {
			TableItem[] items = table.getItems();
			for (TableItem item : items) {
				item.setChecked(true);
			}
		} else if (e.getSource() == buttonDeselectAll) {
			TableItem[] items = table.getItems();
			for (TableItem item : items) {
				item.setChecked(false);
			}
		} else if (e.getSource() == buttonDone) {
			TableItem[] items = table.getItems();
			filters = new LinkedList<Filter>();
			for (TableItem item : items) {
				Assert.isTrue(item.getData() instanceof Filter);
				filters.add((Filter) item.getData());
			}
			dialog.dispose();
		}

	}

	private static final class FilterInputDialog extends Dialog {
		private String pattern;
		private String name;
		private Text textName;
		private Text textPattern;

		private Button buttonOk;

		public FilterInputDialog(final Shell parent) {
			super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		}

		public Filter open() {
			Shell parent = getParent();
			Shell shell = new Shell(parent, SWT.DIALOG_TRIM
					| SWT.APPLICATION_MODAL);
			shell.setText(getText());

			createControls(shell);

			shell.pack();
			shell.open();
			Display display = parent.getDisplay();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}

			if (pattern == null) {
				return null;
			}

			if (name == null || name.length() == 0) {
				name = pattern;
			}

			return new Filter(pattern, name, true);
		}

		private void createControls(final Shell shell) {

			FormLayout formLayout = new FormLayout();
			formLayout.marginHeight = 15;
			formLayout.marginWidth = 10;
			formLayout.spacing = 5;
			shell.setLayout(formLayout);

			Label labelPattern = new Label(shell, SWT.NONE);
			labelPattern
					.setText("Pattern to exclude from the view:\n(Pattern is case sensitive. *=any string, ?=any character.)");
			FormData formData = new FormData();
			formData.left = new FormAttachment(0, 0);
			labelPattern.setLayoutData(formData);

			textPattern = new Text(shell, SWT.BORDER);
			formData = new FormData();
			formData.width = 300;
			formData.left = new FormAttachment(0, 0);
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(labelPattern);
			textPattern.setLayoutData(formData);

			Label labelName = new Label(shell, SWT.NONE);
			labelName.setText("Name (optional):");
			formData = new FormData();
			formData.left = new FormAttachment(0, 0);
			formData.top = new FormAttachment(textPattern);
			labelName.setLayoutData(formData);

			textName = new Text(shell, SWT.BORDER);
			formData = new FormData();
			formData.width = 300;
			formData.left = new FormAttachment(0, 0);
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(labelName);
			textName.setLayoutData(formData);

			Button buttonCancel = new Button(shell, SWT.BORDER);
			buttonCancel.setText("&Cancel");
			formData = new FormData();
			formData.right = new FormAttachment(100, 0);
			formData.top = new FormAttachment(textName);
			buttonCancel.setLayoutData(formData);

			buttonOk = new Button(shell, SWT.BORDER);
			buttonOk.setText("    &Ok    ");
			formData = new FormData();
			formData.right = new FormAttachment(buttonCancel, 0);
			formData.top = new FormAttachment(textName);
			buttonOk.setLayoutData(formData);
			buttonOk.setEnabled(false);

			buttonCancel.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					pattern = null;
					shell.dispose();
				}
			});
			buttonOk.addListener(SWT.Selection, new Listener() {
				public void handleEvent(final Event event) {
					name = textName.getText();
					shell.dispose();
				}
			});
			textPattern.addListener(SWT.Modify, new Listener() {
				public void handleEvent(final Event event) {
					if (textPattern.getText().length() == 0) {
						pattern = null;
						buttonOk.setEnabled(false);
					} else {
						name = textName.getText();
						pattern = textPattern.getText();
						buttonOk.setEnabled(true);
					}
				}
			});

			shell.setDefaultButton(buttonOk);
		}

	}
}
