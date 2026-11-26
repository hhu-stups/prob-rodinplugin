/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.ticket;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * The dialog for ProB's errors and warnings (instead of Eclipse's error
 * dialog, which shows the whole message in a label that can get larger than
 * the screen): a short header, a list of the problems with an error or
 * warning icon each, and below it the full text of the selected problem in
 * a scrollable field (in a fixed-width font, as ProB formats its messages).
 * The divider between the list and the text can be dragged, and the dialog
 * can be resized.
 * <p>
 * A message like "ProB reported errors:" followed by ProB's messages is
 * split into one entry per message. Problems reported while the dialog is
 * open are added to it, so that a series of problems does not open a series
 * of dialogs.
 */
public final class ProBProblemDialog extends Dialog {

	private static final int COPY_ID = IDialogConstants.CLIENT_ID + 1;
	private static final int MAX_LABEL = 200;

	/** a problem in the list */
	private static final class Entry {
		final int severity;
		final String text;

		Entry(final int severity, final String text) {
			this.severity = severity;
			this.text = text;
		}

		/** @return the first line, for the list */
		String summary() {
			String line = text.trim();
			final int newline = line.indexOf('\n');
			if (newline >= 0)
				line = line.substring(0, newline).trim();
			return line.length() > MAX_LABEL ? line.substring(0, MAX_LABEL)
					+ "..." : line;
		}
	}

	/** the open dialog, to add further problems to */
	private static ProBProblemDialog open;

	private final List<Entry> entries = new ArrayList<Entry>();
	private final List<String> headers = new ArrayList<String>();
	private int worst = IStatus.INFO;

	private SashForm sash;
	private Label icon;
	private Label header;
	private Table table;
	private Text details;
	private Font font;

	private ProBProblemDialog(final Shell parent) {
		super(parent);
		setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
		setBlockOnOpen(false);
	}

	/**
	 * Shows the problem, in the open dialog if there is one; must be called
	 * in the UI thread
	 */
	public static void show(final Shell parent, final IStatus status) {
		if (open != null && open.getShell() != null
				&& !open.getShell().isDisposed()) {
			open.add(status);
			open.refresh();
			return;
		}
		final ProBProblemDialog dialog = new ProBProblemDialog(parent);
		dialog.add(status);
		open = dialog;
		dialog.open();
	}

	@Override
	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setText("ProB Problem");
	}

	@Override
	protected boolean isResizable() {
		return true;
	}

	@Override
	public boolean close() {
		if (open == this)
			open = null;
		return super.close();
	}

	// ------------------ the problems

	private void add(final IStatus status) {
		addStatus(status);
		if (status.isMultiStatus())
			for (IStatus child : status.getChildren())
				addStatus(child);
	}

	private void addStatus(final IStatus status) {
		final int severity = status.getSeverity();
		if (severity > worst)
			worst = severity;
		final String message = status.getMessage() == null ? "" : status
				.getMessage().replace("\r\n", "\n");
		final String[] lines = message.split("\n", -1);
		final List<String> items;
		// e.g. "ProB reported errors:" followed by ProB's messages
		if (lines.length > 1 && lines[0].trim().endsWith(":")) {
			headers.add(lines[0].trim());
			items = split(lines, 1);
		} else {
			headers.add(severity == IStatus.ERROR ? "An error occurred"
					: "Warning");
			items = new ArrayList<String>();
			if (!message.trim().isEmpty())
				items.add(message.trim());
		}
		for (String item : items)
			entries.add(new Entry(severityOf(item, severity), item));
		final Throwable exception = status.getException();
		if (exception != null) {
			final StringWriter trace = new StringWriter();
			exception.printStackTrace(new PrintWriter(trace));
			entries.add(new Entry(severity, "Exception: " + exception
					+ "\n\n" + trace));
		}
	}

	/**
	 * @return ProB's messages: a message starts with a line that is not
	 *         indented; indented lines (e.g. " ### Line: 3, Column: 5") and
	 *         empty lines belong to the message before
	 */
	private static List<String> split(final String[] lines, final int from) {
		final List<String> result = new ArrayList<String>();
		StringBuilder current = null;
		for (int i = from; i < lines.length; i++) {
			final String line = lines[i];
			final boolean continuation = line.isEmpty()
					|| Character.isWhitespace(line.charAt(0))
					|| line.startsWith("###");
			if (current == null || !continuation) {
				if (current != null && !current.toString().trim().isEmpty())
					result.add(current.toString().trim());
				current = new StringBuilder(line);
			} else
				current.append('\n').append(line);
		}
		if (current != null && !current.toString().trim().isEmpty())
			result.add(current.toString().trim());
		return result;
	}

	/** ProB's warnings among its errors, e.g. "Warning: ..." */
	private static int severityOf(final String item, final int severity) {
		return severity == IStatus.ERROR
				&& item.regionMatches(true, 0, "warning", 0, 7) ? IStatus.WARNING
				: severity;
	}

	private String headerText() {
		final String first = headers.size() == 1 ? headers.get(0)
				: "Several problems were reported:";
		if (entries.size() <= 1)
			return first;
		// e.g. "ProB reported errors (35 messages):"
		final String count = " (" + entries.size() + " messages)";
		return first.endsWith(":") ? first.substring(0, first.length() - 1)
				+ count + ":" : first + count;
	}

	// ------------------ the widgets

	@Override
	protected Control createDialogArea(final Composite parent) {
		final Composite area = (Composite) super.createDialogArea(parent);
		final GridLayout layout = (GridLayout) area.getLayout();
		layout.numColumns = 2;

		icon = new Label(area, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.BEGINNING)
				.applyTo(icon);
		header = new Label(area, SWT.WRAP);
		GridDataFactory.fillDefaults().grab(true, false).hint(500, SWT.DEFAULT)
				.applyTo(header);

		sash = new SashForm(area, SWT.VERTICAL | SWT.SMOOTH);
		// smaller for a single short message
		GridDataFactory.fillDefaults().grab(true, true).span(2, 1)
				.hint(650, entries.size() == 1 && isShort(entries.get(0)) ? 120
						: 380).applyTo(sash);

		table = new Table(sash, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		final TableColumn column = new TableColumn(table, SWT.NONE);
		table.addListener(SWT.Resize, e -> fitColumn(column));
		table.addListener(SWT.Selection, e -> showSelected());

		// wrapped: ProB's messages are often long sentences
		details = new Text(sash, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP
				| SWT.V_SCROLL | SWT.BORDER);
		// fixed width, as ProB aligns parts of its messages; at the size of
		// the other text (the editors' text font is often larger)
		final int height = JFaceResources.getDefaultFont().getFontData()[0]
				.getHeight();
		font = textFont(details.getDisplay(), height);
		details.setFont(font);
		details.addListener(SWT.Dispose, e -> font.dispose());
		sash.setWeights(new int[] { 2, 3 });

		refresh();
		return area;
	}

	/** Eclipse's text font at the height; a monospace font if it is not set */
	private static Font textFont(final Display display, final int height) {
		try {
			return JFaceResources.getTextFontDescriptor().setHeight(height)
					.createFont(display);
		} catch (RuntimeException e) {
			return FontDescriptor.createFrom("Monospace", height, SWT.NORMAL)
					.createFont(display);
		}
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent) {
		createButton(parent, COPY_ID, "Copy All", false);
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL,
				true);
	}

	@Override
	protected void buttonPressed(final int buttonId) {
		if (buttonId == COPY_ID) {
			copyAll();
			return;
		}
		super.buttonPressed(buttonId);
	}

	/** shows the entries; keeps the selection */
	private void refresh() {
		if (table == null || table.isDisposed())
			return;
		icon.setImage(largeImage(worst));
		header.setText(headerText());
		final int selected = Math.max(0, table.getSelectionIndex());
		table.removeAll();
		for (Entry entry : entries) {
			final TableItem item = new TableItem(table, SWT.NONE);
			item.setText(entry.summary());
			item.setImage(image(entry.severity));
		}
		fitColumn(table.getColumn(0));
		// no list for a single message
		final boolean single = entries.size() == 1;
		if (single != (sash.getMaximizedControl() == details)) {
			sash.setMaximizedControl(single ? details : null);
			if (!single)
				grow();
		}
		if (!entries.isEmpty()) {
			table.setSelection(Math.min(selected, entries.size() - 1));
			showSelected();
		}
		header.getParent().layout(true, true);
	}

	private static boolean isShort(final Entry entry) {
		return entry.text.length() < 400
				&& entry.text.split("\n").length <= 5;
	}

	/** room for the list when further problems are added */
	private void grow() {
		final Shell shell = getShell();
		if (shell == null || !shell.isVisible())
			return;
		final Point size = shell.getSize();
		final Rectangle screen = shell.getMonitor().getClientArea();
		final int height = Math.min(Math.max(size.y, 560),
				screen.height * 8 / 10);
		if (height > size.y)
			shell.setSize(size.x, height);
	}

	private void showSelected() {
		final int index = table.getSelectionIndex();
		details.setText(index < 0 || index >= entries.size() ? ""
				: entries.get(index).text);
	}

	/** the column fills the table, or is wider with a scroll bar */
	private void fitColumn(final TableColumn column) {
		if (column.isDisposed())
			return;
		column.pack();
		final int client = table.getClientArea().width;
		if (column.getWidth() < client)
			column.setWidth(client);
	}

	private void copyAll() {
		final StringBuilder text = new StringBuilder(headerText());
		for (Entry entry : entries)
			text.append("\n\n").append(entry.text);
		final Clipboard clipboard = new Clipboard(getShell().getDisplay());
		try {
			clipboard.setContents(new Object[] { text.toString() },
					new Transfer[] { TextTransfer.getInstance() });
		} finally {
			clipboard.dispose();
		}
	}

	/** the large icon of the header */
	private Image largeImage(final int severity) {
		final Display display = getShell() != null ? getShell().getDisplay()
				: Display.getDefault();
		return display.getSystemImage(severity == IStatus.ERROR ? SWT.ICON_ERROR
				: severity == IStatus.WARNING ? SWT.ICON_WARNING
						: SWT.ICON_INFORMATION);
	}

	/** the small icon of a problem, as in the Problems view */
	private static Image image(final int severity) {
		if (!PlatformUI.isWorkbenchRunning())
			return null;
		return PlatformUI.getWorkbench().getSharedImages().getImage(
				severity == IStatus.ERROR ? ISharedImages.IMG_OBJS_ERROR_TSK
						: severity == IStatus.WARNING ? ISharedImages.IMG_OBJS_WARN_TSK
								: ISharedImages.IMG_OBJS_INFO_TSK);
	}

	@Override
	protected Point getInitialSize() {
		// not larger than the screen
		final Point size = super.getInitialSize();
		final Rectangle screen = getShell().getMonitor().getClientArea();
		return new Point(Math.min(size.x, screen.width * 9 / 10), Math.min(
				size.y, screen.height * 8 / 10));
	}
}
