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
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackDropResult;
import org.eclipse.ui.presentations.StackPresentation;

public class Presentation extends StackPresentation {

	private Color borderColor;

	private IPresentablePart current;

	private final PaintListener paintListener = new PaintListener() {

		public void paintControl(final PaintEvent e) {
			final Rectangle clientArea = presentationControl.getClientArea();
			// Image img = new Image(e.display, clientArea.width,
			// clientArea.height);
			// GC gc = new GC(img);
			final GC gc = e.gc;

			final int border = 1;
			gc.setLineWidth(border);
			gc.setForeground(borderColor);
			gc.drawRectangle(clientArea.x, clientArea.y, clientArea.width
					- border, clientArea.height - border);

			// e.gc.drawImage(img, 0, 0);
			// gc.dispose();
			// img.dispose();

		}
	};

	/**
	 * Listener attached to all child parts. It responds to changes in part
	 * properties
	 */
	private final IPropertyListener partPropertyChangeListener = new IPropertyListener() {

		public void propertyChanged(final Object source, final int property) {

			if (source instanceof IPresentablePart) {
				redraw();
			}
		}
	};

	private Composite presentationControl;

	private TabContainer tabContainer;

	private Title titleBar;

	private Color toolBarColor;

	public Presentation(final Composite parent,
			final IStackPresentationSite site) {
		super(site);
		// Create a top-level control for the presentation.
		presentationControl = new Composite(parent, SWT.NONE);
		borderColor = new Color(presentationControl.getDisplay(), 50, 50, 50);
		toolBarColor = new Color(presentationControl.getDisplay(), 203, 220,
				235);

		presentationControl.addPaintListener(paintListener);

		titleBar = new Title(presentationControl, SWT.NONE, getSite());
		tabContainer = new TabContainer(presentationControl, SWT.NONE);

		/*
		 * Add a dispose listener. Important because dispose() may // not always
		 * be called.
		 */
		presentationControl.addDisposeListener(new DisposeListener() {

			public void widgetDisposed(final DisposeEvent e) {
				presentationDisposed();
			}
		});

	}

	protected Presentation(final IStackPresentationSite stackSite) {
		super(stackSite);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void addPart(final IPresentablePart newPart, final Object cookie) {
		newPart.addPropertyListener(partPropertyChangeListener);
		tabContainer.addPart(newPart, this, getSite());
	}

	@Override
	public int computePreferredSize(final boolean width,
			final int availableParallel, final int availablePerpendicular,
			final int preferredResult) {
		if (width)
			return Math.max(preferredResult, 100);
		else
			return tabContainer.getHeight() + titleBar.getHeight();
	}

	@Override
	public void dispose() {
		presentationDisposed();
	}

	@Override
	public StackDropResult dragOver(final Control currentControl,
			final Point location) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Gets the colorBorder.
	 * 
	 * @return Returns the colorBorder.
	 */
	public Color getColorBorder() {
		return borderColor;
	}

	@Override
	public Control getControl() {
		return presentationControl;
	}

	@Override
	public Control[] getTabList(final IPresentablePart part) {
		return new Control[] { part.getControl() };
	}

	@Override
	public void removePart(final IPresentablePart oldPart) {
		oldPart.removePropertyListener(partPropertyChangeListener);
		tabContainer.removePart(oldPart);
		titleBar.removePart(oldPart);
		if (current == oldPart) {
			current = null;
		}
		redraw();
	}

	@Override
	public void selectPart(final IPresentablePart toSelect) {
		// Ignore redundant selections
		if (toSelect == current)
			return;

		// If there was an existing part selected, make it invisible
		if (current != null) {
			current.setVisible(false);
		}
		// Select the new part
		current = toSelect;

		// Make the part visible before setBounds, or the call to setBounds
		// may be ignored.
		if (current != null) {
			current.setVisible(true);
			setBounds(presentationControl.getBounds());
			titleBar.setPresentablePart(current);
			tabContainer.setPresentablePart(current);
		}
	}

	@Override
	public void setActive(final int newState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBounds(final Rectangle bounds) {
		// Set the bounds of the presentation widget
		presentationControl.setBounds(bounds);

		final int titlebarHeight = titleBar.getHeight();
		final Rectangle clientArea = presentationControl.getClientArea();
		titleBar.setBounds(clientArea.x + 1, clientArea.y + 1,
				clientArea.width - 2, titlebarHeight);
		final int tabPaneHeight = tabContainer.getHeight();
		tabContainer.setBounds(clientArea.x, clientArea.y + clientArea.height
				- tabPaneHeight, clientArea.width, tabPaneHeight);
		if (current != null) {
			final Rectangle contentArea = presentationControl.getBounds();
			int toolBarHeight = 0;
			final Control toolBar = current.getToolBar();
			if (toolBar != null) {
				toolBar.setBackground(toolBarColor);
				toolBarHeight = toolBar.getBounds().height;
				toolBar.setBounds(contentArea.x + 1, contentArea.y + 1
						+ titlebarHeight, contentArea.width - 2, toolBarHeight);
			}
			contentArea.x += 1;
			contentArea.y += titlebarHeight + toolBarHeight;
			contentArea.width -= 2;
			contentArea.height -= titlebarHeight + tabPaneHeight
					+ toolBarHeight;
			if (tabPaneHeight == 0) {
				contentArea.height -= 1;
			}
			current.setBounds(contentArea);
		}
	}

	@Override
	public void setState(final int state) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setVisible(final boolean isVisible) {
		// Make the presentation widget visible
		presentationControl.setVisible(isVisible);
		titleBar.setVisible(isVisible);

		// Make the currently visible part visible
		if (current != null) {
			current.setVisible(isVisible);

			if (isVisible) {
				// Restore the bounds of the currently visible part.
				// IPartPresentations can be used by multiple
				// StackPresentations,
				// although only one such presentation is ever visible at a
				// time.
				// It is possible that some other presentation has changed the
				// bounds of the part since it was last visible, so we need to
				// update the part's bounds when the presentation becomes
				// visible.

				setBounds(presentationControl.getBounds());
			}
		}
	}

	@Override
	public void showPaneMenu() {
		// TODO Auto-generated method stub
	}

	@Override
	public void showSystemMenu() {
		// TODO Auto-generated method stub
	}

	protected void presentationDisposed() {
		// Remove any listeners that were attached to any
		// global Eclipse resources. This is necessary in order to prevent
		// memory leaks.
		borderColor.dispose();
		toolBarColor.dispose();
		presentationControl.removePaintListener(paintListener);
		presentationControl.dispose();
		presentationControl = null;
	}

	protected void redraw() {
		if (presentationControl != null) {
			presentationControl.redraw();
			titleBar.redraw();
			tabContainer.redraw();
		}
	}
}
