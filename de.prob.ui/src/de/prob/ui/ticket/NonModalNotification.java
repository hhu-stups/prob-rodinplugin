/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.ticket;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.prob.logging.Logger;

/**
 * Shows notices of {@link Logger#notifyUserNonModal} in a small window at the
 * bottom right of the workbench window, which does not block the user and
 * closes itself after a while (unless the mouse is over it or its details
 * are shown). Several notices are stacked upwards (at most
 * {@link #MAX_SHOWN}, the oldest is closed); a notice with the same message
 * as a shown one replaces it.
 */
public final class NonModalNotification implements Logger.NonModalNotifier {

	private static final int CLOSE_AFTER_MS = 15000;
	private static final int WIDTH = 420;
	private static final int MARGIN = 16;
	private static final int GAP = 6;
	private static final int MAX_SHOWN = 4;

	/** the shown notices, the newest last; only used in the UI thread */
	private final java.util.List<Shell> popups = new java.util.ArrayList<Shell>();
	/** their messages, in the same order */
	private final java.util.List<String> messages = new java.util.ArrayList<String>();

	@Override
	public void notify(final String message, final String details) {
		notify(message, details, null, null);
	}

	@Override
	public void notify(final String message, final String details,
			final String actionLabel, final Runnable action) {
		final Display display = Display.getDefault();
		if (display == null || display.isDisposed())
			return;
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				show(display, message, details, actionLabel, action);
			}
		});
	}

	private void show(final Display display, final String message,
			final String details, final String actionLabel,
			final Runnable action) {
		// the same notice again, e.g. after loading the model again
		final int same = messages.indexOf(message);
		if (same >= 0)
			popups.get(same).close(); // removed by its dispose listener
		while (popups.size() >= MAX_SHOWN)
			popups.get(0).close();
		final Shell parent = parentShell(display);
		final Shell shell = parent == null ? new Shell(display, SWT.TOOL
				| SWT.TITLE | SWT.CLOSE | SWT.ON_TOP) : new Shell(parent,
				SWT.TOOL | SWT.TITLE | SWT.CLOSE);
		popups.add(shell);
		messages.add(message);
		shell.addListener(SWT.Dispose, e -> {
			final int i = popups.indexOf(shell);
			if (i >= 0) {
				popups.remove(i);
				messages.remove(i);
				placeAll(parent);
			}
		});
		shell.setText("ProB");
		shell.setLayout(new GridLayout(2, false));

		final Label icon = new Label(shell, SWT.NONE);
		icon.setImage(display.getSystemImage(SWT.ICON_WARNING));
		icon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		final Label text = new Label(shell, SWT.WRAP);
		text.setText(message);
		final GridData textData = new GridData(SWT.FILL, SWT.TOP, true, false);
		textData.widthHint = WIDTH;
		text.setLayoutData(textData);

		final boolean[] expanded = { false };
		if (details != null) {
			new Label(shell, SWT.NONE);
			final Link link = new Link(shell, SWT.NONE);
			link.setText("<a>Show details</a>");
			addAction(shell, actionLabel, action);
			final Text detailText = new Text(shell, SWT.MULTI | SWT.READ_ONLY
					| SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
			detailText.setText(details);
			final GridData detailData = new GridData(SWT.FILL, SWT.FILL, true,
					true, 2, 1);
			detailData.widthHint = WIDTH;
			detailData.heightHint = 120;
			detailData.exclude = true;
			detailText.setLayoutData(detailData);
			detailText.setVisible(false);
			link.addListener(SWT.Selection, e -> {
				expanded[0] = !expanded[0];
				detailData.exclude = !expanded[0];
				detailText.setVisible(expanded[0]);
				link.setText(expanded[0] ? "<a>Hide details</a>"
						: "<a>Show details</a>");
				placeAll(parent);
			});
		}

		if (details == null)
			addAction(shell, actionLabel, action);

		placeAll(parent);
		// shown without taking the focus from the user's editor or view
		shell.setVisible(true);
		closeLater(display, shell, expanded);
	}

	/**
	 * a link below the details' link (or the message) that runs the action
	 * and closes the notice
	 */
	private static void addAction(final Shell shell, final String label,
			final Runnable action) {
		if (label == null || action == null)
			return;
		new Label(shell, SWT.NONE);
		final Link link = new Link(shell, SWT.NONE);
		link.setText("<a>" + label + "</a>");
		link.addListener(SWT.Selection, e -> {
			shell.close();
			action.run();
		});
	}

	/** closes the notice after a while, unless it is being looked at */
	private static void closeLater(final Display display, final Shell shell,
			final boolean[] expanded) {
		display.timerExec(CLOSE_AFTER_MS, new Runnable() {
			@Override
			public void run() {
				if (shell.isDisposed())
					return;
				final boolean mouseOver = shell.getBounds().contains(
						display.getCursorLocation());
				if (expanded[0] || mouseOver || display.getActiveShell() == shell)
					display.timerExec(CLOSE_AFTER_MS, this);
				else
					shell.close();
			}
		});
	}

	/**
	 * sizes the notices and stacks them at the bottom right of the parent,
	 * the newest at the bottom
	 */
	private void placeAll(final Shell parent) {
		int bottom = -1;
		for (int i = popups.size() - 1; i >= 0; i--) {
			final Shell shell = popups.get(i);
			if (shell.isDisposed())
				continue;
			final Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
			final Rectangle area = parent != null && !parent.isDisposed() ? parent
					.getBounds() : shell.getDisplay().getPrimaryMonitor()
					.getClientArea();
			if (bottom < 0)
				bottom = area.y + area.height - MARGIN;
			shell.setBounds(area.x + area.width - size.x - MARGIN, bottom
					- size.y, size.x, size.y);
			shell.layout(true, true);
			bottom -= size.y + GAP;
		}
	}

	private static Shell parentShell(final Display display) {
		if (PlatformUI.isWorkbenchRunning()) {
			final IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (window != null && window.getShell() != null
					&& !window.getShell().isDisposed())
				return window.getShell();
			final IWorkbenchWindow[] windows = PlatformUI.getWorkbench()
					.getWorkbenchWindows();
			if (windows.length > 0)
				return windows[0].getShell();
		}
		return null;
	}
}
