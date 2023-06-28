package de.prob.eventb.disprover.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class DisproverActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob.eventb.disprover.ui";

	// The shared instance
	private static DisproverActivator plugin;

	/**
	 * The constructor
	 */
	public DisproverActivator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DisproverActivator getDefault() {
		return plugin;
	}

}
