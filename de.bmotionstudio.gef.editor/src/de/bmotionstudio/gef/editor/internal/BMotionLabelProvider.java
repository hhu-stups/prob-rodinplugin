/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.gef.editor.internal;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.bmotionstudio.gef.editor.BMotionStudioImage;

public class BMotionLabelProvider implements ILabelProvider {

	public Image getImage(final Object element) {
		return BMotionStudioImage.getImage(BMotionStudioImage.IMG_LOGO_BMOTION);
	}

	public String getText(final Object element) {

		if (element instanceof BMotionStudioRodinFile)
			return ((BMotionStudioRodinFile) element).getResource().getName()
					.replace(".bmso", "");
		return element.toString();

	}

	public void addListener(final ILabelProviderListener listener) {

	}

	public void dispose() {

	}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {

	}

}
