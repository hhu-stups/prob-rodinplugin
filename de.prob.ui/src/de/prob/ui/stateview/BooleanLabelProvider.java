/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

/**
 * 
 */
package de.prob.ui.stateview;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

/**
 * The BooleanLabelProvider acts as LabelProvider for text, image, font, fore-
 * and background color and visibility.
 * 
 * Its input is a boolean value which may be null. So we distinct between three
 * states: INACTIVE, OK and KO. For each part (text/image/font etc.) and state
 * one can define a value.
 * 
 * @author plagge
 */
public class BooleanLabelProvider extends BaseLabelProvider implements
		ILabelProvider, IColorProvider, IFontProvider, IVisibilityProvider {
	private static int INACTIVE = 0;
	private static int OK = 1;
	private static int KO = 2;

	private final Color[] foregroundColors = { null, null, null };
	private final Color[] backgroundColors = { null, null, null };
	private final String[] texts = { null, null, null };
	private final Image[] images = { null, null, null };
	private final Font[] fonts = { null, null, null };

	private boolean hideInactive = false;

	private static int toState(Object element) {
		final int state;
		if (element != null && element instanceof Boolean) {
			state = (((Boolean) element).booleanValue()) ? OK : KO;
		} else {
			state = INACTIVE;
		}
		return state;
	}

	public Color getBackground(Object element) {
		return backgroundColors[toState(element)];
	}

	public Color getForeground(Object element) {
		return foregroundColors[toState(element)];
	}

	public Image getImage(Object element) {
		return images[toState(element)];
	}

	public String getText(Object element) {
		return texts[toState(element)];
	}

	public Font getFont(Object element) {
		return fonts[toState(element)];
	}

	public boolean isVisible(Object element) {
		return !(hideInactive && toState(element) == INACTIVE);
	}

	public void setTexts(final String inactive, final String ok, final String ko) {
		texts[INACTIVE] = inactive;
		texts[OK] = ok;
		texts[KO] = ko;
		fireChangedEvent();
	}

	public void setImages(final Image inactive, final Image ok, final Image ko) {
		images[INACTIVE] = inactive;
		images[OK] = ok;
		images[KO] = ko;
		fireChangedEvent();
	}

	public void setForegroundColors(final Color inactive, final Color ok,
			final Color ko) {
		foregroundColors[INACTIVE] = inactive;
		foregroundColors[OK] = ok;
		foregroundColors[KO] = ko;
		fireChangedEvent();
	}

	public void setBackgroundColors(final Color inactive, final Color ok,
			final Color ko) {
		backgroundColors[INACTIVE] = inactive;
		backgroundColors[OK] = ok;
		backgroundColors[KO] = ko;
		fireChangedEvent();
	}

	public void setFonts(final Font inactive, final Font ok, final Font ko) {
		fonts[INACTIVE] = inactive;
		fonts[OK] = ok;
		fonts[KO] = ko;
		fireChangedEvent();
	}

	public void hideWhenInactive(final boolean hide) {
		this.hideInactive = hide;
	}

	private void fireChangedEvent() {
		fireLabelProviderChanged(new LabelProviderChangedEvent(this));
	}
}
