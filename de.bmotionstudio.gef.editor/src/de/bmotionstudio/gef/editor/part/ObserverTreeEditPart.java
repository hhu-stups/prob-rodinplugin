/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.part;

import de.bmotionstudio.gef.editor.BMotionStudioImage;
import de.bmotionstudio.gef.editor.EditorImageRegistry;
import de.bmotionstudio.gef.editor.observer.Observer;

public class ObserverTreeEditPart extends BMSAbstractTreeEditPart {

	@Override
	public void refreshVisuals() {
		Observer o = (Observer) getModel();
		setWidgetText(o.getName());
		setWidgetImage(BMotionStudioImage
				.getImage(EditorImageRegistry.IMG_ICON_OBSERVER));
	}

}
