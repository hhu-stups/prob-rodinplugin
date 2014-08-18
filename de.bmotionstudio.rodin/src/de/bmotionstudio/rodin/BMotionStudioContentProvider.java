/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


public class BMotionStudioContentProvider implements ITreeContentProvider {

	public Object[] getChildren(final Object parentElement) {

		List<Object> res = new ArrayList<Object>();

		if (parentElement instanceof IProject) {

			final IProject project = (IProject) parentElement;

			if (project.exists()) {

				try {
					for (IResource rs : project.members()) {
						if (rs.getFileExtension() != null
								&& rs.getFileExtension().equals("bmso")) {
							res.add(new BMotionStudioRodinFile(rs));
						}

					}
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}

			return res.toArray(new BMotionStudioRodinFile[res.size()]);

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
