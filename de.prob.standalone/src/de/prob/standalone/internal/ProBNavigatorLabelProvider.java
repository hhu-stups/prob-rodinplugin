package de.prob.standalone.internal;

/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

import de.prob.standalone.Activator;

public class ProBNavigatorLabelProvider implements ILabelProvider {

	public Image getImage(final Object element) {
		// TODO: (LL) Needs to be improved
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			if (file.getFileExtension().equals("bum")) {
				return Activator.getImageDescriptor("icons/eventb/mch_obj.png")
						.createImage();
			} else if (file.getFileExtension().equals("buc")) {
				return Activator.getImageDescriptor("icons/eventb/ctx_obj.png")
						.createImage();
			} else if (file.getFileExtension().equals("mch")) {
				return Activator.getImageDescriptor("icons/icon16.png")
						.createImage();
			}
		}
		return null;
	}

	public String getText(final Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			return file.getName().replace("." + file.getFileExtension(), "");
		}
		return null;
	}

	public void addListener(final ILabelProviderListener listener) {
		// do nothing
	}

	public void dispose() {
		// do nothing
	}

	public boolean isLabelProperty(final Object element, final String property) {
		return false;
	}

	public void removeListener(final ILabelProviderListener listener) {
		// do nothing
	}

}
