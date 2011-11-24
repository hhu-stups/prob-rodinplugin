/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.requests.CreationFactory;

import de.bmotionstudio.gef.editor.model.BControl;
import de.bmotionstudio.gef.editor.model.Visualization;

public class BControlCreationFactory implements CreationFactory {

	private Visualization visualization;
	private String type;

	public BControlCreationFactory(String type, Visualization visualization) {
		this.type = type;
		this.visualization = visualization;
	}

	@Override
	public Object getNewObject() {
		BControl control = null;
		try {
			IBControlService service = (IBControlService) BMotionEditorPlugin
					.getControlServices().get(type)
					.createExecutableExtension("service");
			control = service.createControl(visualization);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		// TODO: check if control == null
		return control;
	}

	@Override
	public Object getObjectType() {
		return BControl.class;
	}

}
