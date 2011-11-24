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

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.presentations.AbstractPresentationFactory;
import org.eclipse.ui.presentations.IStackPresentationSite;
import org.eclipse.ui.presentations.StackPresentation;

public class PresentationFactory extends AbstractPresentationFactory {
	@Override
	public StackPresentation createEditorPresentation(final Composite parent,
			final IStackPresentationSite site) {
		return new Presentation(parent, site);
	}

	@Override
	public StackPresentation createViewPresentation(final Composite parent,
			final IStackPresentationSite site) {
		return new Presentation(parent, site);
	}

	@Override
	public StackPresentation createStandaloneViewPresentation(
			final Composite parent, final IStackPresentationSite site,
			final boolean showTitle) {
		return new Presentation(parent, site);
	}
}