/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.symbolic;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IVariable;
import org.eventb.core.basis.Constant;
import org.eventb.internal.ui.eventbeditor.manipulation.AbstractBooleanManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

public class SymbolicAttribute extends AbstractBooleanManipulation {
	private static final String SYMBOLIC = "symbolic";
	private static final String CONCRETE = "concrete";
	public static IAttributeType.Boolean ATTRIBUTE = RodinCore
			.getBooleanAttrType(Activator.PLUGIN_ID + ".symbolicAttribute");

	public SymbolicAttribute() {
		super(SYMBOLIC, CONCRETE);
	}

	private IInternalElement asInternalElement(IRodinElement element) {
		if (element instanceof IVariable) {
			return (IVariable) element;
		} else if (element instanceof Constant) {
			return (Constant) element;
		}
		return null;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asInternalElement(element).getAttributeValue(ATTRIBUTE) ? SYMBOLIC
				: CONCRETE;
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asInternalElement(element).hasAttribute(ATTRIBUTE);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInternalElement(element).removeAttribute(ATTRIBUTE, monitor);
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asInternalElement(element).setAttributeValue(ATTRIBUTE, false, monitor);

	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		final boolean isSymbolic = value.equals(SYMBOLIC);
		asInternalElement(element).setAttributeValue(ATTRIBUTE, isSymbolic,
				monitor);
	}
}
