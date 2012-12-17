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

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;
import de.bmotionstudio.gef.editor.IBControlService;
import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class BMSTreeEditPartFactory implements EditPartFactory {

	@Override
	public EditPart createEditPart(EditPart context, Object model) {

		BMSAbstractTreeEditPart part = null;

		if (model instanceof Visualization) {
			part = new BControlTreeEditPart();
		}
		else if (model instanceof BControl) {

			BControl control = (BControl) model;

			try {
				IConfigurationElement configElement = BMotionEditorPlugin
						.getControlServices().get(control.getType());
				if (configElement != null) {
					IBControlService service = (IBControlService) configElement
							.createExecutableExtension("service");
					part = service.createTreeEditPart();
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}

		}
		// else if (model instanceof Observer) {
		// part = new ObserverTreeEditPart();
		// } else if (model instanceof ObserverRootVirtualTreeNode) {
		// part = new ObserverRootTreeEditpart();
		// }

		if (part != null)
			part.setModel(model);

		System.out.println("CREATE EDIT PART!!! " + part);

		return part;

	}

}
