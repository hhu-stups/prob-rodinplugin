/** 
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, 
 * Heinrich Heine Universitaet Duesseldorf
 * This software is licenced under EPL 1.0 (http://www.eclipse.org/org/documents/epl-v10.html) 
 * */

package de.bmotionstudio.rodin;

import org.rodinp.core.IInternalElementType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinDBException;
import org.rodinp.core.basis.InternalElement;
import org.rodinp.core.basis.RodinElement;



public class BMotionSurfaceRoot extends InternalElement implements
		IBMotionSurfaceRoot {

	public BMotionSurfaceRoot(final String name, final IRodinElement parent) {
		super(name, parent);
	}

	@Override
	public RodinElement[] getChildren() throws RodinDBException {
		return NO_ELEMENTS;
	}

	@Override
	public IInternalElementType<IBMotionSurfaceRoot> getElementType() {
		return ELEMENT_TYPE;
	}

}
