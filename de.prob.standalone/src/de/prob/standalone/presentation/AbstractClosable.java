/*******************************************************************************
 * Copyright (c) 2009 Siemens AG
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Kai Tödter - initial API and implementation
 *******************************************************************************/

package de.prob.standalone.presentation;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.presentations.IPresentablePart;

import de.prob.standalone.Activator;

public abstract class AbstractClosable extends Canvas implements PaintListener {

	private static final String PLUGIN = Activator.PLUGIN_ID;

	protected static Image closeUnselectedImage;

	protected static Image closeSelectedImage;

	protected boolean isCloseSelected;

	protected boolean isMouseOver;

	protected IPresentablePart part;

	private final MouseMoveListener mouseMoveListener = new MouseMoveListener() {
		public void mouseMove(final MouseEvent e) {
			boolean oldState = isCloseSelected;
			Rectangle clientArea = getBounds();
			clientArea.x = clientArea.width - 20;
			clientArea.width = closeSelectedImage.getBounds().width;
			clientArea.y = 2;
			clientArea.height = closeSelectedImage.getBounds().height;
			if (clientArea.contains(e.x, e.y)) {
				isCloseSelected = true;
			} else {
				isCloseSelected = false;
			}

			if (oldState != isCloseSelected) {
				redraw();
			}
		}
	};

	protected MouseTrackAdapter mouseTrackAdapter = new MouseTrackAdapter() {
		@Override
		public void mouseEnter(final MouseEvent e) {
			isMouseOver = true;
			redraw();
		}

		@Override
		public void mouseExit(final MouseEvent e) {
			isMouseOver = false;
			isCloseSelected = false;
			redraw();
		}
	};

	static {
		ImageDescriptor imageDescriptor = AbstractUIPlugin
				.imageDescriptorFromPlugin(PLUGIN, "images/close_blue.gif");
		if (imageDescriptor != null) {
			closeUnselectedImage = imageDescriptor.createImage();
		}
		imageDescriptor = AbstractUIPlugin.imageDescriptorFromPlugin(PLUGIN,
				"images/closeSelected.gif");
		if (imageDescriptor != null) {
			closeSelectedImage = imageDescriptor.createImage();
		}
	}

	public AbstractClosable(final Composite parent, final int style) {
		super(parent, style);
		addMouseMoveListener(mouseMoveListener);
		addMouseTrackListener(mouseTrackAdapter);
	}

	public void setPresentablePart(final IPresentablePart part) {
		this.part = part;
		setToolTipText(part.getTitleToolTip());
		layout();
		redraw();
	}

	public int getHeight() {
		return 21;
	}

	public void removePart(final IPresentablePart oldPart) {
		if (part == oldPart) {
			part = null;
		}
	}

	protected String shortenText(final GC gc, String text, final int width) {
		if (text == null)
			return null;
		if (gc.textExtent(text, 0).x <= width)
			return text;
		String elipse = "...";
		int elipseWidth = gc.textExtent(elipse, 0).x;
		int textLength = text.length();

		while (textLength >= 0) {
			String s1 = text.substring(0, textLength);
			int textWidth = gc.textExtent(s1, 0).x;

			if (textWidth + elipseWidth < width) {
				text = s1 + elipse;
				break;
			}
			textLength--;

		}
		return text;
	}

}
