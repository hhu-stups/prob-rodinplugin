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

	private BMotionStudioEditor editor;

	public BMotionSelectionSynchronizer(BMotionStudioEditor editor) {
		this.editor = editor;
	}

	protected EditPart convert(EditPartViewer viewer, EditPart part) {
		EditPart p = super.convert(viewer, part);
		if (viewer instanceof ScrollingGraphicalViewer
				|| viewer instanceof TreeViewer) {
			Object model = part.getModel();
			if (model instanceof BControl) {
				BControl control = (BControl) model;
				String id = control.getID();
				BControl editControl = editor.getEditPage().getVisualization()
						.getBControl(id);
				Object temp = viewer.getEditPartRegistry().get(editControl);
				EditPart newPart = null;
				if (temp != null) {
					newPart = (EditPart) temp;
				}
				return newPart;
			}
		}
		return p;
	}

}
