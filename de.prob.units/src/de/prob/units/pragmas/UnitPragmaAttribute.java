/**
 * (c) 2009 Lehrstuhl fuer Softwaretechnik und Programmiersprachen, Heinrich
 * Heine Universitaet Duesseldorf This software is licenced under EPL 1.0
 * (http://www.eclipse.org/org/documents/epl-v10.html)
 * */

package de.prob.units.pragmas;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IVariable;
import org.eventb.core.basis.Constant;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IInternalElement;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.prob.units.Activator;

public class UnitPragmaAttribute implements IAttributeManipulation {
	public static IAttributeType.String ATTRIBUTE = RodinCore
			.getStringAttrType(Activator.PLUGIN_ID + ".unitPragmaAttribute");

	private final String defaultValue = "";

	public UnitPragmaAttribute() {
		// empty constructor
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
	public String[] getPossibleValues(IRodinElement element,
			IProgressMonitor monitor) {
		return null;
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		try {
			return asInternalElement(element).getAttributeValue(ATTRIBUTE);
		} catch (RodinDBException ex) {
			// happens if the attribute is not set on this element
			// just return a default instead of throwing a RodinDBException
		}
		return defaultValue;
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
		asInternalElement(element).setAttributeValue(ATTRIBUTE, defaultValue,
				monitor);
	}

	@Override
	public void setValue(IRodinElement element, String value,
			IProgressMonitor monitor) throws RodinDBException {
		asInternalElement(element).setAttributeValue(ATTRIBUTE, value, monitor);
	}
}
