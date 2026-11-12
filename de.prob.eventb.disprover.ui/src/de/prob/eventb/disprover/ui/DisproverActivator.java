package de.prob.eventb.disprover.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DisproverActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "de.prob.eventb.disprover.ui";

	/**
	 * The constructor
	 */
	public DisproverActivator() {
	}

	@Override
	public void start(final org.osgi.framework.BundleContext context)
			throws Exception {
		super.start(context);
		// starts ProB's UI plug-in (loading one of its classes does), which
		// shows the disprover's notices about counter-examples
		de.prob.ui.ProbUiPlugin.getDefault();
		de.prob.eventb.disprover.core.DisproverReasoner
				.setCounterExampleVisualiser(DisproverActivator::showInGraphView);
	}

	@Override
	public void stop(final org.osgi.framework.BundleContext context)
			throws Exception {
		de.prob.eventb.disprover.core.DisproverReasoner
				.setCounterExampleVisualiser(null);
		super.stop(context);
	}

	/** shows the visualisation of a counter-example in the ProB Graph view */
	private static void showInGraphView(final String title,
			final de.prob.core.Animator animator,
			final de.prob.eventb.disprover.core.CounterExampleVisualiser.DotWriter writer) {
		de.prob.ui.ProbUiPlugin.showDotGraph(title, animator, writer::writeDot);
	}
}
