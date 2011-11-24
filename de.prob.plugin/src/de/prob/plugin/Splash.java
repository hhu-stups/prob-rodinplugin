package de.prob.plugin;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.splash.AbstractSplashHandler;

import de.prob.ui.ProbUiPlugin;

public class Splash extends AbstractSplashHandler {

	@Override
	public void init(final Shell splash) {
		super.init(splash);

		final Image overlay = ProbUiPlugin.getDefault().getImageRegistry().get(
				ProbUiPlugin.OVERLAY);

		// FillLayout layout = new FillLayout();
		//
		// getSplash().setLayout(layout);

		// Force shell to inherit the splash background
		getSplash().setBackgroundMode(SWT.INHERIT_DEFAULT);

		Label transparentIdeaLabel = new Label(getSplash(), SWT.NONE);
		transparentIdeaLabel.setImage(overlay);

		transparentIdeaLabel.setBounds(10, 220, 90, 35);

		splash.layout(true);

	}
}
