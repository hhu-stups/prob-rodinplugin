/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class AppEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		AbstractGraphicalEditPart part = null;

		BControl control = (BControl) model;

		if (control instanceof Visualization) {
			part = new VisualizationPart();
		} else {
			try {
				IConfigurationElement configElement = BMotionEditorPlugin
						.getControlServices().get(control.getType());
				if (configElement != null) {
					IBControlService service = (IBControlService) configElement
							.createExecutableExtension("service");
					part = service.createEditPart();
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		if (part != null)
			part.setModel(control);

		// TODO: check if part == null
		return part;

	}

}
