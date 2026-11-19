/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.prob.core.Animator;
import de.prob.core.command.CommandPreferences;
import de.prob.core.command.GetPreferenceInfosCommand;
import de.prob.core.command.GetPreferenceInfosCommand.Info;
import de.prob.exceptions.ProBException;

/**
 * Edits the ProB preferences relevant for a graph or table, e.g. of the ProB
 * Graph view; when they are applied, they are set in ProB and stored (see
 * {@link CommandPreferences}), and the view is refreshed.
 */
public final class CommandPreferencesDialog extends Dialog {

	private static final int DEFAULTS_ID = IDialogConstants.CLIENT_ID + 1;
	private static final Pattern RANGE = Pattern
			.compile("range\\((-?\\d+),(-?\\d+)\\)");

	private final String what;
	private final List<Info> infos;
	private final List<Editor> editors = new ArrayList<Editor>();
	private Map<String, String> changed = Collections.emptyMap();

	/**
	 * Lets the user edit the preferences of a command and, if they change any,
	 * sets them in the background and then runs refresh in the UI thread.
	 *
	 * @param what
	 *            the name of the graph or table, for the title
	 * @param preferences
	 *            the names of the preferences, see
	 *            GetCommandsCommand.CommandInfo#getPreferences()
	 */
	public static void open(final Shell shell, final String what,
			final List<String> preferences, final Runnable refresh) {
		final String title = "Preferences for " + what;
		if (preferences.isEmpty()) {
			MessageDialog.openInformation(shell, title, what
					+ " has no preferences.");
			return;
		}
		final Animator animator = Animator.getAnimator();
		final List<Info> infos;
		try {
			infos = Animator
					.callQuietly(new Animator.ProBCall<List<Info>>() {
						@Override
						public List<Info> call() throws ProBException {
							return GetPreferenceInfosCommand.getInfos(animator,
									preferences);
						}
					});
		} catch (ProBException e) {
			MessageDialog.openError(shell, title,
					"ProB could not list the preferences:\n\n"
							+ e.getMessage());
			return;
		}
		if (infos.isEmpty()) {
			MessageDialog.openInformation(shell, title, what
					+ " has no preferences.");
			return;
		}
		final CommandPreferencesDialog dialog = new CommandPreferencesDialog(
				shell, what, infos);
		if (dialog.open() != OK || dialog.changed.isEmpty())
			return;
		final Map<String, String> values = dialog.changed;
		final Job job = new Job("Setting ProB preferences") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				String error = null;
				try {
					Animator.callQuietly(new Animator.ProBCall<Void>() {
						@Override
						public Void call() throws ProBException {
							CommandPreferences.set(animator, values);
							return null;
						}
					});
				} catch (ProBException e) {
					error = e.getMessage();
				}
				final String message = error;
				final Display display = shell.getDisplay();
				if (!display.isDisposed())
					display.asyncExec(new Runnable() {
						@Override
						public void run() {
							if (message != null && !shell.isDisposed())
								MessageDialog.openError(shell, title,
										"ProB could not set the preferences:\n\n"
												+ message);
							refresh.run();
						}
					});
				return Status.OK_STATUS;
			}
		};
		job.setSystem(true);
		job.schedule();
	}

	private CommandPreferencesDialog(final Shell shell, final String what,
			final List<Info> infos) {
		super(shell);
		this.what = what;
		this.infos = infos;
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("Preferences for " + what);
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final Label intro = new Label(area, SWT.WRAP);
		intro.setText("ProB preferences used for " + what
				+ ". OK sets them and computes it again."
				+ " Hover over a preference for its name in ProB.");
		GridDataFactory.fillDefaults().grab(true, false).hint(520, SWT.DEFAULT)
				.applyTo(intro);

		final ScrolledComposite scrolled = new ScrolledComposite(area,
				SWT.V_SCROLL);
		scrolled.setExpandHorizontal(true);
		scrolled.setExpandVertical(true);
		final Composite grid = new Composite(scrolled, SWT.NONE);
		grid.setLayout(new GridLayout(2, false));
		for (Info info : infos)
			editors.add(new Editor(grid, info));
		scrolled.setContent(grid);
		final Point size = grid.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		scrolled.setMinSize(size);
		GridDataFactory.fillDefaults().grab(true, true)
				.hint(Math.max(520, size.x), Math.min(size.y, 450))
				.applyTo(scrolled);
		return area;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, DEFAULTS_ID, "Restore Defaults", false);
		super.createButtonsForButtonBar(parent);
	}

	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == DEFAULTS_ID) {
			for (Editor editor : editors)
				editor.setValue(editor.info.getDefaultValue());
			return;
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected void okPressed() {
		final Map<String, String> values = new LinkedHashMap<String, String>();
		for (Editor editor : editors) {
			final String value = editor.getValue().trim();
			final String error = editor.check(value);
			if (error != null) {
				MessageDialog.openError(getShell(), "Preferences for " + what,
						error);
				editor.focus();
				return;
			}
			if (!value.equals(editor.info.getCurrentValue()))
				values.put(editor.info.getName(), value);
		}
		changed = values;
		super.okPressed();
	}

	/** the field for one preference */
	private static final class Editor {
		private final Info info;
		private Button check;
		private Combo combo;
		private Text text;

		Editor(final Composite parent, final Info info) {
			this.info = info;
			final String tooltip = info.getName() + " (default: "
					+ info.getDefaultValue() + ")";
			final String type = info.getType();
			if ("bool".equals(type)) {
				check = new Button(parent, SWT.CHECK);
				check.setText(info.getDescription());
				check.setToolTipText(tooltip);
				GridDataFactory.fillDefaults().span(2, 1).applyTo(check);
			} else {
				final Label label = new Label(parent, SWT.NONE);
				label.setText(info.getDescription() + ":");
				label.setToolTipText(tooltip);
				if (!info.getValues().isEmpty()) {
					combo = new Combo(parent, SWT.READ_ONLY);
					combo.setItems(info.getValues().toArray(new String[0]));
					combo.setToolTipText(tooltip);
					combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
							true, false));
				} else if ("rgb_color".equals(type)) {
					final Composite row = new Composite(parent, SWT.NONE);
					final GridLayout layout = new GridLayout(2, false);
					layout.marginWidth = 0;
					layout.marginHeight = 0;
					row.setLayout(layout);
					row.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
							true, false));
					text = new Text(row, SWT.BORDER);
					text.setToolTipText(tooltip
							+ "; a colour name such as blue, or #RRGGBB");
					text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
							true, false));
					final Button choose = new Button(row, SWT.PUSH);
					choose.setText("...");
					choose.setToolTipText("Choose a colour");
					choose.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(final SelectionEvent e) {
							final ColorDialog dialog = new ColorDialog(text
									.getShell());
							final RGB rgb = dialog.open();
							if (rgb != null)
								text.setText(String.format("#%02x%02x%02x",
										rgb.red, rgb.green, rgb.blue));
						}
					});
				} else {
					text = new Text(parent, SWT.BORDER);
					text.setToolTipText(tooltip
							+ (type == null ? "" : "; type: " + type));
					text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
							true, false));
				}
			}
			setValue(info.getCurrentValue());
		}

		void setValue(final String value) {
			if (check != null)
				check.setSelection("true".equals(value));
			else if (combo != null) {
				final int i = combo.indexOf(value);
				if (i >= 0)
					combo.select(i);
				else
					combo.setText(value);
			} else
				text.setText(value);
		}

		String getValue() {
			if (check != null)
				return check.getSelection() ? "true" : "false";
			if (combo != null)
				return combo.getText();
			return text.getText();
		}

		void focus() {
			(check != null ? check : combo != null ? combo : text).setFocus();
		}

		/** @return an error message if the value is not valid, else null */
		String check(final String value) {
			final String type = info.getType();
			if (type == null || check != null)
				return null;
			long min = Long.MIN_VALUE, max = Long.MAX_VALUE;
			final Matcher range = RANGE.matcher(type);
			if (range.matches()) {
				min = Long.parseLong(range.group(1));
				max = Long.parseLong(range.group(2));
			} else if ("nat".equals(type))
				min = 0;
			else if ("nat1".equals(type))
				min = 1;
			else if (!"int".equals(type))
				return null;
			try {
				final long v = Long.parseLong(value);
				if (v >= min && v <= max)
					return null;
			} catch (NumberFormatException e) {
				// below
			}
			return info.getDescription()
					+ ": "
					+ (min == Long.MIN_VALUE ? "an integer is expected"
							: max == Long.MAX_VALUE ? "an integer of at least "
									+ min + " is expected"
									: "an integer from " + min + " to " + max
											+ " is expected") + ".";
		}
	}
}
