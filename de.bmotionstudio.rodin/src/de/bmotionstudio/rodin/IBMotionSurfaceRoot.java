/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.rodinp.core.IInternalElement;
import org.rodinp.core.IInternalElementType;
import org.rodinp.core.RodinCore;

import de.bmotionstudio.gef.editor.BMotionEditorPlugin;

public interface IBMotionSurfaceRoot extends IInternalElement {

	IInternalElementType<IBMotionSurfaceRoot> ELEMENT_TYPE = RodinCore
			.getInternalElementType(BMotionEditorPlugin.PLUGIN_ID
					+ ".BMotionStudioFile"); //$NON-NLS-1$

}
