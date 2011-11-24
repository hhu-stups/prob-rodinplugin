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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.IPresentablePart;
import org.eclipse.ui.presentations.IStackPresentationSite;

public class TabContainer extends Composite {

	protected List<Tab> tabItems = new ArrayList<Tab>();

	protected IPresentablePart part;

	protected int style;

	public TabContainer(final Composite parent, final int style) {
		super(parent, style);
		this.style = style;
	}

	public void addPart(final IPresentablePart part,
			final Presentation presentation, final IStackPresentationSite site) {
		Tab tab = new Tab(this, style, presentation, site);
		tab.setPresentablePart(part);
		tabItems.add(tab);
		redraw();
	}

	public void setPresentablePart(final IPresentablePart part) {
		this.part = part;
		for (Tab b : tabItems) {
			b.setSected(b.checkPart(part));
		}
		redraw();
	}

	public int getHeight() {
		if (tabItems.size() < 2)
			return 0;
		return tabItems.size() * tabItems.get(0).getHeight() - tabItems.size()
				+ 1;
	}

	@Override
	public void setBounds(final int x, final int y, final int width,
			final int height) {
		int y2 = 0;
		int h = 21;
		for (Tab b : tabItems) {
			b.setBounds(x, y2, width, h);
			y2 += 20;
		}
		super.setBounds(x, y, width, height);
	}

	@Override
	public void redraw() {
		if (tabItems.size() < 2)
			return;
		for (Tab b : tabItems) {
			b.redraw();
		}
	}

	public void removePart(final IPresentablePart oldPart) {
		Tab foundTab = null;
		for (Tab b : tabItems) {
			if (b.getPart() == oldPart) {
				foundTab = b;
				break;
			}
		}
		if (foundTab != null) {
			tabItems.remove(foundTab);
			foundTab.dispose();
		}
	}
}
