/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.widgets.Display;

import de.prob.core.domainobjects.State;
import de.prob.ui.stateview.statetree.*;

/**
 * A StateLabelProvider defines some properties of an entry in a table (like
 * color, image, text).
 * 
 * @author plagge
 */
public class StateLabelProvider {
	public Image getImage(final State state, final StaticStateElement element) {
		return null;
	}

	public String getText(final State state, final StaticStateElement element) {
		final String result;
		if (element != null) {
			StateDependendElement sde = getStateDependendElement(state, element);
			result = sde == null ? null : sde.getValue();
		} else {
			result = null;
		}
		return result;
	}

	public Color getBackground(final State state,
			final StaticStateElement element) {
		return null;
	}

	public Color getForeground(final State state, final boolean hasChanged,
			final StaticStateElement element) {
		final EStateTreeElementProperty property = getPropertyValue(state,
				element);
		int colorcst = SWT.COLOR_BLACK;
		if (property != null) {
			switch (property) {
			case FALSE:
				colorcst = SWT.COLOR_RED;
				break;
			case TRUE:
				colorcst = SWT.COLOR_DARK_GREEN;
				break;
			case INACTIVE:
				colorcst = SWT.COLOR_GRAY;
				break;
			case NONBOOLEAN:
				if (hasChanged) {
					colorcst = SWT.COLOR_BLUE;
				} else {
					colorcst = SWT.COLOR_BLACK;
				}
				break;
			case ERROR:
				colorcst = SWT.COLOR_MAGENTA;
				break;
			default:
				break;
			}
		} else {
			colorcst = SWT.COLOR_GRAY;
		}
		return Display.getCurrent().getSystemColor(colorcst);
	}

	private static EStateTreeElementProperty getPropertyValue(
			final State state, final StaticStateElement elem) {
		StateDependendElement sde = getStateDependendElement(state, elem);
		return sde == null ? null : sde.getProperty();
	}

	private static StateDependendElement getStateDependendElement(
			final State state, final StaticStateElement elem) {
		return state == null ? null : elem.getValue(state);
	}

}
