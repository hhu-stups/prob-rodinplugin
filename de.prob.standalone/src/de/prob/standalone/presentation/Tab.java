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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class Tab extends AbstractClosable implements PaintListener {

	private final Presentation presentation;

	private final IStackPresentationSite site;

	protected final Color unselectedTop = new Color(getDisplay(), 250, 250, 250);

	protected final Color unselectedBottom = new Color(getDisplay(), 200, 225,
			255);

	protected final Color selectedTop = new Color(getDisplay(), 250, 230, 150);

	protected final Color selectedBottom = new Color(getDisplay(), 240, 155, 30);

	protected final Color mouseOverTop = new Color(getDisplay(), 255, 248, 223);

	protected final Color mouseOverBottom = new Color(getDisplay(), 255, 225,
			120);

	protected Font font = new Font(getDisplay(), "Default", 10, SWT.NORMAL);

	private boolean isSelected;

	/**
	 * This listener responds to selection events in all tabs.
	 */
	private final MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseDown(final MouseEvent e) {
			if (part != null) {
				part.setFocus();
				if (isCloseSelected()) {
					site.close(new IPresentablePart[] { part });
				} else {
					site.selectPart(part);
					presentation.selectPart(part);
				}
			}
		}
	};

	public Tab(final Composite parent, final int style,
			final Presentation presentation, final IStackPresentationSite site) {
		super(parent, style | SWT.NO_BACKGROUND);
		this.presentation = presentation;
		this.site = site;

		setSected(false);
		addPaintListener(this);
		addMouseListener(mouseListener);
	}

	@Override
	public void setPresentablePart(final IPresentablePart part) {
		this.part = part;
		setToolTipText(part.getTitleToolTip());
		layout();
		redraw();
	}

	public boolean checkPart(final IPresentablePart part) {
		return (this.part == part);
	}

	public void setSected(final boolean selected) {
		isSelected = selected;
	}

	/**
	 * Paint the title bar
	 */
	public void paintControl(final PaintEvent e) {
		Rectangle clientArea = getBounds();
		Image img = new Image(e.display, clientArea.width, clientArea.height);
		GC gc = new GC(img);

		gc.setForeground(presentation.getColorBorder());
		gc.drawRectangle(0, 0, clientArea.width - 1, clientArea.height - 1);

		Color colorTop;
		Color colorBottom;

		if (isMouseOver) {
			if (isSelected) {
				colorTop = selectedBottom;
				colorBottom = selectedTop;
			} else {
				colorTop = mouseOverTop;
				colorBottom = mouseOverBottom;
			}
		} else {
			if (isSelected) {
				colorTop = selectedTop;
				colorBottom = selectedBottom;
			} else {
				colorTop = unselectedTop;
				colorBottom = unselectedBottom;
			}
		}
		gc.setForeground(colorTop);
		gc.setBackground(colorBottom);
		gc.fillGradientRectangle(1, 1, clientArea.width - 2,
				clientArea.height - 2, true);

		if (part != null) {
			Image partImage = part.getTitleImage();
			gc.drawImage(partImage, 2, 2);

			Color colorText = new Color(getDisplay(), 0, 0, 0);
			// gc.setFont(font);
			gc.setForeground(colorText);
			String dirty = "";
			if (part.isDirty()) {
				dirty = "*";
			}
			int closeImageOffset = 0;
			if (part.isCloseable()) {
				closeImageOffset = 20;
			}
			String text = shortenText(gc, dirty + part.getTitle(),
					clientArea.width - 25 - closeImageOffset);
			gc.drawText(text, partImage.getBounds().width + 7, 3, true);
		}

		if (part.isCloseable()) {
			if (isCloseSelected) {
				gc.drawImage(closeSelectedImage, clientArea.width - 20, 3);
			} else {
				gc.drawImage(closeUnselectedImage, clientArea.width - 20, 3);
			}
		}

		e.gc.drawImage(img, 0, 0);
		gc.dispose();
		img.dispose();
	}

	public IPresentablePart getPart() {
		return part;
	}

	public boolean isCloseSelected() {
		return isCloseSelected;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Widget#dispose()
	 */
	@Override
	public void dispose() {
		unselectedTop.dispose();
		unselectedBottom.dispose();
		selectedTop.dispose();
		selectedBottom.dispose();
		mouseOverTop.dispose();
		mouseOverBottom.dispose();
		font.dispose();
		super.dispose();
	}
}
