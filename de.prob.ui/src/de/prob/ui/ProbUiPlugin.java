/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.prob.ui;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import de.prob.core.UnsatAxiomsNotice;
import de.prob.logging.Logger;
import de.prob.ui.eventb.ExplorerLabels;
import de.prob.ui.ticket.NonModalNotification;
import de.prob.ui.ticket.ProBLogListener;

public class ProbUiPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "de.prob.ui";

	public static final String IMG_FILTER = "IMG_FILTER";
	public static final String IMG_FORWARD = "IMG_FORWARD";
	public static final String IMG_BACK = "IMG_BACK";
	public static final String IMG_RESUME = "IMG_RESUME";
	public static final String IMG_SEARCH = "IMG_SEARCH";
	public static final String IMG_DISABLED = "IMG_DISABLED";
	public static final String IMG_TIMEOUT = "IMG_TIMEOUT";
	public static final String IMG_ENABLED = "IMG_ENABLED";
	public static final String IMG_DOUBLECLICK = "IMG_DOUBLECLICK";
	public static final String CHANGE_STAR = "change_star";
	public static final String IMG_RELOAD = "IMG_RELOAD";
	public static final String IMG_PREFERENCES = "IMG_PREFERENCES";
	public static final String IMG_CHECKED = "IMG_CHECKED";
	public static final String IMG_UNCHECKED = "IMG_UNCHECKED";
	public static final String IMG_MACHINE = "IMG_MACHINE";
	public static final String IMG_CONTEXT = "IMG_CONTEXT";
	public static final String IMG_PROB = "IMG_PROB";
	public static final String IMG_ANIMATION_DIALOG = "IMG_ANIMATION_DIALOG";

	private static ProbUiPlugin plugin;

	private BundleContext context;

	public ProbUiPlugin() {
		super();
		setInstance(this);
	}

	public Bundle[] getInstalledBundles() {
		final Bundle[] bundles = context.getBundles();
		final int bundlesSize = bundles.length;
		final Bundle[] result = new Bundle[bundlesSize];
		System.arraycopy(bundles, 0, result, 0, bundlesSize);
		return result;
	}

	private static void setInstance(final ProbUiPlugin p) {
		plugin = p;
	}

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		this.context = context;
		Logger.addListener(new ProBLogListener());
		Logger.setNonModalNotifier(new NonModalNotification());
		UnsatAxiomsNotice.setMarkersChangedListener(ExplorerLabels::refreshLater);
		UnsatAxiomsNotice.setUnsatCoreAction(ProbUiPlugin::showUnsatCore);
	}

	/**
	 * opens the ProB Table view at the Unsat Core of Properties/Axioms table,
	 * which computes it
	 */
	private static void showUnsatCore() {
		final org.eclipse.ui.IWorkbenchWindow window = org.eclipse.ui.PlatformUI
				.getWorkbench().getActiveWorkbenchWindow();
		if (window == null || window.getActivePage() == null)
			return;
		try {
			final de.prob.ui.table.TableViewPart view = (de.prob.ui.table.TableViewPart) window
					.getActivePage().showView(
							de.prob.ui.table.TableViewPart.VIEW_ID);
			view.showTable(de.prob.ui.table.TableViewPart.UNSAT_CORE_TABLE);
		} catch (org.eclipse.ui.PartInitException e) {
			Logger.notifyUser("Could not open the ProB Table view", e);
		}
	}

	/**
	 * Opens the ProB Graph view and shows a graph that is not one of the
	 * animator's, e.g. the disprover's visualisation of a counter-example.
	 * To be called in the UI thread.
	 *
	 * @param animator
	 *            the animator writing the dot file: interrupted by the Stop
	 *            button
	 */
	public static void showDotGraph(final String title,
			final de.prob.core.Animator animator, final DotSource source) {
		final org.eclipse.ui.IWorkbenchWindow window = org.eclipse.ui.PlatformUI
				.getWorkbench().getActiveWorkbenchWindow();
		if (window == null || window.getActivePage() == null)
			return;
		try {
			final de.prob.ui.graph.GraphViewPart view = (de.prob.ui.graph.GraphViewPart) window
					.getActivePage().showView(
							de.prob.ui.graph.GraphViewPart.VIEW_ID);
			view.showDotGraph(title, animator, source);
		} catch (org.eclipse.ui.PartInitException e) {
			Logger.notifyUser("Could not open the ProB Graph view", e);
		}
	}

	public static ProbUiPlugin getDefault() {
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		super.initializeImageRegistry(reg);
		reg.put(IMG_FORWARD,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/forward.gif"));
		reg.put(IMG_BACK,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/back.gif"));
		reg.put(IMG_FILTER,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/filter_ps.gif"));
		reg.put(IMG_RESUME,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/resume.gif"));
		reg.put(IMG_SEARCH,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/search_src.gif"));
		reg.put(IMG_DISABLED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/disabled.png"));
		reg.put(IMG_TIMEOUT,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/timeout.png"));
		reg.put(IMG_ENABLED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/enabled.png"));
		reg.put(IMG_DOUBLECLICK,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/doubleclick.png"));
		reg.put(CHANGE_STAR,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/star.png"));
		reg.put(IMG_PREFERENCES,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/settings.png"));
		reg.put(IMG_RELOAD,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/refresh.gif"));
		reg.put(IMG_CHECKED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/checked.gif"));
		reg.put(IMG_UNCHECKED,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/unchecked.gif"));
		reg.put(IMG_MACHINE,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/mch_obj.gif"));
		reg.put(IMG_CONTEXT,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/ctx_obj.gif"));
		reg.put(IMG_PROB,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/prob.png"));
		reg.put(IMG_ANIMATION_DIALOG, imageDescriptorFromPlugin(PLUGIN_ID,
				"icons/prob_animation_dialog.png"));
	}

}
