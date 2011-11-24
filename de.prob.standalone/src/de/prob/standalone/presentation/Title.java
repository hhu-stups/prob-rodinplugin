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

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class Title extends AbstractClosable {
	private final IStackPresentationSite site;

	Color colorTop = new Color(getDisplay(), 142, 158, 170);
	// Color colorTop = new Color(getDisplay(), 135, 135, 135);

	Color colorBottom = new Color(getDisplay(), 13, 63, 110);

	Color colorText = new Color(getDisplay(), 255, 255, 255);

	protected Font font = new Font(getDisplay(), "Default", 10, SWT.BOLD);

	/**
	 * This listener responds to selection events in all tab buttons.
	 */
	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseDown(final MouseEvent e) {
			if (part != null) {
				part.setFocus();
				if (isCloseSelected) {
					site.close(new IPresentablePart[] { part });
				}
			}
		}
	};

	public Title(final Composite parent, final int style,
			final IStackPresentationSite site) {
		super(parent, style | SWT.NO_BACKGROUND);
		this.site = site;
		addPaintListener(this);
		addMouseListener(mouseListener);
	}

	/**
	 * Paint the title
	 */
	public void paintControl(final PaintEvent e) {
		Rectangle clientArea = getBounds();
		Image img = new Image(e.display, clientArea.width, clientArea.height);
		GC gc = new GC(img);

		gc.setForeground(colorTop);
		gc.setBackground(colorBottom);
		gc.fillGradientRectangle(0, 0, clientArea.width, clientArea.height,
				true);

		if (part != null) {
			gc.setFont(JFaceResources.getBannerFont());
			gc.setForeground(colorText);
			String dirty = "";
			if (part.isDirty()) {
				dirty = "*";
			}
			String text = shortenText(gc, dirty + part.getTitle(),
					clientArea.width - 30);
			gc.drawText(text, 5, 2, true);

			if (part.isCloseable()) {
				if (isCloseSelected) {
					gc.drawImage(closeSelectedImage, clientArea.width - 20, 2);
				} else {
					gc
							.drawImage(closeUnselectedImage,
									clientArea.width - 20, 2);
				}
			}
		}

		e.gc.drawImage(img, 0, 0);
		gc.dispose();
		img.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		colorTop.dispose();
		colorBottom.dispose();
		colorText.dispose();
		font.dispose();
		super.dispose();
	}

}
