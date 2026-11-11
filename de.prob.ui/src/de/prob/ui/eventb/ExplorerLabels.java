/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui.eventb;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * Updates the icons of the Event-B Explorer after ProB's problem markers have
 * changed: it shows the markers' severity, but does not notice when markers
 * are only deleted, and would keep e.g. a red error icon.
 */
public final class ExplorerLabels {

	private static final String EXPLORER_ID = "fr.systerel.explorer.navigator.view";

	private ExplorerLabels() {
	}

	/** may be called in any thread */
	public static void refreshLater() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final Display display = PlatformUI.getWorkbench().getDisplay();
		if (display == null || display.isDisposed())
			return;
		display.asyncExec(ExplorerLabels::refresh);
	}

	private static void refresh() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		for (final IWorkbenchWindow window : PlatformUI.getWorkbench()
				.getWorkbenchWindows()) {
			for (final IWorkbenchPage page : window.getPages()) {
				final IViewPart view = page.findView(EXPLORER_ID);
				if (!(view instanceof CommonNavigator))
					continue;
				final CommonViewer viewer = ((CommonNavigator) view)
						.getCommonViewer();
				if (viewer != null && viewer.getControl() != null
						&& !viewer.getControl().isDisposed())
					viewer.refresh(true); // also the labels
			}
		}
	}
}
