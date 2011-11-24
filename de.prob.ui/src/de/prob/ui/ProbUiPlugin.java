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

import de.prob.logging.Logger;
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
	public static final String OVERLAY = "OVERLAY";
	public static final String CHANGE_STAR = "change_star";
	public static final String IMG_RELOAD = "IMG_RELOAD";

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
		reg.put(OVERLAY,
				imageDescriptorFromPlugin(PLUGIN_ID,
						"icons/splash_overlay_x.png"));
		reg.put(CHANGE_STAR,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/star.png"));
		reg.put(IMG_RELOAD,
				imageDescriptorFromPlugin(PLUGIN_ID, "icons/refresh.gif"));
	}

}
