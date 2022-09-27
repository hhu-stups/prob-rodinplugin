/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.dnd.TemplateTransferDropTargetListener;
import org.eclipse.gef.requests.CreationFactory;

import de.bmotionstudio.gef.editor.BControlCreationFactory;
import de.bmotionstudio.gef.editor.model.Visualization;

public class BControlTransferDropTargetListener extends
		TemplateTransferDropTargetListener {

	private CreationFactory factory = null;
	private BControlTemplate currentTempl;
	private Visualization visualization;

	public BControlTransferDropTargetListener(EditPartViewer viewer,
			Visualization visualization) {
		super(viewer);
		this.visualization = visualization;
	}

	@Override
	protected CreationFactory getFactory(Object template) {

		if (template != null) {

			if (template instanceof BControlTemplate) {

				BControlTemplate templ = (BControlTemplate) template;

				if (factory == null
						|| !templ.getType().equals(currentTempl.getType())) {
					factory = new BControlCreationFactory(templ.getType(),
							visualization);
					currentTempl = templ;
				}

			}

		}

		return factory;

	}

}
