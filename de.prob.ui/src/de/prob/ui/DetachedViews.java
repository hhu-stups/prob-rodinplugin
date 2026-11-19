/**
 * (c) 2026 Lehrstuhl fuer Softwaretechnik und Programmiersprachen,
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.ui;

import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.ui.model.application.ui.MElementContainer;
import org.eclipse.e4.ui.model.application.ui.MUIElement;
import org.eclipse.e4.ui.model.application.ui.advanced.MPlaceholder;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;

import de.prob.logging.Logger;

/**
 * Opens the ProB Console, Graph and Table views in a window of their own if
 * the preference {@link #PREFERENCE} is set (see {@link ProBViewPreferences}):
 * a view that is created in the workbench window is detached, into the same
 * window as another of these views if one is already detached. Views that are
 * already in a detached window (e.g. when Rodin restores them) stay there.
 *
 * Uses Eclipse 4's model service: Eclipse's perspective API cannot place
 * views in detached windows. If this fails, the view just stays where it is.
 */
public final class DetachedViews {

	/** ProB for Rodin preference (not a ProB preference) */
	public static final String PREFERENCE = "openViewsDetached";

	private static final List<String> VIEWS = Arrays.asList(
			"de.prob.ui.ConsoleView", "de.prob.ui.GraphView",
			"de.prob.ui.TableView");

	private DetachedViews() {
	}

	public static boolean isEnabled() {
		final ProbUiPlugin plugin = ProbUiPlugin.getDefault();
		return plugin != null
				&& plugin.getPreferenceStore().getBoolean(PREFERENCE);
	}

	/**
	 * to be called at the end of the view's createPartControl: detaches it
	 * later, after it has been opened
	 */
	public static void viewCreated(final IViewPart view) {
		if (!isEnabled())
			return;
		final Display display = view.getSite().getShell().getDisplay();
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					detach(view);
				} catch (RuntimeException | LinkageError e) {
					// e.g. a changed Eclipse model: the view stays docked
					Logger.info("Could not detach the view "
							+ view.getSite().getId() + ": " + e);
				}
			}
		});
	}

	private static void detach(final IViewPart view) {
		final IWorkbenchPage page = view.getSite().getPage();
		if (page == null || page.findViewReference(view.getSite().getId()) == null)
			return; // closed meanwhile
		final MPart part = view.getSite().getService(MPart.class);
		final EModelService models = view.getSite().getService(
				EModelService.class);
		final MWindow mainWindow = page.getWorkbenchWindow().getService(
				MWindow.class);
		if (part == null || models == null || mainWindow == null)
			return;
		final MUIElement placeholder = part.getCurSharedRef() != null ? part
				.getCurSharedRef() : part;
		if (isDetached(placeholder, mainWindow))
			return;

		// together with another of the views, if one is detached
		for (IViewReference ref : page.getViewReferences()) {
			if (!VIEWS.contains(ref.getId())
					|| ref.getId().equals(view.getSite().getId()))
				continue;
			final IViewPart other = ref.getView(false);
			if (other == null)
				continue;
			final MPart otherPart = other.getSite().getService(MPart.class);
			if (otherPart == null || otherPart.getCurSharedRef() == null)
				continue;
			final MPlaceholder otherPlaceholder = otherPart.getCurSharedRef();
			final Object otherParent = otherPlaceholder.getParent();
			if (isDetached(otherPlaceholder, mainWindow)
					&& otherParent instanceof MPartStack) {
				final MPartStack stack = (MPartStack) otherParent;
				if (placeholder instanceof MPlaceholder) {
					models.move((MPlaceholder) placeholder, stack);
					stack.setSelectedElement((MPlaceholder) placeholder);
					page.activate(view);
					return;
				}
			}
		}

		if (!(placeholder instanceof MPlaceholder))
			return;
		final Shell shell = page.getWorkbenchWindow().getShell();
		final Rectangle main = shell.getBounds();
		final int width = Math.max(500, Math.min(1000, main.width * 2 / 3));
		final int height = Math.max(350, Math.min(700, main.height * 2 / 3));
		models.detach((MPlaceholder) placeholder, main.x
				+ (main.width - width) / 2, main.y + (main.height - height)
				/ 2, width, height);
		page.activate(view);
	}

	/** @return whether the element is in a window other than the main one */
	private static boolean isDetached(final MUIElement element,
			final MWindow mainWindow) {
		MUIElement e = element;
		while (e != null && !(e instanceof MWindow)) {
			final MElementContainer<?> parent = e.getParent();
			e = parent;
		}
		return e != null && e != mainWindow;
	}
}
