/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;

import de.bmotionstudio.gef.editor.model.BControl;

public class BMotionSelectionSynchronizer extends SelectionSynchronizer {

	protected EditPart convert(EditPartViewer viewer, EditPart part) {
		EditPart p = super.convert(viewer, part);
		if (viewer instanceof ScrollingGraphicalViewer
				|| viewer instanceof TreeViewer) {
			Object model = part.getModel();
			if (model instanceof BControl) {
				Object temp = viewer.getEditPartRegistry().get(model);
				EditPart newPart = null;
				if (temp != null)
					newPart = (EditPart) temp;
				return newPart;
			}
		}
		return p;
	}

}
