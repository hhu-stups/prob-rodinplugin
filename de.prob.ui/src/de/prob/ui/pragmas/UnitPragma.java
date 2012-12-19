package de.prob.ui.pragmas;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eventb.core.IVariable;
import org.eventb.internal.ui.eventbeditor.manipulation.IAttributeManipulation;
import org.rodinp.core.IAttributeType;
import org.rodinp.core.IRodinElement;
import org.rodinp.core.RodinCore;
import org.rodinp.core.RodinDBException;

import de.prob.core.internal.Activator;

public class UnitPragma implements IAttributeManipulation {
	public static IAttributeType.String ATTRIBUTE = RodinCore.getStringAttrType(Activator.PLUGIN_ID + ".unitPragma");
	
	public UnitPragma() {
		// empty constructor
	}

	private IVariable asVariable(IRodinElement element) {
		assert element instanceof IVariable;
		return (IVariable) element;
	}
	
	@Override
	public String[] getPossibleValues(IRodinElement element, IProgressMonitor monitor) {
		return new String[] {"","a","b","c"};
	}

	@Override
	public String getValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asVariable(element).getAttributeValue(ATTRIBUTE);
	}

	@Override
	public boolean hasValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		return asVariable(element).hasAttribute(ATTRIBUTE);
	}

	@Override
	public void removeAttribute(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asVariable(element).removeAttribute(ATTRIBUTE, monitor);
		
	}

	@Override
	public void setDefaultValue(IRodinElement element, IProgressMonitor monitor)
			throws RodinDBException {
		asVariable(element).setAttributeValue(ATTRIBUTE, "",
				monitor);		
	}

	@Override
	public void setValue(IRodinElement element, String value, IProgressMonitor monitor)
			throws RodinDBException {
		asVariable(element).setAttributeValue(ATTRIBUTE, value,
				monitor);		
	}
}
