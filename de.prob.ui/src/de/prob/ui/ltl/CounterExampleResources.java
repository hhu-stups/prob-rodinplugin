package de.prob.ui.ltl;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/***
 * Shared fonts and colors for the LTL counter-example views. The resources are
 * owned by the JFace font and color registries (disposed together with the
 * display), so callers must never dispose them. Must be called from the UI
 * thread.
 */
final class CounterExampleResources {
	private static final String NORMAL_FONT = "de.prob.ui.ltl.counterexample.normalFont";
	private static final String BOLD_FONT = "de.prob.ui.ltl.counterexample.boldFont";
	private static final String BACKGROUND_COLOR = "de.prob.ui.ltl.counterexample.backgroundColor";

	private CounterExampleResources() {
	}

	public static Font getNormalFont() {
		return getFont(NORMAL_FONT, SWT.NORMAL);
	}

	public static Font getBoldFont() {
		return getFont(BOLD_FONT, SWT.BOLD);
	}

	public static Color getBackgroundColor() {
		final ColorRegistry registry = JFaceResources.getColorRegistry();
		if (!registry.hasValueFor(BACKGROUND_COLOR)) {
			registry.put(BACKGROUND_COLOR, new RGB(255, 255, 206));
		}
		return registry.get(BACKGROUND_COLOR);
	}

	private static Font getFont(final String key, final int style) {
		final FontRegistry registry = JFaceResources.getFontRegistry();
		if (!registry.hasValueFor(key)) {
			registry.put(key, new FontData[] { new FontData("Arial", 10,
					style) });
		}
		return registry.get(key);
	}
}
