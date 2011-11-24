/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rodinp.core.IRodinProject;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;


public class BMotionStudioContentProvider implements ITreeContentProvider {

	public Object[] getChildren(final Object parentElement) {

		if (parentElement instanceof IProject) {

			final IProject project = (IProject) parentElement;

			// if it is a RodinProject return the IRodinProject from the DB.
			final IRodinProject proj = RodinCore.valueOf(project);
			if (proj.exists()) {

				try {
					return proj
							.getRootElementsOfType(IBMotionSurfaceRoot.ELEMENT_TYPE);
				} catch (final RodinDBException e) {
					e.printStackTrace();
				}

			}

		}

		return new Object[0];

	}

	public Object getParent(final Object element) {
		// do nothing
		return null;
	}

	public boolean hasChildren(final Object element) {
		return false;
	}

	public Object[] getElements(final Object inputElement) {
		return getChildren(inputElement);
	}

	public void dispose() {
		// do nothing

	}

	public void inputChanged(final Viewer viewer, final Object oldInput,
			final Object newInput) {
		// do nothing
	}

}
