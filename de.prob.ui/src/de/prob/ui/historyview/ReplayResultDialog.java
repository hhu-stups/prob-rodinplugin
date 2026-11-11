/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.historyview;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import de.prob.core.command.ReplayJsonTraceCommand;

/**
 * Shows the outcome of a replay that was not perfect: a summary, a table
 * explaining how each step of the trace file was replayed (if probcli provides
 * it) and ProB's messages.
 */
final class ReplayResultDialog extends MessageDialog {

	private static final String[] COLUMNS = { "Step", "Trace File",
			"Replayed", "Match", "Mismatches", "Errors" };
	private static final int[] WIDTHS = { 45, 230, 200, 130, 110, 260 };

	private final List<ReplayJsonTraceCommand.Step> table;
	private final List<String> messages;

	ReplayResultDialog(final Shell shell, final String title,
			final String summary, final boolean error,
			final List<ReplayJsonTraceCommand.Step> table,
			final List<String> messages) {
		super(shell, title, null, summary, error ? MessageDialog.ERROR
				: MessageDialog.WARNING,
				new String[] { IDialogConstants.OK_LABEL }, 0);
		this.table = table;
		this.messages = messages;
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createCustomArea(final Composite parent) {
		final Composite area = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 10;
		area.setLayout(layout);
		area.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		if (!table.isEmpty())
			createTable(area);
		if (!messages.isEmpty())
			createMessages(area);
		return area;
	}

	private void createTable(final Composite parent) {
		final Table t = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.V_SCROLL | SWT.H_SCROLL);
		t.setHeaderVisible(true);
		t.setLinesVisible(true);
		for (int i = 0; i < COLUMNS.length; i++) {
			final TableColumn column = new TableColumn(t, i == 0 ? SWT.RIGHT
					: SWT.LEFT);
			column.setText(COLUMNS[i]);
			column.setWidth(WIDTHS[i]);
		}
		final Color red = t.getDisplay().getSystemColor(SWT.COLOR_RED);
		final Color orange = t.getDisplay().getSystemColor(
				SWT.COLOR_DARK_YELLOW);
		for (ReplayJsonTraceCommand.Step step : table) {
			final TableItem item = new TableItem(t, SWT.NONE);
			final String[] texts = { Integer.toString(step.getNumber()),
					step.getTraceFile(), step.getReplayed(),
					matchText(step.getMatch()),
					String.join(", ", step.getMismatches()),
					String.join("; ", step.getErrors()) };
			item.setText(texts);
			if (step.isFailed())
				item.setForeground(red);
			else if (!step.isPrecise())
				item.setForeground(orange);
		}
		GridDataFactory.fillDefaults().grab(true, true)
				.hint(SWT.DEFAULT, Math.min(table.size(), 12)
						* t.getItemHeight() + t.getHeaderHeight() + 8)
				.applyTo(t);
		final Label legend = new Label(parent, SWT.WRAP);
		legend.setText("Match: \"precise\" means parameters, results and target state "
				+ "as in the trace file; \"ignoring target state\" and "
				+ "\"operation name only\" mean that ProB had to relax these "
				+ "requirements; \"failed\" steps were skipped. Mismatches "
				+ "lists the parameters, results and variables whose values "
				+ "differ from the trace file.");
		GridDataFactory.fillDefaults().grab(true, false)
				.hint(600, SWT.DEFAULT).applyTo(legend);
	}

	private void createMessages(final Composite parent) {
		final Label label = new Label(parent, SWT.NONE);
		label.setText("ProB reported:");
		final Text text = new Text(parent, SWT.BORDER | SWT.MULTI
				| SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		text.setText(String.join("\n", messages));
		GridDataFactory.fillDefaults().grab(true, table.isEmpty())
				.hint(600, Math.min(messages.size() + 1, 8)
						* text.getLineHeight()).applyTo(text);
	}

	/** @return a readable name for ProB's match info */
	static String matchText(final String match) {
		switch (match) {
		case "precise":
			return "precise";
		case "precise_with_errs":
			return "precise, with errors";
		case "params_and_results":
			return "ignoring target state";
		case "keep_name":
			return "operation name only";
		case "failed":
			return "failed";
		case "-":
			return "not replayed";
		default:
			return match;
		}
	}
}
